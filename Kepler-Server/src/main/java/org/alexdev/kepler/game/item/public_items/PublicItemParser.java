package org.alexdev.kepler.game.item.public_items;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.dao.mysql.PublicRoomsDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.ItemManager;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.interactors.InteractionType;
import org.alexdev.kepler.game.pathfinder.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class PublicItemParser {
    public static List<Item> getPublicItems(int roomId, String modelId) {
        List<String> randomPublicId = new ArrayList<>();
        Map<String, InteractionType> itemTriggerMap = new HashMap<>();

        itemTriggerMap.put("poolEnter", InteractionType.POOL_ENTER);
        itemTriggerMap.put("poolExit", InteractionType.POOL_EXIT);//.POOL_EXIT);

        itemTriggerMap.put("poolLift", InteractionType.POOL_LIFT);
        itemTriggerMap.put("poolBooth", InteractionType.POOL_BOOTH);
        itemTriggerMap.put("queue_tile2", InteractionType.POOL_QUEUE);
        itemTriggerMap.put("s_queue_tile2", InteractionType.POOL_QUEUE);
        itemTriggerMap.put("gamehall_chair_wood", InteractionType.GAME_TIC_TAC_TOE);

        if (modelId.equals("hallC")) {
            itemTriggerMap.put("gamehall_chair_green", InteractionType.GAME_CHESS);
            itemTriggerMap.put("chess_king_chair", InteractionType.GAME_CHESS);
        }

        if (modelId.equals("hallB")) {
            itemTriggerMap.put("gamehall_chair_green", InteractionType.GAME_BATTLESHIPS);
        }

        if (modelId.equals("hallD")) {
            itemTriggerMap.put("gamehall_chair_green", InteractionType.GAME_POKER);
        }

        itemTriggerMap.put("wsJoinQueue", InteractionType.WS_JOIN_QUEUE);
        itemTriggerMap.put("wsQueueTile", InteractionType.WS_QUEUE_TILE);
        itemTriggerMap.put("wsTileStart", InteractionType.WS_TILE_START);

        int itemId = 0;

        List<Item> items = new ArrayList<>();
        List<PublicItemData> publicItemData = PublicRoomsDao.getPublicItemData(modelId);

        for (PublicItemData itemData : publicItemData) {
            String customId = null;

            String alphabet = "abcdefghijlmnopqrstuvwyz";

            while (customId == null) {
                String temp = alphabet.charAt(ThreadLocalRandom.current().nextInt(0, alphabet.length())) + "" + ThreadLocalRandom.current().nextInt(0, 999);

                if (!randomPublicId.contains(temp)) {
                    randomPublicId.add(temp);
                    customId = temp;
                }
            }

            Item item = new Item();
            item.setId(itemId++);
            item.setRoomId(roomId);
            item.setCustomData(customId);
            item.getDefinition().setSprite(itemData.getSprite());
            item.getDefinition().setTopHeight(itemData.getTopHeight());
            item.getDefinition().setLength(itemData.getLength());
            item.getDefinition().setWidth(itemData.getWidth());
            item.setCurrentProgram(itemData.getCurrentProgram());

            if (itemTriggerMap.containsKey(itemData.getSprite())) {
                item.getDefinition().setInteractionType(itemTriggerMap.get(itemData.getSprite()));
            }

            if (itemData.getBehaviour().length() > 0) {
                for (String behaviour : itemData.getBehaviour().split(",")) {
                    item.getDefinition().addBehaviour(ItemBehaviour.valueOf(behaviour.toUpperCase()));
                }
            }

            if (item.getDefinition().getInteractionType() == null) {
                if (item.getDefinition().hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP)) {
                    item.getDefinition().setInteractionType(InteractionType.CHAIR);
                } else {
                    item.getDefinition().setInteractionType(InteractionType.DEFAULT);
                }
            }

            item.getPosition().setX(itemData.getX());
            item.getPosition().setY(itemData.getY());
            item.getPosition().setZ(itemData.getZ());
            item.getPosition().setRotation(itemData.getRotation());

            if (!item.getDefinition().getSprite().contains("queue_tile2")) {
                item.getDefinition().addBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT);
            }

            if (item.getDefinition().hasBehaviour(ItemBehaviour.PRIVATE_FURNITURE)) {
                item.setDefinitionId(ItemManager.getInstance().getDefinitionBySprite(itemData.getSprite()).getId());
            }

            if (item.getDefinition().getSprite().equals("poolLift") ||
                    item.getDefinition().getSprite().equals("poolBooth")) {
                item.showProgram("open");
            }

            if (itemData.getTeleportTo() != null) {
                int X = Integer.parseInt(itemData.getTeleportTo()[0]);
                int Y = Integer.parseInt(itemData.getTeleportTo()[1]);
                int Z = Integer.parseInt(itemData.getTeleportTo()[2]);

                var position = new Position(X, Y, Z);
                position.setBodyRotation(Integer.parseInt(itemData.getTeleportTo()[3]));
                position.setHeadRotation(Integer.parseInt(itemData.getTeleportTo()[3]));

                item.setTeleportTo(position);
            }

            if (itemData.getSwimTo() != null) {
                int X = Integer.parseInt(itemData.getSwimTo()[0]);
                int Y = Integer.parseInt(itemData.getSwimTo()[1]);
                int Z = Integer.parseInt(itemData.getSwimTo()[2]);

                var position = new Position(X, Y, Z);
                position.setBodyRotation(Integer.parseInt(itemData.getSwimTo()[3]));
                position.setHeadRotation(Integer.parseInt(itemData.getSwimTo()[3]));

                item.setSwimTo(position);
            }

            items.add(item);
        }

        /*File file = Paths.get("tools", "gamedata", "publicrooms", modelId + ".dat").toFile();

        if (!file.exists()) {
            return items;
        }

        int id = Integer.MAX_VALUE;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");

                Item item = new Item();
                item.setVirtualId(id--);
                item.getDefinition().addBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT);
                item.getDefinition().setSprite(data[1]);
                item.getDefinition().setTopHeight(ItemDefinition.DEFAULT_TOP_HEIGHT);

                item.setCustomData(data[0]);
                item.getPosition().setX(Integer.parseInt(data[2]));
                item.getPosition().setY(Integer.parseInt(data[3]));
                item.getPosition().setZ(Integer.parseInt(data[4]));
                item.getPosition().setRotation(Integer.parseInt(data[5]));

                if (data.length >= 7) {
                    String customData = data[6];

                    if (customData.equals("2")) {
                        item.getDefinition().addBehaviour(ItemBehaviour.EXTRA_PARAMETER);
                    } else {
                        item.setCurrentProgram(customData);
                    }
                }


                // Set item triggers for public room furniture
                if (itemTriggerMap.containsKey(item.getDefinition().getSprite())) {
                    item.setItemTrigger(itemTriggerMap.get(item.getDefinition().getSprite()));
                }

                if (item.getDefinition().getSprite().contains("chair")
                        || item.getDefinition().getSprite().contains("bench")
                        || item.getDefinition().getSprite().contains("seat")
                        || item.getDefinition().getSprite().contains("stool")
                        || item.getDefinition().getSprite().contains("sofa")
                        || item.getDefinition().getSprite().equals("l")
                        || item.getDefinition().getSprite().equals("m")
                        || item.getDefinition().getSprite().equals("k")
                        || item.getDefinition().getSprite().equals("shift1")
                        || item.getDefinition().getSprite().equals("stone")
                        || item.getDefinition().getSprite().startsWith("rooftop_flatcurb")) {
                    item.getDefinition().addBehaviour(ItemBehaviour.CAN_SIT_ON_TOP);
                    item.getDefinition().removeBehaviour(ItemBehaviour.CAN_STAND_ON_TOP);
                    item.getDefinition().setTopHeight(1.0);

                    if (item.getItemTrigger() == null) {
                        item.setItemTrigger(ItemBehaviour.CAN_SIT_ON_TOP.getTrigger());
                    }

                } else {
                    item.getDefinition().removeBehaviour(ItemBehaviour.CAN_SIT_ON_TOP);
                    item.getDefinition().removeBehaviour(ItemBehaviour.CAN_STAND_ON_TOP);
                }

                if (item.getDefinition().getSprite().equals("poolEnter")
                    || item.getDefinition().getSprite().equals("poolExit")
                    || item.getDefinition().getSprite().equals("poolLift")
                    || item.getDefinition().getSprite().equals("poolBooth")
                    || item.getDefinition().getSprite().equals("queue_tile2")
                    || item.getDefinition().getSprite().equals("stair")) {
                    //item.getBehaviour().setCanSitOnTop(false);
                    //item.getBehaviour().setCanStandOnTop(true);
                    item.getDefinition().removeBehaviour(ItemBehaviour.CAN_SIT_ON_TOP);
                    item.getDefinition().addBehaviour(ItemBehaviour.CAN_STAND_ON_TOP);
                }

                if (item.getDefinition().getSprite().equals("queue_tile2")) {
                    item.getDefinition().removeBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT);
                }

                if (item.getDefinition().getSprite().equals("poolLift") ||
                    item.getDefinition().getSprite().equals("poolBooth")) {
                    item.setCurrentProgramValue("open");
                }

                // Custom heights for these furniture
                if (item.getDefinition().getSprite().equals("picnic_dummychair4")) {
                    item.getDefinition().setTopHeight(4.0);
                }

                if (item.getDefinition().getSprite().equals("picnic_dummychair6")) {
                    item.getDefinition().setTopHeight(7.0);
                }

                // This is the only public item I'm aware of that has a length of 2
                if (item.getDefinition().getSprite().equals("hw_shelf")) {
                    item.getDefinition().setLength(2);
                }

                Connection sqlConnection = null;
                PreparedStatement preparedStatement = null;

                String alphabet = "abcdefghijlmnopqrstuvwyz";

                item.getDefinition().getBehaviourList().remove(ItemBehaviour.PUBLIC_SPACE_OBJECT);
                String[] behaviourList = new String[item.getDefinition().getBehaviourList().size()];

                for (int i = 0; i  < item.getDefinition().getBehaviourList().size(); i++) {
                    behaviourList[i] = item.getDefinition().getBehaviourList().get(i).name().toLowerCase();
                }

                try {
                    sqlConnection = Storage.getStorage().getConnection();
                    preparedStatement = Storage.getStorage().prepare("INSERT INTO publicrooms (id, room_model, sprite, x, y, z, rotation, top_height, length, width, behaviour, current_program) " +
                                    "VALUES (?, ?, ?, ?, ?, ? ,?, ?, ? ,? ,? ,?)", sqlConnection);
                    preparedStatement.setString(1, alphabet.charAt(ThreadLocalRandom.current().nextInt(0, alphabet.length())) + "" + ThreadLocalRandom.current().nextInt(0, 999));
                    preparedStatement.setString(2, modelId);
                    preparedStatement.setString(3, item.getDefinition().getSprite());
                    preparedStatement.setInt(4, item.getPosition().getX());
                    preparedStatement.setInt(5, item.getPosition().getY());
                    preparedStatement.setDouble(6, item.getPosition().getZ());
                    preparedStatement.setInt(7, item.getPosition().getRotation());
                    preparedStatement.setDouble(8, item.getDefinition().getTopHeight());
                    preparedStatement.setInt(9, item.getDefinition().getWidth());
                    preparedStatement.setInt(10, item.getDefinition().getLength());
                    preparedStatement.setString(11, String.join(",", behaviourList));
                    preparedStatement.setString(12, item.getCurrentProgram() != null ? item.getCurrentProgram() : "");
                    preparedStatement.execute();

                } catch (Exception e) {
                    Storage.logError(e);
                } finally {
                    Storage.closeSilently(preparedStatement);
                    Storage.closeSilently(sqlConnection);
                }

                items.add(item);
            }
        } catch (IOException e) {
            return items;
        }*/

        return items;
    }
}
