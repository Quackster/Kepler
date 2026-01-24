package net.h4bbo.kepler.util;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.concurrent.ThreadLocalRandom;

public class FigureUtil {
    public static String getRandomFigure(String genderReq, boolean clubReq) throws ParserConfigurationException, IOException, SAXException {
        StringBuilder figureOutput = new StringBuilder();

        if (genderReq == null) {
            if (ThreadLocalRandom.current().nextBoolean()) {
                genderReq = "M";
            } else {
                genderReq = "F";
            }
        }

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

        Document doc = dBuilder.parse(new File("figuredata.xml"));
        doc.normalize();

        NodeList setTypeList = doc.getElementsByTagName("settype");

        for (int i = 0; i < setTypeList.getLength(); i++) {
            Node setType = setTypeList.item(i);
            NodeList setList = setType.getChildNodes();

            LinkedHashMap<String, Boolean> possibleSets = new LinkedHashMap<>();
            LinkedList<String> randomColours = new LinkedList<>();

            boolean isMandatory = (setType.getAttributes().getNamedItem("mandatory") != null && setType.getAttributes().getNamedItem("mandatory").getNodeValue().equals("1")) ||
                    ThreadLocalRandom.current().nextBoolean();

            if (isMandatory) {
                String type = setType.getAttributes().getNamedItem("type").getNodeValue();

                boolean hasColour = false;

                for (int j = 0; j < setList.getLength(); j++) {
                    Node set = setList.item(j);

                    if (set.getNodeName().equals("set")) {
                        var genderXml = set.getAttributes().getNamedItem("gender");

                        if (genderXml == null) {
                            continue;
                        } else {
                            String gender = set.getAttributes().getNamedItem("gender").getNodeValue();

                            if (!gender.equals("U") && !gender.equals(genderReq)) {
                                continue;
                            }
                        }

                        var selectableXml = set.getAttributes().getNamedItem("selectable");

                        if (selectableXml != null) {
                            String selectable = set.getAttributes().getNamedItem("selectable").getNodeValue();

                            if (selectable.equals("0")) {
                                continue;
                            }
                        }

                        var colourableXml = set.getAttributes().getNamedItem("colorable");

                        if (colourableXml != null) {
                            String colorable = set.getAttributes().getNamedItem("colorable").getNodeValue();
                            hasColour = colorable.equals("0");
                        }

                        var clubXml = set.getAttributes().getNamedItem("club");

                        if (clubXml != null) {
                            String club = set.getAttributes().getNamedItem("club").getNodeValue();

                            if (club.equals("1") && !clubReq) {
                                continue;
                            }
                        }

                        possibleSets.put(set.getAttributes().getNamedItem("id").getNodeValue(), hasColour);
                    }
                }

                int index = ThreadLocalRandom.current().nextInt(0, possibleSets.size());

                String setId = (String) possibleSets.keySet().toArray()[index];
                boolean isColouringAllowed = (boolean) possibleSets.values().toArray()[index];

                if (!isColouringAllowed) {
                    NodeList paletteList = getNodes(doc, setType.getAttributes().getNamedItem("paletteid").getNodeValue());

                    for (int k = 0; k < (paletteList != null ? paletteList.getLength() : 0); k++) {
                        Node set = paletteList.item(k);

                        if (!set.getNodeName().equals("color")) {
                            continue;
                        }

                        var clubXml = set.getAttributes().getNamedItem("club").getNodeValue();

                        if (clubXml.equals("1") && !clubReq) {
                            continue;
                        }

                        var selectableXml = set.getAttributes().getNamedItem("selectable").getNodeValue();

                        if (selectableXml.equals("0")) {
                            continue;
                        }


                        randomColours.add(set.getAttributes().getNamedItem("id").getNodeValue());
                    }
                }

                figureOutput.append(type).append("-").append(setId);
                figureOutput.append('-');

                if (randomColours.size() > 0) {
                    figureOutput.append(randomColours.get(ThreadLocalRandom.current().nextInt(0, randomColours.size())));
                }

                figureOutput.append('.');
            }
        }

        return figureOutput.toString().substring(0, figureOutput.toString().length() - 1);
    }

    private static NodeList getNodes(Document doc, String paletteId) {
        NodeList setTypeList = doc.getElementsByTagName("palette");

        for (int i = 0; i < setTypeList.getLength(); i++) {
            Node setType = setTypeList.item(i);

            if (setType.getAttributes().getNamedItem("id").getNodeValue().equals(paletteId)) {
                return setType.getChildNodes();
            }
        }

        return null;
    }
}
