package org.alexdev.kepler.game.games;

import org.alexdev.kepler.game.GameScheduler;
import org.alexdev.kepler.game.games.battleball.events.PlayerMoveEvent;
import org.alexdev.kepler.game.games.enums.GameState;
import org.alexdev.kepler.game.games.enums.GameType;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.games.player.GameTeam;
import org.alexdev.kepler.game.games.utils.FinishedGame;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.models.RoomModel;
import org.alexdev.kepler.log.Log;
import org.alexdev.kepler.messages.outgoing.games.*;
import org.alexdev.kepler.messages.outgoing.messenger.ROOMFORWARD;
import org.alexdev.kepler.messages.types.MessageComposer;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.schedule.FutureRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Game {
    private int id;
    private int mapId;
    private int teamAmount;

    private String gameCreatorName;
    private int gameCreatorId;

    private GameType gameType;
    private GameState gameState;

    private Room room;
    private RoomModel roomModel;

    private String name;
    private Map<Integer, GameTeam> teams;

    private List<Player> observers;
    private List<GamePlayer> spectators;

    private BlockingQueue<GameEvent> eventsQueue;
    private BlockingQueue<GameObject> objectsQueue;

    private List<GameObject> objects;
    private List<GameEvent> events;

    private AtomicInteger preparingGameSecondsLeft;
    private AtomicInteger totalSecondsLeft;
    private AtomicLong restartCountdown;
    private AtomicInteger objectId;

    private FutureRunnable preparingTimerRunnable;
    private FutureRunnable gameTimerRunnable;
    private FutureRunnable restartRunnable;

    private boolean gameStarted;
    private boolean gameFinished;

    private int regenerateMapInterval;
    private boolean regenerateMapEnabled;

    public Game(int id, int mapId, GameType gameType, String name, int teamAmount, Player gameCreator) {
        this.id = id;
        this.mapId = mapId;
        this.gameType = gameType;
        this.name = name;
        this.teamAmount = teamAmount;
        this.gameCreatorName = gameCreator.getDetails().getName();
        this.gameCreatorId = gameCreator.getDetails().getId();
        this.teams = new ConcurrentHashMap<>();

        this.spectators = new CopyOnWriteArrayList<>();
        this.observers = new CopyOnWriteArrayList<>();

        this.eventsQueue = new LinkedBlockingQueue<>();
        this.objectsQueue = new LinkedBlockingQueue<>();

        this.objects = new CopyOnWriteArrayList<>();
        this.events = new CopyOnWriteArrayList<>();

        for (int i = 0; i < teamAmount; i++) {
            this.teams.put(i, new GameTeam(i, this));
        }

        this.objectId = new AtomicInteger(0);
        this.gameState = GameState.WAITING;

        this.regenerateMapEnabled = GameConfiguration.getInstance().getBoolean("regenerate.map.enabled");
        this.regenerateMapInterval = GameConfiguration.getInstance().getInteger("regenerate.map.interval");
    }

    public void reassignGameId() {
        this.id = GameManager.getInstance().getGameCounter().getAndIncrement();
    }

    /**
     * Method to initialise the game
     */
    public void initialise() {
        this.initialise(GameManager.getInstance().getLifetimeSeconds(this.gameType), "Battleball Arena", GameManager.getInstance().getModel(this.gameType, this.mapId));
    }

    public void initialise(int lifetimeSeconds, String roomName, RoomModel model) {
        this.gameState = GameState.STARTED;

        this.preparingGameSecondsLeft = new AtomicInteger(GameManager.getInstance().getPreparingSeconds(this.gameType));
        this.totalSecondsLeft = new AtomicInteger(lifetimeSeconds);

        if (model != null) {
            this.roomModel = model;
        }

        if (this.room == null) {
            this.room = new Room();
            this.room.getData().fill(0, roomName, "");
            this.room.setRoomModel(this.getRoomModel());
            this.room.getData().setGame(this);
            this.room.setGameArena(true);

            //this.room.getData().setCategoryId(NavigatorManager.getInstance().getCategories().values().stream().findFirst().get().getId());
            this.room.getData().setGameArena(true);
            this.room.getData().setGameLobby(this.gameType.getLobbyModel());
        }

        this.objects.clear();
        this.events.clear();
        this.room.getMapping().regenerateCollisionMap();
        this.buildMap();
    }

    /**
     * Method to start the game
     */
    public void startGame() {
        this.initialise();
        this.assignSpawnPoints();

        var game = this;

        for (GamePlayer p : this.getActivePlayers()) {
            p.setEnteringGame(true); // Set to true so when they leave the lobby, the server knows to initialise the user when they join the arena
        }

        this.send(new GAMELOCATION());
        this.sendSpectatorsToArena();

        /*while (true) {
            if (this.getPlayers().stream().allMatch(g -> g.getPlayer().getRoomUser().getRoom() != null)) {
                this.send(new FULLGAMESTATUS(this));
                break;
            }
        }*/

        // Preparing game seconds countdown
        this.preparingTimerRunnable = new FutureRunnable() {
            public void run() {
                try {
                    gamePrepareTick();

                    if (getActivePlayers().isEmpty()) {
                        GameManager.getInstance().getGames().remove(game);
                        killSpectators();
                        this.cancelFuture();
                        return;
                    }

                    if (preparingGameSecondsLeft.getAndDecrement() == 0) {
                        this.cancelFuture();
                        beginGame();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        var future = GameScheduler.getInstance().getService().scheduleAtFixedRate(this.preparingTimerRunnable, 0, 1, TimeUnit.SECONDS);
        this.preparingTimerRunnable.setFuture(future);

        this.sendObservers(new GAMEINSTANCE(this));
        this.gamePrepare();
    }

    /**
     * Method for when the game begins after the initial preparing game seconds timer
     */
    private void beginGame() {
        this.gameStarted = true;

        // Stop all players from walking when game starts if they selected a tile
        for (GamePlayer p : this.getActivePlayers()) {
            p.getPlayer().getRoomUser().setWalkingAllowed(true);
        }

        // Send game seconds
        this.send(new GAMESTART(this.totalSecondsLeft.get()));

        // Regenerate collision map when game starts
        this.room.getMapping().regenerateCollisionMap();

        // Game seconds counter
        this.gameTimerRunnable = new FutureRunnable() {
            public void run() {
                try {
                    gameTick();

                    if (regenerateMapEnabled) {
                        if (totalSecondsLeft.get() % regenerateMapInterval == 0) {
                            room.getMapping().regenerateCollisionMap();
                        }
                    }

                    // Game ends either when time runs out or there's no free tiles left to seal
                    if (totalSecondsLeft.decrementAndGet() == 0 || !canTimerContinue() || getActivePlayers().isEmpty()) {
                        this.cancelFuture();
                        finishGame();
                    }

                } catch (Exception ex) {
                    Log.getErrorLogger().error("Error occurred in game timer runnable: ", ex);
                }
            }
        };

        var future = GameScheduler.getInstance().getService().scheduleAtFixedRate(this.gameTimerRunnable, 0, 1, TimeUnit.SECONDS);
        this.gameTimerRunnable.setFuture(future);

        gameStarted();
    }

    /**
     * Finish game
     */
    private void finishGame() {
        Game instance = this;
        this.gameStarted = false;
        this.gameFinished = true;
        this.gameState = GameState.ENDED;

        FinishedGame finishedGame = new FinishedGame(this);
        GameManager.getInstance().getFinishedGames().add(finishedGame);

        // Stop all players from walking when game starts if they selected a tile
        for (GamePlayer p : this.getActivePlayers()) {
            p.getPlayer().getRoomUser().setWalkingAllowed(false);
        }

        if (this.canIncreasePoints()) {
            CompletableFuture.runAsync(new GameFinishTask(this.gameType, this.getActivePlayers(), this.getTeams()));
        }

        // Send scores to everybody
        this.send(new GAMEEND(this.gameType, this.teams));
        this.gameEnded();

        // Restart countdown
        this.restartCountdown = new AtomicLong(GameManager.getInstance().getRestartSeconds(this.gameType));

        this.restartRunnable = new FutureRunnable() {
            public void run() {
                if (!hasEnoughPlayers() || gameState != GameState.ENDED) {
                    this.cancelFuture();
                    return;
                }

                List<GamePlayer> afkPlayers = new ArrayList<>(); // Players who didn't touch any button

                for (GamePlayer p : instance.getActivePlayers()) {
                    if (!p.isClickedRestart()) {
                        afkPlayers.add(p);
                    }
                }

                if (restartCountdown.decrementAndGet() == 0) {
                    if (afkPlayers.size() > 0) { // Only call this if not everyone clicked play again, else GAMERESTART.java handles it
                        triggerRestart();
                    }

                    this.cancelFuture();
                }
            }
        };

        var future = GameScheduler.getInstance().getService().scheduleAtFixedRate(this.restartRunnable, 0, 1, TimeUnit.SECONDS);
        this.restartRunnable.setFuture(future);

        this.sendObservers(new GAMEINSTANCE(finishedGame));
        this.observers.clear();
    }

    /**
     * Restarts all the new players who clicked to play the next game.
     */
    public void triggerRestart() {
        List<GamePlayer> players = new ArrayList<>(); // Players who wanted to restart
        List<GamePlayer> afkPlayers = new ArrayList<>(); // Players who didn't touch any button

        for (GamePlayer p : this.getActivePlayers()) {
            if (!p.isClickedRestart()) {
                afkPlayers.add(p);
            } else {
                p.setClickedRestart(false); // Reset whether or not they clicked restart, for next game
                players.add(p);
            }
        }

        // Only create a new game if there's two players who joined
        if (players.size() >= GameConfiguration.getInstance().getInteger(this.gameType.name().toLowerCase() + ".start.minimum.active.teams")) {
            this.restartGame(players);
        } else {
            afkPlayers.addAll(players);
        }

        // Send spectators to lobby too
        afkPlayers.addAll(this.spectators);

        for (var afkPlayer : afkPlayers) {
            this.sendToLobby(afkPlayer);
        }
    }

    /**
     * Method to restart game.
     */
    public void restartGame(List<GamePlayer> players) {
        if (this.restartRunnable != null) {
            this.restartRunnable.cancelFuture();
        }

        if (this.preparingTimerRunnable != null) {
            this.preparingTimerRunnable.cancelFuture();
        }

        if (this.gameTimerRunnable != null) {
            this.gameTimerRunnable.cancelFuture();
        }

        for (GameTeam gameTeam : this.teams.values()) {
            gameTeam.getPlayers().clear();
        }

        for (var gamePlayer : players) {
            this.movePlayer(gamePlayer, -1, gamePlayer.getTeamId());
            gamePlayer.getPlayer().getRoomUser().setWalkingAllowed(false); // Don't allow them to walk, for next game
        }

        this.initialise();
        this.assignSpawnPoints();

        for (GamePlayer gamePlayer : this.getActivePlayers()) {
            gamePlayer.getPlayer().getRoomUser().setPosition(gamePlayer.getSpawnPosition());
        }

        this.send(new GAMERESET(GameManager.getInstance().getPreparingSeconds(this.gameType), players));
        this.send(new FULLGAMESTATUS(this));  // Show users back at spawn positions
        this.sendObservers(new GAMEDELETED(this.id));

        // Preparing game seconds countdown
        this.preparingTimerRunnable = new FutureRunnable() {
            public void run() {
                try {
                    if (!hasEnoughPlayers()) {
                        this.cancelFuture();
                        return;
                    }

                    gamePrepareTick();

                    if (preparingGameSecondsLeft.getAndDecrement() == 0) {
                        this.cancelFuture();
                        beginGame();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        var future = GameScheduler.getInstance().getService().scheduleAtFixedRate(this.preparingTimerRunnable, 1, 1, TimeUnit.SECONDS);
        this.preparingTimerRunnable.setFuture(future);

        this.gamePrepare();
    }

    /**
     * Make the player leave the game, abort the game if the owner leaves
     *
     * @param gamePlayer the game player to leave
     */
    public void leaveGame(GamePlayer gamePlayer) {
        boolean isSpectator = this.spectators.contains(gamePlayer);

        this.spectators.remove(gamePlayer);
        this.objects.remove(gamePlayer.getGameObject());

        gamePlayer.getPlayer().getRoomUser().setGamePlayer(null);
        gamePlayer.getPlayer().send(new GAMEDELETED(this.id));

        if (!isSpectator) {
            this.movePlayer(gamePlayer, gamePlayer.getTeamId(), -1);
        } else {
            this.send(new GAMEINSTANCE(this));
            this.sendObservers(new GAMEINSTANCE(this));
        }

        if (this.gameState == GameState.WAITING && this.gameCreatorId == gamePlayer.getPlayer().getDetails().getId() || this.getActivePlayers().isEmpty()) {
            GameManager.getInstance().getGames().remove(this);

            for (var player : this.getTotalPlayers()) {
                player.getPlayer().getRoomUser().setGamePlayer(null);
            }

            for (var player : this.getSpectators()) {
                player.getPlayer().getRoomUser().setGamePlayer(null);
            }

            for (var player : this.getObservers()) {
                player.getRoomUser().setObservingGameId(0);
            }

            this.send(new GAMEDELETED(this.id));
            this.sendObservers(new GAMEDELETED(this.id));
            this.killSpectators();
        }

        gamePlayer.getPlayer().getRoomUser().setGamePlayer(null);
        gamePlayer.setGameId(-1);
    }

    /**
     * Send all spectators to arena, happens when game starts after waiting for players.
     */
    private void sendSpectatorsToArena() {
        for (GamePlayer spectator : this.spectators) {
            this.sendSpectatorToArena(spectator);
        }
    }

    /**
     * Send spectaot to arena
     *
     * @param spectator the spectator to send
     */
    public void sendSpectatorToArena(GamePlayer spectator) {
        if (!spectator.isSpectator()) {
            return;
        }

        if (spectator.getPlayer().getRoomUser().getRoom() != this.room) {
            spectator.getPlayer().send(new GAMELOCATION());
            spectator.setEnteringGame(true);

            // No longer an observer
            this.observers.remove(spectator.getPlayer());
        }
    }

    /**
     * Terminate all spectators, and send them to the lobby.
     */
    private void killSpectators() {
        for (GamePlayer spectator : this.spectators) {
            this.sendToLobby(spectator);
        }

        this.spectators.clear();
    }

    /**
     * Send players to the game lobby, will make them leave the game.
     *
     * @param gamePlayer the player to send
     */
    public void sendToLobby(GamePlayer gamePlayer) {
        Player player = gamePlayer.getPlayer();

        if (player.getRoomUser().getRoom() != this.room) {
            return; // Don't force people to go to a room they didn't request
        }

        if (player.getRoomUser().getRoom() != null) {
            player.getRoomUser().getRoom().getEntityManager().leaveRoom(player, false);
        } else {
            this.leaveGame(gamePlayer);
        }

        player.send(new ROOMFORWARD(true, RoomManager.getInstance().getRoomByModel(this.gameType.getLobbyModel()).getId()));
    }

    /**
     * Get the lobby for this game.
     *
     * @return the room lobby
     */
    public Room getLobby() {
        return RoomManager.getInstance().getRoomByModel(this.gameType.getLobbyModel());
    }

    /**
     * Moves a player from one team to another team.
     *
     * @param gamePlayer the player to move
     * @param fromTeamId the team to move from, -1 if just to add to team
     * @param toTeamId   the team to move to, -1 if just removing user from team
     */
    public void movePlayer(GamePlayer gamePlayer, int fromTeamId, int toTeamId) {
        if (fromTeamId != -1) {
            if (this.gameState == GameState.WAITING || this.gameState == GameState.ENDED) {
                this.teams.get(fromTeamId).getPlayers().remove(gamePlayer);
            }

            gamePlayer.setInGame(false); // Leaving team so they're not in game
        }

        if (toTeamId != -1) {
            if (!this.teams.get(toTeamId).getPlayers().contains(gamePlayer)) {
                this.teams.get(toTeamId).getPlayers().add(gamePlayer);
            }

            gamePlayer.setTeamId(toTeamId);
            gamePlayer.setInGame(true); // Entering team so they're in game
        } else {
            if (this.gameState == GameState.WAITING || this.gameState == GameState.ENDED) {
                this.teams.get(gamePlayer.getTeamId()).getPlayers().remove(gamePlayer);
            } else {
                gamePlayer.setInGame(false); // Don't remove from team, just show they're no longer in game, for "0" score at the end.
            }

            gamePlayer.getPlayer().getRoomUser().setGamePlayer(null);
        }

        this.send(new GAMEINSTANCE(this));
        this.sendObservers(new GAMEINSTANCE(this));
    }

    /**
     * Send a packet to all members in game
     *
     * @param composer the composer to send
     */
    public void send(MessageComposer composer) {
        for (GamePlayer gamePlayer : this.getActivePlayers()) {
            gamePlayer.getPlayer().send(composer);
        }

        for (GamePlayer player : this.spectators) {
            player.getPlayer().send(composer);
        }
    }

    /**
     * Send a packet (game status) to all observers
     *
     * @param composer the composer to send
     */
    public void sendObservers(MessageComposer composer) {
        for (Player player : this.observers) {
            player.send(composer);
        }
    }

    /**
     * Get if there is enough space for this user to switch to the team
     *
     * @param teamId the team id to check for
     * @return true, if successful
     */
    public boolean canSwitchTeam(int teamId) {
        int maxPerTeam = 0;

        if (this.teamAmount == 1) {
            maxPerTeam = 10;
        } else if (this.teamAmount == 2) {
            maxPerTeam = 5;
        } else if (this.teamAmount == 3) {
            maxPerTeam = 3;
        } else if (this.teamAmount == 4) {
            maxPerTeam = 2;
        }

        return this.teams.get(teamId).getActivePlayers().size() <= maxPerTeam;
    }

    public int getMaxPlayers() {
        int maxPerTeam = 0;

        if (this.teamAmount == 1) {
            maxPerTeam = 10;
        } else if (this.teamAmount == 2) {
            maxPerTeam = 5;
        } else if (this.teamAmount == 3) {
            maxPerTeam = 3;
        } else if (this.teamAmount == 4) {
            maxPerTeam = 2;
        }

        return maxPerTeam * this.teamAmount;
    }

    /**
     * Get whether points can be increased or not
     *
     * @return true, if successful
     */
    public boolean canIncreasePoints() {
        return GameConfiguration.getInstance().getBoolean(this.gameType.name().toLowerCase() + ".increase.points");
    }

    /**
     * Gets if there's enough players in different teams for the game to start
     * (minimum of 2 players)
     *
     * @return true, if successful
     */
    public boolean canGameStart() {
        if (this.gameType == GameType.SNOWSTORM && this.teamAmount == 1) {
            return this.getActivePlayers().size() > 1;
        } else {
            int activeTeamCount = 0;

            for (int i = 0; i < this.teamAmount; i++) {
                if (this.teams.get(i).getActivePlayers().size() > 0) {
                    activeTeamCount++;
                }
            }

            return activeTeamCount >= GameConfiguration.getInstance().getInteger(this.gameType.name().toLowerCase() + ".start.minimum.active.teams");
        }
    }

    /**
     * Gets if there's enough players in different teams for the game to continue
     * (minimum of 1 players)
     *
     * @return true, if successful
     */
    public abstract boolean hasEnoughPlayers();

    /**
     * Get the game player instance by id
     *
     * @param userId the id to get the player by
     * @return the game player instance, else if null
     */
    public GamePlayer getGamePlayer(int userId) {
        for (GamePlayer gamePlayer : this.getActivePlayers()) {
            if (gamePlayer.getUserId() == userId) {
                return gamePlayer;
            }
        }

        return null;
    }

    /**
     * Get a tile by specified coordinates
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the battleball tile, if successful
     */
    public GameTile getTile(int x, int y) {
        if (x < 0 || y < 0) {
            return null;
        }

        if (x >= this.roomModel.getMapSizeX() || y >= roomModel.getMapSizeY() || y >= getTileMap().length || x >= getTileMap()[0].length) {
            return null;
        }

        return this.getTileMap()[x][y];
    }

    /**
     * Method to create object ids.
     *
     * @return the new object ids
     */
    public int createObjectId() {
        return this.objectId.incrementAndGet();
    }

    public void addObjectToQueue(GameObject object) {
        this.objectsQueue.removeIf(obj -> obj.getId() == object.getId());
        this.objectsQueue.add(object);
    }

    public void addPlayerMove(PlayerMoveEvent event) {
        this.eventsQueue.removeIf(e -> e instanceof PlayerMoveEvent && ((PlayerMoveEvent) e).getGamePlayer() == event.getGamePlayer());
        this.eventsQueue.add(event);
    }

    /**
     * Method for whether the game can continue, eg if all tiles are filled up or
     * some other logic
     *
     * @return true, if game has finished
     */
    public abstract boolean canTimerContinue();

    /**
     * Assign spawn points to all team members
     */
    public abstract void assignSpawnPoints();

    /**
     * Method to get the tile map
     */
    public abstract GameTile[][] getTileMap();

    /**
     * Handler for building map
     */
    public abstract void buildMap();

    /**
     * Method called when game is ticked
     */
    public abstract void gameTick();

    /**
     * Method called when the game initially began
     */
    public void gamePrepare() {
        for (GameTeam gameTeam : this.getTeams().values()) {
            gameTeam.setScore(0);
        }

        for (GamePlayer gamePlayer : this.getActivePlayers()) {
            gamePlayer.setScore(0);
        }
    }

    /**
     * Method called for the tick in game beginning
     */
    public void gamePrepareTick() {
    }

    /**
     * Method called when the game initially started
     */
    public void gameStarted() {
    }

    /**
     * Method called when the game ends, when the scoreboard shows
     */
    public void gameEnded() {
    }

    public List<GamePlayer> getActivePlayers() {
        List<GamePlayer> gamePlayers = new ArrayList<>();

        for (GameTeam team : this.teams.values()) {
            gamePlayers.addAll(team.getActivePlayers());
        }

        return gamePlayers;
    }

    public List<GamePlayer> getTotalPlayers() {
        List<GamePlayer> gamePlayers = new ArrayList<>();

        for (GameTeam team : this.teams.values()) {
            gamePlayers.addAll(team.getActivePlayers());
        }

        return gamePlayers;
    }

    /**
     * Get the cost of tickets required to play this game
     *
     * @return the ticket amount required
     */
    public int getTicketCost() {
        return getTicketCost(this.getGameType());
    }

    /**
     * Get the cost of tickets required to play each game
     *
     * @param gameType the type of game to get the ticket cost for
     * @return the ticket amount required
     */
    public static int getTicketCost(GameType gameType) {
        return GameConfiguration.getInstance().getInteger(gameType.name().toLowerCase() + ".ticket.charge");
    }

    /**
     * Get the list of specators, the people currently watching the game
     *
     * @return the list of spectators
     */
    public List<GamePlayer> getSpectators() {
        return spectators;
    }

    /**
     * @return
     */
    public List<Player> getObservers() {
        return observers;
    }

    /**
     * Get the game id
     *
     * @return the game id
     */
    public int getId() {
        return id;
    }

    public GameType getGameType() {
        return gameType;
    }

    public GameState getGameState() {
        return gameState;
    }

    public String getName() {
        return name;
    }

    public int getTeamAmount() {
        return teamAmount;
    }

    public Map<Integer, GameTeam> getTeams() {
        return teams;
    }

    public String getGameCreator() {
        return this.gameCreatorName;
    }

    public int getMapId() {
        return mapId;
    }

    public AtomicInteger getPreparingGameSecondsLeft() {
        return preparingGameSecondsLeft;
    }

    public AtomicInteger getTotalSecondsLeft() {
        return totalSecondsLeft;
    }

    public RoomModel getRoomModel() {
        return roomModel;
    }

    public Room getRoom() {
        return room;
    }

    public boolean isGameStarted() {
        return gameStarted;
    }

    public boolean isGameFinished() {
        return gameFinished;
    }

    public int getGameCreatorId() {
        return gameCreatorId;
    }

    public BlockingQueue<GameEvent> getEventsQueue() {
        return eventsQueue;
    }

    public BlockingQueue<GameObject> getObjectsQueue() {
        return objectsQueue;
    }

    public List<GameObject> getObjects() {
        return objects;
    }

    public void setRoomModel(RoomModel roomModel) {
        this.roomModel = roomModel;
    }

    public AtomicInteger getObjectId() {
        return objectId;
    }

    public void setObjectId(AtomicInteger objectId) {
        this.objectId = objectId;
    }

    public FutureRunnable getRestartRunnable() {
        return restartRunnable;
    }
}
