package DataStructures;

import java.util.ArrayList;

public class GraphNode extends Node {
    //A GraphNode also has:
    //A list of edges
    public ArrayList<Edge> edges = null;

    //A node it was reached from
    public GraphNode reachedFrom = null;

    //A weight to from dijkstra call
    public double weightTo = Double.POSITIVE_INFINITY;

    public GraphNode(Node node) {
        super(node.lat, node.lon);
    }
}
