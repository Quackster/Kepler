package org.alexdev.kepler.game.infostand;

import org.alexdev.kepler.dao.mysql.InfoStandDao;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerRank;

import java.util.ArrayList;
import java.util.List;

public class InfoStand {
    public static final String DEFAULT_EXPRESSION = "std";
    public static final String DEFAULT_ACTION = "std";
    public static final int DEFAULT_DIRECTION = 4;
    public static final int DEFAULT_FURNI = 0;
    public static final int DEFAULT_PLATE = 0;

    private final Player player;
    private InfoStandActive active;
    private List<Integer> plates;
    private List<Integer> furnis;
    private List<String> expressions;
    private List<String> actions;
    private List<Integer> directions;

    public InfoStand(Player player) {
        this.player = player;
        this.reload();
    }

    public void reload() {
        this.active = InfoStandDao.getUserActive(this.player.getDetails().getId());

        final List<InfoStandEntry> props = InfoStandDao.getUserOwned(this.player.getDetails().getId());

        this.plates = new ArrayList<>();
        this.furnis = new ArrayList<>();
        this.expressions = new ArrayList<>();
        this.actions = new ArrayList<>();
        this.directions = new ArrayList<>();

        if (this.player.getDetails().getRank().getRankId() >= PlayerRank.MODERATOR.getRankId()) {
            this.addProp(InfoStandProp.PLATE, "1");
        }

        for (InfoStandEntry prop : props) {
            switch (prop.getType()) {
                case PLATE -> this.plates.add(Integer.parseInt(prop.getProductId()));
                case FURNI -> this.furnis.add(Integer.parseInt(prop.getProductId()));
                case EXPRESSION -> this.expressions.add(prop.getProductId());
                case ACTION -> this.actions.add(prop.getProductId());
                case DIRECTION -> this.directions.add(Integer.parseInt(prop.getProductId()));
            }
        }
    }

    public Player getPlayer() {
        return player;
    }

    public InfoStandActive getActive() {
        return active;
    }

    public void setActive(InfoStandActive active) {
        this.active = active;
    }

    public List<Integer> getPlates() {
        return plates;
    }

    public List<Integer> getFurnis() {
        return furnis;
    }

    public List<String> getExpressions() {
        return expressions;
    }

    public List<String> getActions() {
        return actions;
    }

    public List<Integer> getDirections() {
        return directions;
    }

    public void addProp(InfoStandProp type, String productId) {
        switch (type) {
            case PLATE -> this.plates.add(Integer.parseInt(productId));
            case FURNI -> this.furnis.add(Integer.parseInt(productId));
            case EXPRESSION -> this.expressions.add(productId);
            case ACTION -> this.actions.add(productId);
            case DIRECTION -> this.directions.add(Integer.parseInt(productId));
        }
    }

    public boolean ownsProp(InfoStandProp type, String productId) {
        switch (type) {
            case PLATE -> {
                return this.plates.contains(Integer.parseInt(productId));
            }
            case FURNI -> {
                return this.furnis.contains(Integer.parseInt(productId));
            }
            case EXPRESSION -> {
                return this.expressions.contains(productId);
            }
            case ACTION -> {
                return this.actions.contains(productId);
            }
            case DIRECTION -> {
                return this.directions.contains(Integer.parseInt(productId));
            }
        }

        return false;
    }
}
