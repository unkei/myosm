package com.company;

public class Main {

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.printf("Usage: myosm filename\n");
        } else {
            OsmReader osmReader = new OsmReader(args[0]);
            try {
                osmReader.parse();
                osmReader.printOsm();
            }
            catch (Exception e) {
                System.out.printf(e.toString());
            }
        }
    }
}
