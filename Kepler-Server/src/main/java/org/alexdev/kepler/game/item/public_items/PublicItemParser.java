package org.alexdev.kepler.game.item.public_items;

import org.alexdev.kepler.dao.mysql.ItemDao;
import org.alexdev.kepler.game.item.Item;
import org.alexdev.kepler.game.item.base.ItemBehaviour;
import org.alexdev.kepler.game.item.triggers.*;
import org.alexdev.kepler.game.triggers.GenericTrigger;
import org.alexdev.kepler.game.games.triggers.BattleShipsTrigger;
import org.alexdev.kepler.game.games.triggers.ChessTrigger;
import org.alexdev.kepler.game.games.triggers.PokerTrigger;
import org.alexdev.kepler.game.games.triggers.TicTacToeTrigger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PublicItemParser {
    public static List<Item> getPublicItems(int roomId, String modelId) {
        TicTacToeTrigger ticTacToeTrigger = new TicTacToeTrigger(roomId);
        ChessTrigger chessTrigger = new ChessTrigger(roomId);
        BattleShipsTrigger battleShipsTrigger = new BattleShipsTrigger(roomId);
        PokerTrigger pokerTrigger = new PokerTrigger(roomId);

        Map<String, GenericTrigger> itemTriggerMap = new HashMap<>() {{
            put("poolExit", new PoolExitTrigger());
            put("poolEnter", new PoolEnterTrigger());
            put("poolLift", new PoolLiftTrigger());
            put("poolBooth", new PoolBoothTrigger());
            put("queue_tile2", new PoolQueueTrigger());
            put("gamehall_chair_wood", ticTacToeTrigger);

            if (modelId.equals("hallC")) {
                put("gamehall_chair_green", chessTrigger);
                put("chess_king_chair", chessTrigger);
            }

            if (modelId.equals("hallB")) {
                put("gamehall_chair_green", battleShipsTrigger);
            }

            if (modelId.equals("hallD")) {
                put("gamehall_chair_green", pokerTrigger);
            }
        }};

        int itemId = 0;

        List<Item> items = new ArrayList<>();
        List<PublicItemData> publicItemData = ItemDao.getPublicItemData(modelId);

        for (PublicItemData itemData : publicItemData) {
            Item item = new Item();
            item.setId(itemId++);
            item.setRoomId(roomId);
            item.setCustomData(itemData.getId());
            item.getDefinition().setSprite(itemData.getSprite());
            item.getDefinition().setTopHeight(itemData.getTopHeight());
            item.getDefinition().setLength(itemData.getLength());
            item.getDefinition().setWidth(itemData.getWidth());
            item.setCurrentProgram(itemData.getCurrentProgram());

            if (itemTriggerMap.containsKey(itemData.getSprite())) {
                item.setItemTrigger(itemTriggerMap.get(itemData.getSprite()));
            }

            item.getPosition().setX(itemData.getX());
            item.getPosition().setY(itemData.getY());
            item.getPosition().setZ(itemData.getZ());
            item.getPosition().setRotation(itemData.getRotation());

            if (itemData.getBehaviour().length() > 0) {
                for (String behaviour : itemData.getBehaviour().split(",")) {
                    item.getDefinition().addBehaviour(ItemBehaviour.valueOf(behaviour.toUpperCase()));
                }
            }

            if (item.getDefinition().hasBehaviour(ItemBehaviour.CAN_SIT_ON_TOP)) {
                if (item.getItemTrigger() == null) {
                    item.setItemTrigger(ItemBehaviour.CAN_SIT_ON_TOP.getTrigger());
                }
            }

            item.getDefinition().addBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT);

            if (item.getDefinition().getSprite().equals("poolLift") ||
                item.getDefinition().getSprite().equals("poolBooth")) {
                item.showProgram("open");
            }

            if (item.getDefinition().getSprite().equals("queue_tile2")) {
                item.getDefinition().removeBehaviour(ItemBehaviour.PUBLIC_SPACE_OBJECT);
            }

            items.add(item);
        }

        /*File file = Paths.get("tools", "gamedata", "public_items", modelId + ".dat").toFile();

        if (!file.exists()) {
            return items;
        }

        int id = Integer.MAX_VALUE;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] data = line.split(" ");

                Item item = new Item();
                item.setId(id--);
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
                    preparedStatement = Storage.getStorage().prepare("INSERT INTO public_items (id, room_model, sprite, x, y, z, rotation, top_height, length, width, behaviour, current_program) " +
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
