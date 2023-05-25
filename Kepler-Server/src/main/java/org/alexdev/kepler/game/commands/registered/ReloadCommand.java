package org.alexdev.kepler.game.commands.registered;

import org.alexdev.kepler.game.catalogue.CatalogueManager;
import org.alexdev.kepler.game.commands.Command;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuse;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.player.PlayerManager;
import org.alexdev.kepler.game.room.models.RoomModelManager;
import org.alexdev.kepler.game.texts.TextsManager;
import org.alexdev.kepler.messages.outgoing.catalogue.CATALOGUE_PAGES;
import org.alexdev.kepler.messages.outgoing.catalogue.REFRESH_CATALOGUE;
import org.alexdev.kepler.messages.outgoing.alert.ALERT;
import org.alexdev.kepler.util.config.GameConfiguration;
import org.alexdev.kepler.util.config.writer.GameConfigWriter;

public class ReloadCommand extends Command {
    @Override
    public void addPermissions() {
        this.permissions.add(Fuse.DEBUG);
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

            // Resend the catalogue index every 15 minutes to clear page cache
            for (Player p : PlayerManager.getInstance().getPlayers()) {
                p.send(new CATALOGUE_PAGES(
                        CatalogueManager.getInstance().getPagesForRank(p.getDetails().getFuseRights(), p.getDetails().hasClubSubscription())
                ));
                p.send(new REFRESH_CATALOGUE());
            }



            componentName = "Catalogue and item definitions";
        }

        if (component.equalsIgnoreCase("texts")) {
            TextsManager.reset();
            componentName = "Texts";
        }

        if (component.equalsIgnoreCase("models")) {
            RoomModelManager.reset();
            componentName = "Room models";
        }

        if (component.equalsIgnoreCase("settings") ||
                component.equalsIgnoreCase("config")) {

            GameConfiguration.reset(new GameConfigWriter());
            componentName = "Game settings";
        }

        if (componentName != null) {
            player.send(new ALERT(componentName + " has been reloaded."));
        } else {
            player.send(new ALERT("You did not specify which component to reload!<br>You may reload either the catalogue/shop/items, models, texts or game settings."));
        }
    }

    @Override
    public String getDescription() {
        return "Refresh the settings/items/texts";
    }
}