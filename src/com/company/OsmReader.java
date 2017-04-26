package com.company;

import com.company.data.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OsmReader {

    Osm osm;

    private String filename;

    OsmReader(String filename) {
        setFilename(filename);
        osm = new Osm();
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    Osm parse() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(filename);

        osm.clear();
        Node root = document.getDocumentElement();

        NodeList childNodes = root.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);

            if ("bounds".equals(childNode.getNodeName())) {

                Element childElement = (Element)childNode;
                double minLat = Double.parseDouble(childElement.getAttribute("minlat"));
                double minLon = Double.parseDouble(childElement.getAttribute("minlon"));
                double maxLat = Double.parseDouble(childElement.getAttribute("maxlat"));
                double maxLon = Double.parseDouble(childElement.getAttribute("maxlon"));

                OsmBounds bounds = new OsmBounds(minLat, minLon, maxLat, maxLon);
                osm.bounds = bounds;

            } else if ("node".equals(childNode.getNodeName())) {
                Element childElement = (Element)childNode;
                String id = childElement.getAttribute("id");
                double latitude = Double.parseDouble(childElement.getAttribute("lat"));
                double longitude = Double.parseDouble(childElement.getAttribute("lon"));

                HashMap<String, String> osmTags = new HashMap<>();
                NodeList nodeChildren = childNode.getChildNodes();
                for (int j=0; j<nodeChildren.getLength(); j++) {
                    Node nodeChild = nodeChildren.item(j);
                    if ("tag".equals(nodeChild.getNodeName())) {
                        Element nodeChildElement = (Element)nodeChild;
                        String k = nodeChildElement.getAttribute("k");
                        String v = nodeChildElement.getAttribute("v");
                        osmTags.put(k, v);
                    }
                }
                OsmNode osmNode = new OsmNode(id, new GeoCoordinate(latitude, longitude), osmTags);
                osm.nodes.add(osmNode);

            } else if ("way".equals(childNode.getNodeName())) {
                Element childElement = (Element)childNode;
                String id = childElement.getAttribute("id");

                List<OsmNode> refOsmNodes = new ArrayList<>();
                HashMap<String, String> osmTags = new HashMap<>();

                NodeList wayChildren = childNode.getChildNodes();
                for (int j=0; j<wayChildren.getLength(); j++) {
                    Node wayChild = wayChildren.item(j);
                    if ("nd".equals(wayChild.getNodeName())) {
                        Element wayChildElement = (Element)wayChild;
                        String refId = wayChildElement.getAttribute("ref");
                        OsmNode refOsmNode = getOsmNodeById(osm.nodes, refId);
                        refOsmNodes.add(refOsmNode);
                    } else if ("tag".equals(wayChild.getNodeName())) {
                        Element nodeChildElement = (Element)wayChild;
                        String k = nodeChildElement.getAttribute("k");
                        String v = nodeChildElement.getAttribute("v");
                        osmTags.put(k, v);
                    }
                }

                OsmWay osmWay = new OsmWay(id, refOsmNodes, osmTags);
                osm.ways.add(osmWay);

            } else if ("relation".equals(childNode.getNodeName())) {

                // TODO: implement this for more details and accuracy.

            }
        }
        return osm;
    }

    OsmNode getOsmNodeById(List<OsmNode> osmNodes, String id) {
        if (id != null && !id.isEmpty()) {
            for (OsmNode osmNode : osmNodes) {
                if (id.equals(osmNode.getId())) {
                    return osmNode;
                }
            }
        }
        return null;
    }

    public static void printOsm(Osm osm) {
        printOsmNodes(osm.nodes);
        for (OsmWay osmWay: osm.ways) {
            System.out.printf("<way id=%s>\n", osmWay.getId());
            printOsmNodes(osmWay.getOsmNodes(), 1);

            HashMap<String, String> tags = osmWay.getTags();
            for (String k: tags.keySet()) {
                String v = tags.get(k);
                System.out.printf("  %s=%s\n", k, v);
            }
            System.out.printf("</way>\n");
        }
    }

    static void printOsmNodes(List<OsmNode> osmNodes) {
        printOsmNodes(osmNodes, 0);
    }

    static void printOsmNodes(List<OsmNode> osmNodes, int indent) {
        for (OsmNode osmNode: osmNodes) {
            GeoCoordinate geoCoordinate = osmNode.getGeoCoordinate();
            for (int i=0; i<indent; i++) {
                System.out.print("  ");
            }
            System.out.printf("<node id=%s, lat=%f, lon=%f>\n", osmNode.getId(), geoCoordinate.latitude, geoCoordinate.longitude);
            HashMap<String, String> tags = osmNode.getTags();
            for (String k: tags.keySet()) {
                String v = tags.get(k);
                for (int i=0; i<indent; i++) {
                    System.out.print("  ");
                }
                System.out.printf("  %s=%s\n", k, v);
            }
        }
    }

    void printNode(int indent, Node node) {
        if (node.getNodeType() != Node.ELEMENT_NODE) {
            return;
        }

        for (int i=0; i<indent; i++) {
            System.out.print("  ");
        }
        System.out.print(node.getNodeName());

        NamedNodeMap nodeMap = node.getAttributes();
        for (int i=0; i<nodeMap.getLength(); i++) {
            Node attr = nodeMap.item(i);
            System.out.print(", " + attr.getNodeName() + "=" + attr.getNodeValue());
        }
        System.out.println();

        NodeList childNodes = node.getChildNodes();
        for (int i=0; i<childNodes.getLength(); i++) {
            Node childNode = childNodes.item(i);
            printNode(indent+1, childNode);
        }
    }
}
