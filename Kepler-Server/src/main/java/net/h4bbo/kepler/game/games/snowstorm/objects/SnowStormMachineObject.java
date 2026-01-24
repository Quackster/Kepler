package net.h4bbo.kepler.game.games.snowstorm.objects;

import net.h4bbo.kepler.game.games.enums.GameObjectType;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormActivityState;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormAttributes;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormConstants;
import net.h4bbo.kepler.game.games.snowstorm.util.SnowStormMath;
import net.h4bbo.kepler.game.pathfinder.Position;
import net.h4bbo.kepler.server.netty.streams.NettyResponse;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a snowball machine object in the SnowStorm game.
 */
public class SnowStormMachineObject extends SnowStormGameObject {
    private final int x;
    private final int y;
    private AtomicInteger snowballCount;
    private int snowballGeneratorTimer;

    public SnowStormMachineObject(int objectId, int x, int y, int snowballCount) {
        super(objectId, GameObjectType.SNOWWAR_SNOWMACHINE_OBJECT);
        this.x = x;
        this.y = y;
        this.snowballCount = new AtomicInteger(snowballCount);
        this.snowballGeneratorTimer = SnowStormConstants.MACHINE_SNOWBALL_GENERATOR_TIME;
    }

    @Override
    public void calculateFrameMovement() {
        // Machines don't move
    }

    @Override
    public List<Integer> getChecksumValues() {
        List<Integer> values = getSyncValuesContainer();
        values.add(GameObjectType.SNOWWAR_SNOWMACHINE_OBJECT.getObjectId()); // type
        values.add(getId());
        values.add(SnowStormMath.tileToWorld(x));
        values.add(SnowStormMath.tileToWorld(y));
        values.add(snowballCount.get());
        return values;
    }

    @Override
    public boolean isAlive() {
        return true; // Machines are always present
    }

    @Override
    public void serialiseObject(NettyResponse response) {
        response.writeInt(GameObjectType.SNOWWAR_SNOWMACHINE_OBJECT.getObjectId());
        response.writeInt(getId());
        response.writeInt(SnowStormMath.convertToWorldCoordinate(x));
        response.writeInt(SnowStormMath.convertToWorldCoordinate(y));
        response.writeInt(this.snowballCount.get());
    }

    public Position getPosition() {
        return new Position(x, y);
    }

    public Position getPickupPosition() {
        return new Position(x, y + 1);
    }

    public AtomicInteger getSnowballs() {
        return snowballCount;
    }

    public boolean canGenerateSnowball() {
        return snowballCount.get() < SnowStormConstants.MACHINE_MAX_SNOWBALL_CAPACITY;
    }

    public boolean hasSnowballs() {
        return snowballCount.get() > 0;
    }

    public boolean addSnowball() {
        if (!canGenerateSnowball()) {
            return false;
        }
        snowballCount.incrementAndGet();
        return true;
    }

    public boolean removeSnowball() {
        if (!hasSnowballs()) {
            return false;
        }
        snowballCount.decrementAndGet();
        return true;
    }

    /**
     * Process the generator tick. Returns true if the machine is ready to generate a snowball.
     * Note: This only checks if the machine should generate - call addSnowball() separately to add it.
     */
    public boolean processGeneratorTick() {
        if (snowballGeneratorTimer > 0) {
            snowballGeneratorTimer--;
            return false;
        }

        snowballGeneratorTimer = SnowStormConstants.MACHINE_SNOWBALL_GENERATOR_TIME;
        return canGenerateSnowball();
    }

    public boolean isPlayerAtPickupPosition(SnowStormAttributes attr) {
        if (attr == null || attr.getCurrentPosition() == null) {
            return false;
        }
        Position pickupPos = getPickupPosition();
        return attr.getCurrentPosition().getX() == pickupPos.getX()
                && attr.getCurrentPosition().getY() == pickupPos.getY();
    }

    /**
     * Check if the player can pick up from this machine.
     */
    public boolean canPickup(SnowStormAttributes attr) {
        if (attr == null) {
            return false;
        }

        // Must be at pickup position
        if (!isPlayerAtPickupPosition(attr)) {
            return false;
        }

        // Can't pickup while walking
        if (attr.isWalking()) {
            return false;
        }

        // Can't pickup if in non-normal state
        var state = attr.getActivityState();
        if (state != SnowStormActivityState.ACTIVITY_STATE_NORMAL
                && state != SnowStormActivityState.ACTIVITY_STATE_INVINCIBLE_AFTER_STUN) {
            return false;
        }

        // Machine must have snowballs and player must have room
        return hasSnowballs() && attr.getSnowballs().get() < SnowStormConstants.MAX_SNOWBALLS;
    }

    /**
     * Apply pickup - transfers snowball from machine to player.
     * Call AFTER checksum calculation.
     */
    public void applyPickup(SnowStormAttributes attr) {
        if (hasSnowballs() && attr.getSnowballs().get() < SnowStormConstants.MAX_SNOWBALLS) {
            removeSnowball();
            attr.getSnowballs().incrementAndGet();
        }
    }
}
