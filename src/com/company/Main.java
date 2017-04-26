package com.company;

import com.company.data.Osm;

public class Main {

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.printf("Usage: myosm filename\n");
        } else {
            try {
                OsmReader osmReader = new OsmReader(args[0]);
                Osm osm = osmReader.parse();
                OsmReader.printOsm(osm);
                new OsmViewer(osm);
            }
            catch (Exception e) {
                System.out.printf(e.toString());
            }
        }
    }
}
