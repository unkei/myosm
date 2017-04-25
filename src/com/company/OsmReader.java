package com.company;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class OsmReader {

    private String filename;

    OsmReader(String filename) {
        setFilename(filename);
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    void parse() throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = factory.newDocumentBuilder();
        Document document = documentBuilder.parse(filename);

        Node root = document.getDocumentElement();
        printNode(0, root);
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
