package DataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import Utils.HelperMethods;
import javafx.scene.canvas.GraphicsContext;

public class RTrees implements Serializable {

    ArrayList<RTree> innerList = new ArrayList<>(10);

    public void add(List<Way> ways, boolean isLine) {
        innerList.add(new RTree(ways, isLine));
    }

    public RTree get(int index) {
        return innerList.get(index);
    }

    public Node nearestNeighbor(Node node) {
        return nearestNeighbor(node.lat, node.lon);
    }

    public Node nearestNeighbor(double lat, double lon) {
        
        //Initializes nearest node as a node an infinite distance away
        Node nearestNode = new Node(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        
        //Finds nearest node of all priorities
        for (int i = 1; i < innerList.size(); i++) {
            RTree curr = innerList.get(i);
            if(curr.ways.size() > 0) {
                Node found = curr.nearestNeighbor(curr.root, lat, lon, nearestNode, Double.POSITIVE_INFINITY);
                if(HelperMethods.distFromTo(lat, lon, found) < HelperMethods.distFromTo(lat, lon, nearestNode)) {
                    nearestNode = found;
                }
            }
        }
        return nearestNode;
    }

    public void rangeSearch(double[] viewBox, int currentDetailLevel, String theme, GraphicsContext gc) {

        //Performs range search on all prioritized layers
        for (int i = 0; i <= currentDetailLevel; i++) {
            RTree rTree = innerList.get(i);
            rTree.rangeSearch(rTree.root, viewBox, theme, gc);
        }
    }

    public void boxSearch(double[] viewBox, int currentDetailLevel, GraphicsContext gc) {
        
        //Performs box search on all prioritized layers
        for (int i = 0; i <= currentDetailLevel; i++) {
            RTree rTree = innerList.get(i);
            rTree.boxSearch(rTree.root, viewBox, gc);
        }
    }
}
