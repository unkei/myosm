package com.company.data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by unkei on 2017/04/26.
 */
public class OsmRelation extends OsmElement {
    public OsmRelation(String id, List<? extends OsmElement>children, HashMap<String, String>tags) {
        super(id, children, tags);
    }

    public static OsmRelation getOsmRelationById(List<OsmRelation> relations, String id) {
        return (OsmRelation) getOsmElementById(relations, id);
    }
}
