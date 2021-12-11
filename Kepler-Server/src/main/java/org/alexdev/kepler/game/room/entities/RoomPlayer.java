package org.alexdev.kepler.game.room.entities;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.managers.RoomTradeManager;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.user.CHAT_MESSAGE;
import org.alexdev.kepler.messages.outgoing.rooms.user.FIGURE_CHANGE;
import org.alexdev.kepler.messages.outgoing.user.USER_OBJECT;
import org.alexdev.kepler.util.DateUtil;

import java.util.ArrayList;
import java.util.List;

public class RoomPlayer extends RoomEntity {
    private Player player;

    private int authenticateId;
    private int authenticateTelporterId;
    private int observingGameId;
    private int lidoVote;

    private boolean isTyping;
    private boolean isDiving;

    private Player tradePartner;
    private List<Item> tradeItems;
    private boolean tradeAccept;

    private GamePlayer gamePlayer;
    private String currentGameId;

    private int chatSpamCount = 0;
    private int chatSpamTicks = 16;
    private long muteTime;

    public RoomPlayer(Player player) {
        super(player);
        this.player = player;
        this.authenticateId = -1;
        this.authenticateTelporterId = -1;
        this.tradeItems = new ArrayList<>();
    }

    public void handleSpamTicks() {
        if (this.chatSpamTicks >= 0) {
            this.chatSpamTicks--;

            if (this.chatSpamTicks == -1) {
                this.chatSpamCount = 0;
            }
        }
    }

    @Override
    public void talk(String message, CHAT_MESSAGE.ChatMessageType chatMessageType, List<Player> recieveMessages) {
        if (message.endsWith("o/")) {
            this.wave();

            if (message.equals("o/")) {
                return; // Don't move mouth if it's just a wave
            }
        }

        this.chatSpamCount++;

        if (this.chatSpamTicks == -1)
            this.chatSpamTicks = 8;

        if (this.chatSpamCount >= 6) {
            this.muteTime = DateUtil.getCurrentTimeSeconds() + 30;
        }

        if (this.muteTime > DateUtil.getCurrentTimeSeconds()) {
            return;
        }

        super.talk(message, chatMessageType, recieveMessages);
    }

    @Override
    public void reset() {
        super.reset();
        this.isTyping = false;
        this.isDiving = false;
        this.observingGameId = -1;
        this.lidoVote = 0;
        RoomTradeManager.close(this);
    }

    /*
    @Override
    public boolean walkTo(int X, int Y) {
        if (this.getRoom() != null && (this.getRoom().getModel().getName().startsWith("pool_a") || (this.getRoom().getModel().getName().equals("md_a")))) {
            if (this.getGoal() != null && !this.getGoal().equals(this.getPosition())) {
                if ((X == 20 && Y == 28) ||
                        (X == 21 && Y == 28) ||
                        (X == 17 && Y == 19) ||
                        (X == 17 && Y == 21) ||
                        (X == 17 && Y == 22) ||
                        (X == 31 && Y == 11) ||
                        (X == 31 && Y == 10)) {
                    return walkTo(this.getGoal().getX(), this.getGoal().getY());
                }
            }
        }

        boolean walking = super.walkTo(X, Y);

        if (walking) {
            this.getTimerManager().resetRoomTimer();
        }

        return walking;
    }*/

    @Override
    public void stopWalking() {
        super.stopWalking();

        Item item = this.getCurrentItem();

        if (item != null) {
            // Kick out user from teleporter if link is broken
            if (item.hasBehaviour(ItemBehaviour.TELEPORTER)) {
                Item linkedTeleporter = ItemDao.getItem(item.getTeleporterId());

                if (linkedTeleporter == null || RoomManager.getInstance().getRoomById(linkedTeleporter.getRoomId()) == null) {
                    item.setCustomData("TRUE");
                    item.updateStatus();

                    player.getRoomUser().walkTo(item.getPosition().getSquareInFront().getX(), item.getPosition().getSquareInFront().getY());
                    player.getRoomUser().setWalkingAllowed(true);
                    return;
                }
            }
        }
    }

    @Override
    public void kick(boolean allowWalking) {
        super.kick(allowWalking);

        // Remove authentications
        this.authenticateId = -1;
        this.authenticateTelporterId = -1;
    }

    public void stopObservingGame() {
        if (this.observingGameId != -1) {
            Game game = GameManager.getInstance().getGameById(this.observingGameId);

            if (game != null) {
                game.getObservers().remove(this.player);
            }
        }
    }

    /**
     * Refreshes user appearance
     */
    public void refreshAppearance() {
        var newDetails = PlayerDao.getDetails(this.player.getDetails().getId());

        // Reload figure, gender and motto
        this.player.getDetails().setFigure(newDetails.getFigure());
        this.player.getDetails().setSex(newDetails.getSex());
        this.player.getDetails().setMotto(newDetails.getMotto());

        // Send refresh to user
        this.player.send(new USER_OBJECT(this.player.getVersion(), this.player.getDetails()));

        // Send refresh to room if inside room
        if (this.getRoom() != null) {
            this.getRoom().send(new FIGURE_CHANGE(this.getInstanceId(), this.player.getDetails()));

            if (this.getRoom().getModel().getRoomTrigger() instanceof GameLobbyTrigger) {
                GameLobbyTrigger gameLobbyTrigger = (GameLobbyTrigger) getRoom().getModel().getRoomTrigger();
                gameLobbyTrigger.showPoints(this.player, this.getRoom());
            }
        }
    }

    public int getAuthenticateId() {
        return authenticateId;
    }

    public void setAuthenticateId(int authenticateId) {
        this.authenticateId = authenticateId;
    }

    public int getAuthenticateTelporterId() {
        return authenticateTelporterId;
    }

    public void setAuthenticateTelporterId(int authenticateTelporterId) {
        this.authenticateTelporterId = authenticateTelporterId;
    }

    public boolean isTyping() {
        return isTyping;
    }

    public void setTyping(boolean typing) {
        isTyping = typing;
    }

    public boolean isDiving() {
        return isDiving;
    }

    public void setDiving(boolean diving) {
        isDiving = diving;
    }

    public Player getTradePartner() {
        return tradePartner;
    }

    public void setTradePartner(Player tradePartner) {
        this.tradePartner = tradePartner;
    }

    public List<Item> getTradeItems() {
        return tradeItems;
    }

    public boolean hasAcceptedTrade() {
        return tradeAccept;
    }

    public void setTradeAccept(boolean tradeAccept) {
        this.tradeAccept = tradeAccept;
    }

    public String getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(String currentGameId) {
        this.currentGameId = currentGameId;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public int getObservingGameId() {
        return observingGameId;
    }

    public int resetObservingGameId() {
        return observingGameId;
    }

    public void setObservingGameId(int observingGameId) {
        this.observingGameId = observingGameId;
    }

    public int getLidoVote() {
        return lidoVote;
    }

    public void setLidoVote(int lidoVote) {
        this.lidoVote = lidoVote;
    }
}