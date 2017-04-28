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

    public void addOsmWay(OsmWay way) {

        if (way == null || way.children == null) return;

        List<? extends OsmElement> newChildren = way.children;
        if (this.children == null) {
            this.children = newChildren;
        } else {
            List<OsmElement> combinedNodes = new ArrayList<>(this.children);
            for (OsmElement element: newChildren) {
                combinedNodes.add(element);
            }
            this.children = combinedNodes;
        }
    }

    public boolean isFollowedBy(OsmWay way) {
        if (this.children != null && this.children.size() > 0 && way != null && way.children != null && way.children.size() > 0) {
            OsmNode node = (OsmNode)way.children.get(0);
            if (node != null && node.getId() != null) {
                return node.getId().equals(this.children.get(this.children.size() - 1).getId());
            }
        }
        return false;
    }

    public boolean isClosed() {
        // TODO: consider the edge case, where way is attached to the edges and it is closed with the edges.
        if (children != null && children.size() > 3) {
            OsmElement first = children.get(0);
            OsmElement last = children.get(children.size() - 1);
            if (first != null && last != null) {
                String firstId = first.getId();
                String lastId = last.getId();
                if (firstId != null && lastId != null && firstId.equals(lastId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static OsmWay getOsmWayById(List<OsmWay> ways, String id) {
        return (OsmWay) getOsmElementById(ways, id);
    }
}
