package org.alexdev.kepler.game.room.models.triggers;

import org.alexdev.kepler.dao.mysql.PetDao;
import org.alexdev.kepler.dao.mysql.RoomVisitsDao;
import org.alexdev.kepler.game.achievements.AchievementManager;
import org.alexdev.kepler.game.achievements.AchievementType;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.guides.GuideManager;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.item.interactors.types.PetNestInteractor;
import org.alexdev.kepler.game.pathfinder.Position;
import org.alexdev.kepler.game.pets.PetDetails;
import org.alexdev.kepler.game.player.Player;
import org.alexdev.kepler.game.room.Room;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.messages.outgoing.rooms.items.PLACE_FLOORITEM;
import org.alexdev.kepler.messages.outgoing.rooms.items.STUFFDATAUPDATE;

public class FlatTrigger extends GenericTrigger {
    @Override
    public void onRoomEntry(Entity entity, Room room, boolean firstEntry, Object... customArgs) {
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;

        /*player.send(new MessageComposer() {
            @Override
            public void compose(NettyResponse response) {
                response.writeBool(true);
            }

            @Override
            public short getHeader() {
                return 356; // E`
            }
        });*/

        RoomVisitsDao.addVisit(player.getDetails().getId(), room.getId());

        AchievementManager.getInstance().tryProgress(AchievementType.ACHIEVEMENT_ROOMENTRY, player);

        if (player.getGuideManager().isGuide() && player.getGuideManager().getInvitedBy() > 0) {
            int invitedBy = player.getGuideManager().getInvitedBy();

            if (room.getData().getOwnerId() == invitedBy) {
                room.getEntityManager().getPlayers().stream()
                        .filter(p -> p.getDetails().getId() == invitedBy)
                        .findFirst()
                        .ifPresent(newb -> {
                            GuideManager.getInstance().tutorEnterRoom(player, newb);
                        });

                player.getGuideManager().setInvitedBy(0);
            }
        }

        if (firstEntry) {
            for (Item item : room.getItemManager().getFloorItems().stream().filter(item -> item.getDefinition().getInteractionType() == InteractionType.PET_NEST).toList()) {
                PetNestInteractor interactor = (PetNestInteractor) InteractionType.PET_NEST.getTrigger();

                PetDetails petDetails = PetDao.getPetDetails(item.getId());

                if (petDetails != null) {
                    Position position = new Position(petDetails.getX(), petDetails.getY());
                    position.setRotation(petDetails.getRotation());

                    interactor.addPet(room, petDetails, position);
                }
            }
        }

        // Fix showing water amount, doesn't show on initial load
        // Also can't interact with waterbowl until it's been placed again so this is a workaround
        for (Item item : room.getItemManager().getFloorItems().stream().filter(item -> item.getDefinition().getInteractionType() == InteractionType.PET_WATER_BOWL).toList()) {
            player.send(new PLACE_FLOORITEM(item));
            player.send(new STUFFDATAUPDATE(item));
        }
    }

    @Override
    public void onRoomLeave(Entity entity, Room room, Object... customArgs)  {

    }
}
