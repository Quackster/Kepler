package org.alexdev.kepler.game.pets;

import org.alexdev.kepler.dao.mysql.PetDao;
import org.alexdev.kepler.game.entity.Entity;
import org.alexdev.kepler.game.entity.EntityType;
import org.alexdev.kepler.game.fuserights.Fuseright;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.room.entities.RoomPet;
import org.alexdev.kepler.game.room.enums.StatusType;
import org.alexdev.kepler.util.DateUtil;

import java.util.concurrent.TimeUnit;

public class Pet extends Entity {
    private PetDetails petDetails;
    private PetAction petAction;
    private long actionExpiry;
    private RoomPet roomUser;
    private boolean walkBeforeSitLay;

    public Pet(PetDetails petDetails) {
        this.petDetails = petDetails;
        this.roomUser = new RoomPet(this);
        this.petAction = PetAction.NONE;
    }

    public void awake() {
        this.roomUser.removeStatus(StatusType.AVATAR_SLEEP);
        this.roomUser.setNeedsUpdate(true);

        this.petAction = PetAction.NONE;
        this.actionExpiry = 0;

        this.petDetails.setLastKip(DateUtil.getCurrentTimeSeconds());
        PetDao.saveDetails(this.petDetails.getId(), this.petDetails);
    }

    public int getAge() {
        return (int) TimeUnit.SECONDS.toDays(DateUtil.getCurrentTimeSeconds() - this.petDetails.getBorn());
    }

    public int getHunger() {
        return PetManager.getInstance().getPetStats(this.petDetails.getLastEat(), PetStat.HUNGER);
    }

    public int getThirst() {
        return PetManager.getInstance().getPetStats(this.petDetails.getLastDrink(), PetStat.THIRST);
    }

    public int getHappiness() {
        return PetManager.getInstance().getPetStats(this.petDetails.getLastPlayToy(), PetStat.HAPPINESS);
    }

    public int getEnergy() {
        return PetManager.getInstance().getPetStats(this.petDetails.getLastKip(), PetStat.ENERGY);
    }

    public int getFriendship() {
        return PetManager.getInstance().getPetStats(this.petDetails.getLastPlayUser(), PetStat.FRIENDSHIP);
    }

    public boolean isThirsty() {
        return this.getThirst() <= 2;
    }

    public boolean isHungry() {
        return this.getHunger() <= 3;
    }


    @Override
    public boolean hasFuse(Fuseright permission) {
        return false;
    }

    @Override
    public PetDetails getDetails() {
        return this.petDetails;
    }

    @Override
    public RoomPet getRoomUser() {
        return this.roomUser;
    }

    @Override
    public EntityType getType() {
        return EntityType.PET;
    }

    @Override
    public void dispose() {

    }

    public PetAction getAction() {
        return petAction;
    }

    public void setAction(PetAction petAction) {
        this.petAction = petAction;
    }

    public boolean isDoingAction() {
        return !hasActionExpired() && (this.petAction == PetAction.SLEEP || this.petAction == PetAction.EAT || this.petAction == PetAction.DRINK || this.petAction == PetAction.LAY || this.petAction == PetAction.SIT);
    }

    public boolean isActionAllowed() {
        return (this.petAction == PetAction.NONE || this.petAction == PetAction.SIT || this.petAction == PetAction.LAY) && (this.roomUser.getCurrentItem() == null ||
                ((!this.roomUser.getCurrentItem().getDefinition().hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP) && !this.roomUser.getCurrentItem().getDefinition().hasBehaviour(ItemBehaviour.CAN_LAY_ON_TOP)) &&
                !this.roomUser.getCurrentItem().getDatabaseId().equals(this.petDetails.getItemId())));
    }

    public boolean hasActionExpired() {
        return DateUtil.getCurrentTimeSeconds() > this.actionExpiry;
    }

    public void setActionDuration(int seconds) {
        this.actionExpiry = DateUtil.getCurrentTimeSeconds() + seconds;
    }

    public boolean isWalkBeforeSitLay() {
        return walkBeforeSitLay;
    }

    public void setWalkBeforeSitLay(boolean walkBeforeSitLay) {
        this.walkBeforeSitLay = walkBeforeSitLay;
    }
}
