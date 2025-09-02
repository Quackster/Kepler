package net.h4bbo.kepler.game.commands.registered;

import net.h4bbo.kepler.game.achievements.AchievementManager;
import net.h4bbo.kepler.game.ads.AdManager;
import net.h4bbo.kepler.game.catalogue.CatalogueManager;
import net.h4bbo.kepler.game.catalogue.collectables.CollectablesManager;
import net.h4bbo.kepler.game.commands.Command;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.events.EventsManager;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.games.GameManager;
import net.h4bbo.kepler.game.games.snowstorm.SnowStormMapsManager;
import net.h4bbo.kepler.game.item.ItemManager;
import net.h4bbo.kepler.game.player.Player;
import net.h4bbo.kepler.game.room.models.RoomModelManager;
import net.h4bbo.kepler.game.texts.TextsManager;
import net.h4bbo.kepler.game.wordfilter.WordfilterManager;
import net.h4bbo.kepler.messages.outgoing.alert.ALERT;
import net.h4bbo.kepler.util.config.GameConfiguration;
import net.h4bbo.kepler.util.config.writer.GameConfigWriter;

public class ReloadCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuseright.ADMINISTRATOR_ACCESS);
    }

    @Override
    public void addArguments() {
        this.arguments.add("component");
    }

    @Override
    public void handleCommand(Entity entity, String message, String[] args) {
        if (entity.getType() != EntityType.PLAYER) {
            return;
        }

        Player player = (Player) entity;

        if (player.getRoomUser().getRoom() == null) {
            return;
        }

        String component = args[0];
        String componentName = null;

        if (component.equalsIgnoreCase("catalogue")
                || component.equalsIgnoreCase("shop")
                || component.equalsIgnoreCase("items")) {
            ItemManager.reset();
            CatalogueManager.reset();

            // Regenerate collision map with proper height differences (if they changed).
            player.getRoomUser().getRoom().getMapping().regenerateCollisionMap();

                /*
                player.getRoomUser().getRoom().getMapping().sendMap();

                for (Player p : PlayerManager.getInstance().getPlayers()) {
                    new GET_CATALOG_INDEX().handle(p, null);
                }*/

            componentName = "Catalogue and item definitions";
        }

        if (component.equalsIgnoreCase("wordfilter")) {
            WordfilterManager.reset();
            componentName = "Wordfilter";
        }

            /*
            if (component.equalsIgnoreCase("commands")) {
                CommandManager.reset();
                componentName = "Commands";
            }*/

            /*
            if (component.equalsIgnoreCase("badgebuy")) {
                CatalogueManager.getInstance().reloadBadgeRewards();
                componentName = "Badge sale rewards";
            }*/

        if (component.equalsIgnoreCase("ads")) {
            AdManager.getInstance().reset();
            componentName = "Advertisements";
        }

            /*
            if (component.equalsIgnoreCase("navigator")) {
                NavigatorManager.reset();
                componentName = "Navigator";
            }*/

        if (component.equalsIgnoreCase("texts")) {
            TextsManager.reset();
            componentName = "Texts";
        }

        if (component.equalsIgnoreCase("games")) {
            GameManager.reset();
            componentName = "Games";
        }

        if (component.equalsIgnoreCase("events")) {
            EventsManager.reset();
            componentName = "Events";
        }

        if (component.equalsIgnoreCase("gamemaps")) {
            SnowStormMapsManager.reset();
            componentName = "game maps";
        }


        if (component.equalsIgnoreCase("achievements") ||
                component.equalsIgnoreCase("ach")) {
            AchievementManager.reset();
            componentName = "Achievements";
        }

        if (component.equalsIgnoreCase("models")) {
            RoomModelManager.reset();
            componentName = "Room models";
        }

        if (component.equalsIgnoreCase("collectables")) {
            CollectablesManager.reset();
            componentName = "Collectables";
        }

        if (component.equalsIgnoreCase("settings") ||
                component.equalsIgnoreCase("config")) {

            GameConfiguration.reset(new GameConfigWriter());
            componentName = "Game settings";
        }

        /*
        if (component.equalsIgnoreCase("versions")) {
            ItemVersionManager.reset();
            componentName = "Furni settings";
        }*/

        if (componentName != null) {
            player.send(new ALERT(componentName + " have been reloaded."));
        } else {
            player.send(new ALERT("You did not specify which component to reload!" +
                    "<br>You may reload either the catalogue/shop/items, advertisements, events, commands," +
                    "<br>navigator, collectables, models, texts, plugins, wordfitler, games, badgebuy," +
                    "<br>rewards, versions or settings."));
        }
    }

    @Override
    public String getDescription() {
        return "Refresh the settings/items/texts";
    }
}