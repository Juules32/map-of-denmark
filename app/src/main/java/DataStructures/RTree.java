package DataStructures;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import Utils.HelperMethods;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class RTree implements Serializable {
    
    //All the ways to be sorted
    public List<Way> ways;

    //Draws lines if true, draws areas if false
    boolean isLine;
    
    //Amount of ways per leaf
    int cutoffPoint;
    
    //The root node
    public RTreeNode root;

    public RTree(List<Way> ways, boolean isLine) {
        this.ways = ways;
        this.isLine = isLine;
        this.cutoffPoint = 1;
        this.root = new RTreeNode(0, ways.size(), true);
    }

    //Inner class used recursively for RTree nodes
    private class RTreeNode implements Serializable {

        //Indeces of the sorted ways at the leaves of the graph
        int[] leafIndeces;

        //The two subtrees
        RTreeNode left, right;

        //The minimum bounding box of a node in the RTree
        double[] boundingBox;

        //Contructor, which sorts indeces in the right leaves
        RTreeNode(int lo, int hi, boolean dimX) {
            boundingBox = new double[4];
            leafIndeces = new int[2];

            
    
            //If illegal indexing is used return null
            if(lo >= hi) {
                leafIndeces = null;
                left = null;
                right = null;
                return;
            }
    
            //The amount of ways currently looked at
            int n = hi - lo;
    
            //The middle point of n
            int m = n / 2;
    
            //ways is sorted from lo to hi in the current 'direction' (lat or lon)
            //Note: alternatively, partition can be used for faster initialization
            Collections.sort(ways.subList(lo, hi), new Comparator<Way>() {
                @Override
                public int compare(Way o1, Way o2) {
                    Float a, b;
    
                    //Note: nodes[nodesl.length/2] virker ikke pga. null-værdier
                    Node o1RepNode = o1.nodes[0];
                    Node o2RepNode = o2.nodes[0];
                    if(dimX) {
                        a = (Float) o1RepNode.lat;
                        b = (Float) o2RepNode.lat;
                    } else {
                        a = (Float) o1RepNode.lon;
                        b = (Float) o2RepNode.lon;
                    }
                    return a.compareTo(b);
                }
            });
            
            //If n is greater than the cutoff point, call left and right recursively 
            if(n > cutoffPoint) {
                left = new RTreeNode(lo, m+lo, !dimX);
                right = new RTreeNode(m+lo, hi, !dimX);
                leafIndeces = null;
                combineBoundingBoxes(this, left.boundingBox, right.boundingBox);
            }
    
            //Else stop the recursion and save current indeces and find bounding box
            else {
                left = null;
                right = null;
                leafIndeces[0] = lo;
                leafIndeces[1] = hi;
                findBoundingBox(this, lo, hi);
            }
        }
    }


    //Used to only show visible elements (that intersect with viewBox)
    public void rangeSearch(RTreeNode rtreeNode, double[] viewBox, String theme, GraphicsContext gc) {
        RTreeNode left = rtreeNode.left;
        RTreeNode right = rtreeNode.right;
        int[] leafIndeces = rtreeNode.leafIndeces;
        //If there are more subtrees, call range search recursively
        if (left != null && inBox(rtreeNode, viewBox)) rangeSearch(left, viewBox, theme, gc);
        if (right != null && inBox(rtreeNode, viewBox)) rangeSearch(right, viewBox, theme, gc);
        

        //If leaf is found, draw all ways between the indeces
        if (leafIndeces != null && inBox(rtreeNode, viewBox)) {
            for (int i = leafIndeces[0]; i < leafIndeces[1]; i++) {
                HelperMethods.drawWay(ways.get(i), isLine, theme, gc);
            }
        }
    }


    //Draws the bounding boxes of visible elements
    public void boxSearch(RTreeNode rtreeNode, double[] viewBox, GraphicsContext gc) {
        RTreeNode left = rtreeNode.left;
        RTreeNode right = rtreeNode.right;
        double[] boundingBox = rtreeNode.boundingBox;
        if (left != null && inBox(rtreeNode, viewBox)) boxSearch(left, viewBox, gc);
        if (right != null && inBox(rtreeNode, viewBox)) boxSearch(right, viewBox, gc);
        if (rtreeNode.leafIndeces != null && inBox(rtreeNode, viewBox)) {
            gc.beginPath();
            gc.moveTo(boundingBox[2], boundingBox[0]);
            gc.lineTo(boundingBox[2], boundingBox[1]);
            gc.lineTo(boundingBox[3], boundingBox[1]);
            gc.lineTo(boundingBox[3], boundingBox[0]);
            gc.lineTo(boundingBox[2], boundingBox[0]);
            gc.setStroke(Color.RED);
            gc.stroke();
        }
    }

    //Returns whether bounding box overlaps with visible rectangle (viewBox)
    private boolean inBox(RTreeNode rtreeNode, double[] viewBox) {
        double[] boundingBox = rtreeNode.boundingBox;
        if (viewBox[0] < boundingBox[1] && viewBox[1] > boundingBox[0] &&
        viewBox[2] < boundingBox[3] && viewBox[3] > boundingBox[2]) {
            return true;
        }
        else return false;
    }

    //Finds the bounding box of a node in the RTree
    private void findBoundingBox(RTreeNode rtreeNode, int start, int finish) {
        double[] boundingBox = rtreeNode.boundingBox;
        //Initializes bounding box as the first node in the first way
        Node[] nodes = ways.get(start).nodes;
        boundingBox[0] = nodes[0].lat;
        boundingBox[1] = nodes[0].lat;
        boundingBox[2] = nodes[0].lon;
        boundingBox[3] = nodes[0].lon;
        
        //Loops through all nodes in all ways between indeces to find bounding box
        for (int i = start; i < finish; i++) {
            for (Node node : ways.get(i).nodes) {
                if(node == null) continue;
                if(node.lat < boundingBox[0]) {
                    boundingBox[0] = node.lat;
                } else if (node.lat > boundingBox[1]) {
                    boundingBox[1] = node.lat;
                }
                if(node.lon < boundingBox[2]) {
                    boundingBox[2] = node.lon;
                } else if (node.lon > boundingBox[3]) {
                    boundingBox[3] = node.lon;
                }
            }
        }
    }

    //Used to find the parent bounding box from the two children's bounding boxes
    private void combineBoundingBoxes(RTreeNode rtreeNode, double[] box1, double[] box2) {
        double[] boundingBox = rtreeNode.boundingBox;
        boundingBox[0] = box1[0] < box2[0] ? box1[0] : box2[0];
        boundingBox[1] = box1[1] > box2[1] ? box1[1] : box2[1];
        boundingBox[2] = box1[2] < box2[2] ? box1[2] : box2[2];
        boundingBox[3] = box1[3] > box2[3] ? box1[3] : box2[3];
    }

    //Recursively searches the tree to find the nearest node to point of origin
    public Node nearestNeighbor(RTreeNode rtreeNode, double lat, double lon, Node nearestNode, double currDist) {
        
        RTreeNode left = rtreeNode.left;
        RTreeNode right = rtreeNode.right;
        int[] leafIndeces = rtreeNode.leafIndeces;
        
        if (left != null ) {
            if(getDistToBoundingBox(lat, lon, left) < currDist) {
                nearestNode = nearestNeighbor(left, lat, lon, nearestNode, currDist);
                currDist = HelperMethods.distFromTo(lat, lon, nearestNode);
            }
        }

        if (right != null) {
            if(getDistToBoundingBox(lat, lon, right) < currDist) {
                nearestNode = nearestNeighbor(right, lat, lon, nearestNode, currDist);
                currDist = HelperMethods.distFromTo(lat, lon, nearestNode);
            }
        }

        if (leafIndeces != null) {
            double currNewDist = Double.POSITIVE_INFINITY;
            Node currNearestNode = ways.get(leafIndeces[0]).nodes[0];
            for (int i = leafIndeces[0]; i < leafIndeces[1]; i++)
            {
                Way way = ways.get(i);

                for (Node node : way.nodes) {
                    double dist = HelperMethods.distFromTo(lat, lon, node);
                    if(dist < currNewDist) {
                        currNewDist = dist;
                        currNearestNode = node;
                    }
                }
                
            }
            if(currNewDist < currDist)
                {
                    nearestNode = currNearestNode;
                    currDist = currNewDist;
                }
        }
        return nearestNode;
    }

    //Finds distancee to closest edge of a bounding box
    private double getDistToBoundingBox(double lat, double lon, RTreeNode box)
    {
        double latStart = box.boundingBox[0];
        double latFin = box.boundingBox[1];
        double lonStart = box.boundingBox[2];
        double lonFin = box.boundingBox[3];
        
        double[] fan = {lat, lat, lon, lon};
        if(inBox(box, fan)) {
            return 0;
        }
        else if(lat < latStart && lon > lonFin) {
            return HelperMethods.distFromTo(lat, lon, latStart, lonFin);
        }
        else if(lat > latStart && lon > lonFin) {
            return HelperMethods.distFromTo(lat, lon, latFin, lonFin);
        }
        else if(lat > latStart && lon < lonFin) {
            return HelperMethods.distFromTo(lat, lon, latFin, lonStart);
        }
        else if(lat < latStart && lon < lonFin) {
            return HelperMethods.distFromTo(lat, lon, latStart, lonStart);
        }
        else if(lon > lonStart && lon < lonFin) {
            if(lat < latStart) return HelperMethods.distFromTo(lat, lon, latStart, lon);
            else return HelperMethods.distFromTo(lat, lon, latFin, lon);
        }
        else if(lat > latStart && lat < latFin) {
            if(lon < lonStart) return HelperMethods.distFromTo(lat, lon, lat, lonStart);
            else return HelperMethods.distFromTo(lat, lon, lat, lonFin);
        }

        return Double.POSITIVE_INFINITY;
    }

}
