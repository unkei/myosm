package com.company;

import com.company.data.GeoCoordinate;
import com.company.data.OsmNode;
import com.company.data.Osm;
import com.company.data.OsmWay;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
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

                // TODO: need anything?

            } else if ("node".equals(childNode.getNodeName())) {
                Element childElement = (Element)childNode;
                String id = childElement.getAttribute("id");
                double latitude = Double.parseDouble(childElement.getAttribute("lat"));
                double longitude = Double.parseDouble(childElement.getAttribute("lon"));

                OsmNode osmNode = new OsmNode(id, new GeoCoordinate(latitude, longitude));

//                NodeList nodeChildren = childNode.getChildNodes();
                // TODO: add tag handling.

                osm.nodes.add(osmNode);

            } else if ("way".equals(childNode.getNodeName())) {
                Element childElement = (Element)childNode;
                String id = childElement.getAttribute("id");

                List<OsmNode> refOsmNodes = new ArrayList<>();

                NodeList wayChildren = childNode.getChildNodes();
                for (int j=0; j<wayChildren.getLength(); j++) {
                    Node wayChild = wayChildren.item(j);
                    if ("nd".equals(wayChild.getNodeName())) {
                        Element wayChildElement = (Element)wayChild;
                        String refId = wayChildElement.getAttribute("ref");
                        OsmNode refOsmNode = getOsmNodeById(osm.nodes, refId);
                        refOsmNodes.add(refOsmNode);
                    }
                    // TODO: add tag handling.
                }

                OsmWay osmWay = new OsmWay(id, refOsmNodes);
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

    public void printOsm() {
        printOsmNodes(osm.nodes);
        for (OsmWay osmWay: osm.ways) {
            System.out.printf("<way id=%s>\n", osmWay.getId());
            printOsmNodes(osmWay.getOsmNodes(), 1);
            System.out.printf("</way>\n");
        }
    }

    void printOsmNodes(List<OsmNode> osmNodes) {
        printOsmNodes(osmNodes, 0);
    }

    void printOsmNodes(List<OsmNode> osmNodes, int indent) {
        for (OsmNode osmNode: osmNodes) {
            GeoCoordinate geoCoordinate = osmNode.getGeoCoordinate();
            for (int i=0; i<indent; i++) {
                System.out.print("  ");
            }
            System.out.printf("<node id=%s, lat=%f, lon=%f/>\n", osmNode.getId(), geoCoordinate.latitude, geoCoordinate.longitude);
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
