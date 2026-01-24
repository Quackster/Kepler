package net.h4bbo.kepler.game.games.snowstorm.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized registry for SnowStorm item properties.
 * Consolidates walkable heights (for pathfinding) and collision heights (for snowball physics).
 */
public final class SnowStormItemProperties {
    private static final Map<String, ItemProperty> PROPERTIES = new HashMap<>();

    static {
        // Trees (all have walkable height 3, collision height 4600)
        register("sw_tree1", 3, 4600);
        register("sw_tree2", 3, 4600);
        register("sw_tree3", 3, 4600);
        register("sw_tree4", 3, 4600);

        // Basic blocks
        register("block_basic", 1, 2300);
        register("block_basic2", 2, 4600);
        register("block_basic3", 3, 6900);
        register("block_small", 0, 1150);

        // Ice blocks
        register("block_ice", 1, 2300);
        register("block_ice2", 2, 4600);

        // Arch blocks (bottom parts - walkable height 3, collision height 6900)
        register("block_arch1b", 3, 6900);
        register("block_arch2b", 3, 6900);
        register("block_arch3b", 3, 6900);

        // Arch blocks (top parts - walkable height 3, collision height varies)
        register("block_arch1", 3, 2300);
        register("block_arch2", 0, 2300);
        register("block_arch3", 3, 2300);

        // Obstacles
        register("obst_duck", 1, 2300);
        register("obst_snowman", 3, 4600);

        // Fence
        register("sw_fence", 1, 2500);

        // Snowball machine
        register("snowball_machine", 1, 2400);
        register("snowball_machine_hidden", 1, 0);
    }

    private SnowStormItemProperties() {}

    private static void register(String itemName, int walkableHeight, int collisionHeight) {
        PROPERTIES.put(itemName, new ItemProperty(walkableHeight, collisionHeight));
    }

    /**
     * Gets the walkable height for pathfinding (0-3).
     * Higher values mean taller obstacles.
     */
    public static int getWalkableHeight(String itemName) {
        ItemProperty property = PROPERTIES.get(itemName);
        return property != null ? property.walkableHeight : 0;
    }

    /**
     * Gets the collision height for snowball physics (in game units).
     * Snowballs with height below this value will collide with the item.
     */
    public static int getCollisionHeight(String itemName) {
        ItemProperty property = PROPERTIES.get(itemName);
        return property != null ? property.collisionHeight : -1;
    }

    /**
     * Checks if an item is registered.
     */
    public static boolean hasItem(String itemName) {
        return PROPERTIES.containsKey(itemName);
    }

    private static class ItemProperty {
        final int walkableHeight;
        final int collisionHeight;

        ItemProperty(int walkableHeight, int collisionHeight) {
            this.walkableHeight = walkableHeight;
            this.collisionHeight = collisionHeight;
        }
    }
}
