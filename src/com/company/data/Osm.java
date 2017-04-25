package com.company.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by unkei on 2017/04/25.
 */
public class Osm {
    public List<OsmNode> nodes;
    public List<OsmWay> ways;

    public Osm() {
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
    }

    public void clear() {
        nodes.clear();
        ways.clear();
    }
}
