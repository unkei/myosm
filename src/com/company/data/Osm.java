package com.company.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by unkei on 2017/04/25.
 */
public class Osm {
    public OsmBounds bounds;
    public List<OsmNode> nodes;
    public List<OsmWay> ways;
    public List<OsmRelation> relations;

    public Osm() {
        nodes = new ArrayList<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();
    }

    public void clear() {
        nodes.clear();
        ways.clear();
        relations.clear();
    }
}
