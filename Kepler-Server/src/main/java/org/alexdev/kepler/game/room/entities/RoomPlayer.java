package org.alexdev.kepler.game.room.entities;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.PlayerDao;
import org.alexdev.kepler.game.games.Game;
import org.alexdev.kepler.game.games.GameManager;
import org.alexdev.kepler.game.games.player.GamePlayer;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.room.RoomManager;
import org.alexdev.kepler.game.room.managers.RoomTradeManager;
import org.alexdev.kepler.game.triggers.GameLobbyTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.user.FIGURE_CHANGE;
import org.alexdev.kepler.messages.outgoing.user.USER_OBJECT;

import java.util.ArrayList;
import java.util.List;

public class RoomPlayer extends RoomEntity {
    private Player player;

    private int authenticateId;
    private int authenticateTelporterId;
    private int observingGameId;

    private boolean isTyping;
    private boolean isDiving;

    private Player tradePartner;
    private List<Item> tradeItems;
    private boolean tradeAccept;

    private GamePlayer gamePlayer;
    private String currentGameId;

    public RoomPlayer(Player player) {
        super(player);
        this.player = player;
        this.authenticateId = -1;
        this.authenticateTelporterId = -1;
        this.tradeItems = new ArrayList<>();
    }

    @Override
    public void reset() {
        super.reset();
        this.isTyping = false;
        this.isDiving = false;
        this.observingGameId = -1;
        RoomTradeManager.close(this);
    }

    @Override
    public boolean walkTo(int X, int Y) {
        boolean walking = super.walkTo(X, Y);

        if (walking) {
            this.getTimerManager().resetRoomTimer();
        }

        return walking;
    }

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
        this.player.send(new USER_OBJECT(this.player.getDetails()));

        // Send refresh to room if inside room
        if (this.getRoom() != null) {
            this.getRoom().send(new FIGURE_CHANGE(this.getInstanceId(), this.player.getDetails()));

            if (this.getRoom().getModel().getModelTrigger() instanceof GameLobbyTrigger) {
                GameLobbyTrigger gameLobbyTrigger = (GameLobbyTrigger) getRoom().getModel().getModelTrigger();
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
}