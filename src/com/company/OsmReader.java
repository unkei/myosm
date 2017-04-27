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

        List<Element> relationElements = new ArrayList<>();

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
                        OsmNode refOsmNode = OsmNode.getOsmNodeById(osm.nodes, refId);
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
                Element childElement = (Element)childNode;
                String id = childElement.getAttribute("id");

                HashMap<String, String> osmTags = new HashMap<>();

                NodeList relationChildren = childElement.getChildNodes();
                for (int j = 0; j < relationChildren.getLength(); j++) {
                    Node relationChild = relationChildren.item(j);
                    if ("tag".equals(relationChild.getNodeName())) {
                        Element nodeChildElement = (Element) relationChild;
                        String k = nodeChildElement.getAttribute("k");
                        String v = nodeChildElement.getAttribute("v");
                        osmTags.put(k, v);
                    }
                }

                OsmRelation osmRelation = new OsmRelation(id, null, osmTags);
                osm.relations.add(osmRelation);
                relationElements.add(childElement);
            }
        }

        // Relation 2nd pass
        for (Element relationElement: relationElements) {
            String id = relationElement.getAttribute("id");

            OsmRelation osmRelation = OsmRelation.getOsmRelationById(osm.relations, id);
            List<OsmElement> osmMembers = new ArrayList<>();

            HashMap<String, String> tags = osmRelation.getTags();
            String highway = tags.get("highway");
            String natural = tags.get("natural");
            String building = tags.get("building");
            String building_part = tags.get("building:part");
            String landuse = tags.get("landuse");

            NodeList relationChildren = relationElement.getChildNodes();
            for (int j = 0; j < relationChildren.getLength(); j++) {
                Node relationChild = relationChildren.item(j);
                if ("member".equals(relationChild.getNodeName())) {
                    Element relationChildElement = (Element) relationChild;
                    String type = relationChildElement.getAttribute("type");
                    String ref = relationChildElement.getAttribute("ref");
                    String role = relationChildElement.getAttribute("role");
                    OsmElement member = null;
                    if ("node".equals(type)) {
                        member = OsmNode.getOsmNodeById(osm.nodes, ref);
                    } else if ("way".equals(type)) {
                        member = OsmWay.getOsmWayById(osm.ways, ref);
                        if (member != null) {
                            HashMap<String, String> memberTags = member.getTags();
                            if (highway != null) {
                                memberTags.put("highway", highway);
                            }
                            if (natural != null) {
                                memberTags.put("natural", natural);
                            }
                            if (building != null) {
                                memberTags.put("building", building);
                            }
                            if (building_part != null) {
                                memberTags.put("building:part", building_part);
                            }
                            if (landuse != null) {
                                memberTags.put("landuse", landuse);
                            }
                        }
                    } else if ("relation".equals(type)) {
                        member = OsmRelation.getOsmRelationById(osm.relations, ref);
                    }
                    if (member != null) {
                        osmMembers.add(member);
                    }
                }
            }

            osmRelation.addChildren(osmMembers);
        }

        return osm;
    }

    public static void printOsm(Osm osm) {
        printOsmNodes(osm.nodes);
        printOsmWays(osm.ways);
        printOsmRelations(osm.relations);
    }

    static void printTags(HashMap<String, String> tags, int indent) {
        for (String k : tags.keySet()) {
            String v = tags.get(k);
            printIndent(indent);
            System.out.printf("%s=%s\n", k, v);
        }
    }

    static void printOsmNodes(List<OsmNode> osmNodes) {
        printOsmNodes(osmNodes, 0);
    }

    static void printOsmNodes(List<OsmNode> osmNodes, int indent) {
        for (OsmNode osmNode: osmNodes) {
            printOsmNode(osmNode, indent);
        }
    }

    static void printOsmNode(OsmNode osmNode, int indent) {
        GeoCoordinate geoCoordinate = osmNode.getGeoCoordinate();
        printIndent(indent);
        System.out.printf("<node id=%s, lat=%f, lon=%f>\n", osmNode.getId(), geoCoordinate.latitude, geoCoordinate.longitude);
        printTags(osmNode.getTags(), indent + 1);
    }

    static void printOsmWays(List<OsmWay> osmWays) {
        printOsmWays(osmWays, 0);
    }

    static void printOsmWays(List<OsmWay> osmWays, int indent) {
        for (OsmWay osmWay: osmWays) {
            printOsmWay(osmWay, indent);
        }
    }

    static void printOsmWay(OsmWay osmWay, int indent) {
        printIndent(indent);
        System.out.printf("<way id=%s>\n", osmWay.getId());
        printOsmNodes(osmWay.getOsmNodes(), indent + 1);
        printTags(osmWay.getTags(), indent + 1);
    }

    static void printOsmRelations(List<OsmRelation> osmRelations) {
        printOsmRelations(osmRelations, 0);
    }

    static void printOsmRelations(List<OsmRelation> osmRelations, int indent) {
        for (OsmRelation osmRelation: osmRelations) {
            printOsmRelation(osmRelation, indent);
        }
    }

    static void printOsmRelation(OsmRelation osmRelation, int indent) {
        printIndent(indent);
        System.out.printf("<relation id=%s>\n", osmRelation.getId());
        printOsmElements(osmRelation.getChildren(), indent + 1);
        printTags(osmRelation.getTags(), indent + 1);
    }

    static void printOsmElements(List<? extends OsmElement> osmElements, int indent) {
        for (OsmElement osmElement: osmElements) {
            if (osmElement.getClass() == OsmNode.class) {
                printOsmNode((OsmNode)osmElement, indent);
            } else if (osmElement.getClass() == OsmWay.class) {
                printOsmWay((OsmWay) osmElement, indent);
            } else if (osmElement.getClass() == OsmRelation.class) {
                printOsmRelation((OsmRelation) osmElement, indent);
            }
        }
    }

    static void printIndent(int indent) {
        for (int i=0; i<indent; i++) {
            System.out.print("  ");
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
