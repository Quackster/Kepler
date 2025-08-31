package org.alexdev.kepler.game.misc.figure;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FigureManager {
    private static final String FIGUREDATA_FILE = "figuredata.xml";

    private static FigureManager instance;
    private Map<Integer, List<FigureColor>> figurePalettes;
    private Map<String, FigureSetType> figureSetTypes;
    private Map<String, FigureSet> figureSets;

    private FigureManager() {
        this.figurePalettes = new HashMap<>();
        this.figureSetTypes = new HashMap<>();
        this.figureSets = new HashMap<>();

        this.loadFigurePalettes();
        this.loadFigureSetTypes();
        this.loadFigureSets();
    }

    public int validateFigureCode(String figure, String gender, boolean hasClub) {
        //System.out.println("Validating: " + figure);
        String[] figureData = figure.split("\\.");

        if (figureData.length == 0) {
            return 1;
        }

        List<String> sets = new ArrayList<>();

        for (String data : figureData) {
            String[] parts = data.split("-");

            if (parts.length < 2 || parts.length > 3) {
                return 2;
            }

            sets.add(parts[0]);
        }

        for (var figureSetType : figureSetTypes.values()) {
            if (figureSetType.getSet().equalsIgnoreCase("sh")) {
                continue;
            }

            if (figureSetType.isMandatory() && !sets.contains(figureSetType.getSet())) {
                return 3;
            }
        }

        for (String data : figureData) {
            String[] parts = data.split("-");

            if (parts.length < 2 || parts.length > 3) {
                return 4;
            }

            String set = parts[0];
            String setId = parts[1];

            var figureSet = this.figureSets.values().stream().filter(s ->
                    s.getType().equalsIgnoreCase(set) &&
                            s.getId().equalsIgnoreCase(setId) &&
                            (s.getGender().equalsIgnoreCase(gender) || s.getGender().equalsIgnoreCase("U")))
                    .findFirst().orElse(null);

            if (figureSet == null) {
                return 5;
            }

            if (figureSet.isClub() && !hasClub) {
                return 6;
            }

            if (!figureSet.isSelectable()) {
                return 7;
            }

            var figureSetType = this.figureSetTypes.get(set);

            if (parts.length > 2 && parts[2].length() > 0) {
                var paletteId = parts[2];

                if (this.figurePalettes.get(figureSetType.getPaletteId()).stream().noneMatch(palette -> palette.getColourId().equalsIgnoreCase(paletteId))) {
                    return 8;
                }
            }
        }

        return 0;
    }

    public boolean validateFigure(String figure, String gender, boolean hasClub) {
        //System.out.println("Validating: " + figure);
        String[] figureData = figure.split("\\.");

        if (figureData.length == 0) {
            return false;
        }

        List<String> sets = new ArrayList<>();

        for (String data : figureData) {
            String[] parts = data.split("-");

            if (parts.length < 2 || parts.length > 3) {
                return false;
            }

            sets.add(parts[0]);
        }

        for (var figureSetType : figureSetTypes.values()) {
            if (figureSetType.getSet().equalsIgnoreCase("sh")) {
                continue;
            }

            if (figureSetType.isMandatory() && !sets.contains(figureSetType.getSet())) {
                return false;
            }
        }

        for (String data : figureData) {
            String[] parts = data.split("-");

            if (parts.length < 2 || parts.length > 3) {
                return false;
            }

            String set = parts[0];
            String setId = parts[1];

            var figureSet = this.figureSets.values().stream().filter(s ->
                    s.getType().equalsIgnoreCase(set) &&
                    s.getId().equalsIgnoreCase(setId) &&
                            (s.getGender().equalsIgnoreCase(gender) || s.getGender().equalsIgnoreCase("U")))
                    .findFirst().orElse(null);

            if (figureSet == null) {
                return false;
            }

            if (figureSet.isClub() && !hasClub) {
                return false;
            }

            if (!figureSet.isSelectable()) {
                return false;
            }

            var figureSetType = this.figureSetTypes.get(set);

            if (parts.length > 2 && parts[2].length() > 0) {
                var paletteId = parts[2];

                if (this.figurePalettes.get(figureSetType.getPaletteId()).stream().noneMatch(palette -> palette.getColourId().equalsIgnoreCase(paletteId))) {
                    return false;
                }
            }
        }

        return true;
    }

    private void loadFigurePalettes() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new File(FIGUREDATA_FILE));
            doc.normalize();

            NodeList list = doc.getElementsByTagName("colors");

            for (int i = 0; i < list.getLength(); i++) {
                NodeList paletteList = list.item(i).getChildNodes();

                for (int j = 0; j < paletteList.getLength(); j++) {
                    Node palette = paletteList.item(j);//.getChildNodes();
                    NodeList colourList = palette.getChildNodes();

                    var paletteId = Integer.parseInt(palette.getAttributes().getNamedItem("id").getNodeValue());
                    this.figurePalettes.put(paletteId, new ArrayList<>());

                    for (int k = 0; k < colourList.getLength(); k++) {
                        var colour = colourList.item(k);

                        String colourId = colour.getAttributes().getNamedItem("id").getNodeValue();
                        String index = colour.getAttributes().getNamedItem("index").getNodeValue();
                        boolean isClubRequired = colour.getAttributes().getNamedItem("club").getNodeValue().equalsIgnoreCase("1");
                        boolean isSelectable = colour.getAttributes().getNamedItem("selectable").getNodeValue().equalsIgnoreCase("1");

                        this.figurePalettes.get(paletteId).add(new FigureColor(colourId, index, isClubRequired, isSelectable));
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadFigureSetTypes() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new File(FIGUREDATA_FILE));
            doc.normalize();

            NodeList list = doc.getElementsByTagName("settype");

            for (int i = 0; i < list.getLength(); i++) {
                Node setType = list.item(i);
                String set = setType.getAttributes().getNamedItem("type").getNodeValue();
                int paletteId = Integer.parseInt(setType.getAttributes().getNamedItem("paletteid").getNodeValue());
                boolean isMandatory = setType.getAttributes().getNamedItem("mandatory").getNodeValue().equalsIgnoreCase("1");

                this.figureSetTypes.put(set, new FigureSetType(set, paletteId, isMandatory));
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadFigureSets() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

            Document doc = dBuilder.parse(new File(FIGUREDATA_FILE));
            doc.normalize();

            NodeList list = doc.getElementsByTagName("set");

            for (int i = 0; i < list.getLength(); i++) {
                Node set = list.item(i);
                String setType = set.getParentNode().getAttributes().getNamedItem("type").getNodeValue();
                String id = set.getAttributes().getNamedItem("id").getNodeValue();
                String gender = set.getAttributes().getNamedItem("gender").getNodeValue();
                boolean club = set.getAttributes().getNamedItem("club").getNodeValue().equalsIgnoreCase("1");
                boolean colourable = set.getAttributes().getNamedItem("colorable").getNodeValue().equalsIgnoreCase("1");
                boolean selectable = set.getAttributes().getNamedItem("selectable").getNodeValue().equalsIgnoreCase("1");

                var figureSet = new FigureSet(setType, id, gender, club, colourable, selectable);

                NodeList partList = set.getChildNodes();

                for (int j = 0; j < partList.getLength(); j++) {
                    Node part = partList.item(j);//.getChildNodes();

                    if (part.getNodeName().equalsIgnoreCase("hiddenlayers")) {
                        continue;
                    }

                    figureSet.getFigureParts().add(new FigurePart(
                            part.getAttributes().getNamedItem("id").getNodeValue(),
                            part.getAttributes().getNamedItem("type").getNodeValue(),
                            part.getAttributes().getNamedItem("colorable").getNodeValue().equalsIgnoreCase("1"),
                            Integer.parseInt(part.getAttributes().getNamedItem("index").getNodeValue())));
                }

                this.figureSets.put(id, figureSet);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static FigureManager getInstance() {
        if (instance == null) {
            instance = new FigureManager();
        }

        return instance;
    }

}
