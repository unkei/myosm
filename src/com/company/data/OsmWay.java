package com.company.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by unkei on 2017/04/25.
 */
public class OsmWay extends OsmElement {

    public OsmWay(String id, List<OsmNode> children, HashMap<String, String> tags) {
        super(id, children, tags);
    }

    public List<OsmNode> getOsmNodes() {
        return (List<OsmNode>)this.children;
    }

    public static OsmWay getOsmWayById(List<OsmWay> ways, String id) {
        return (OsmWay) getOsmElementById(ways, id);
    }
}
