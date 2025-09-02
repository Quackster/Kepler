package net.h4bbo.kepler.game.pets;

import net.h4bbo.kepler.dao.mysql.PetDao;
import net.h4bbo.kepler.game.entity.Entity;
import net.h4bbo.kepler.game.entity.EntityType;
import net.h4bbo.kepler.game.fuserights.Fuseright;
import net.h4bbo.kepler.game.room.entities.RoomPet;
import net.h4bbo.kepler.game.room.enums.StatusType;
import net.h4bbo.kepler.util.DateUtil;

import java.util.concurrent.TimeUnit;

public class Pet extends Entity {
    private PetDetails petDetails;
    private RoomPet roomUser;

    public Pet(PetDetails petDetails) {
        this.petDetails = petDetails;
        this.roomUser = new RoomPet(this);
    }

    public void awake() {
        this.roomUser.removeStatus(StatusType.AVATAR_SLEEP);
        this.roomUser.setNeedsUpdate(true);

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

    public void saveDetails() {
        PetDao.saveDetails(this.getDetails().getId(), this.getDetails());
    }
}
