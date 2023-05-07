package OldFiles;
/*
import javafx.scene.canvas.GraphicsContext;

import java.awt.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.PriorityQueue;

import DataStructures.Graph;
import DataStructures.GraphNode;
import DataStructures.Node;
import Model.Model;
import javafx.scene.paint.Color;
*/

public class ShortestPath {
    /*
    private Graph graph;
    private GraphNode startNode;
    private GraphNode endNode;

    private ArrayList<Node> nodesReached = new ArrayList<>();


    public ShortestPath(Model model, long startNodeID, long endNodeID, GraphicsContext gc) {
        Boolean aStar = true;
        this.startNode = model.graph.nodes.get(startNodeID);
        this.endNode = model.graph.nodes.get(endNodeID);
        this.graph = model.graph;
        //keys = graph.getKeys();           // index  -> id

        startNode.distTo = 0;

        PriorityQueue<GraphNode> pq = new PriorityQueue<>(new Comparator<GraphNode>() {
            @Override
            public int compare(GraphNode o1, GraphNode o2) {
                if (o1.distTo < o2.distTo) return -1;
                if (o1.distTo > o2.distTo) return 1;
                return 0;
            }
        });

        pq.add(startNode);

        

        while (!pq.isEmpty()) {
            GraphNode currentNode = pq.poll();
            if (currentNode == endNode) {
                System.out.println("Shortest path in km: " + endNode.distTo*111.139);
                GraphNode currNode = endNode;
                while(currNode != startNode) {
                    nodesReached.add(currNode);
                    currNode = currNode.reachedFrom;
                }
                return;
            }

            // Iterates through all edges adjacent with the node
            for (GraphNode node : currentNode.edges)
                if (!aStar) {
                    if (node.distTo > currentNode.distTo + graph.distFromTo(node, currentNode)) {
                        node.distTo = currentNode.distTo + graph.distFromTo(node, currentNode);
                        node.reachedFrom = currentNode;
                        pq.add(node);
                        //System.out.println("distTo: " + distTo[edge.other(currentNode)] + ", nodeIndex: " + edge.other(currentNode) + " - Reached: " + nodesReached.size() + ", reReaches: " + nodesReReached);
                    }
                } else{ //  If aStar is true:
                    if(node.distTo + graph.distFromTo(node, endNode) > currentNode.distTo + graph.distFromTo(node, currentNode) + graph.distFromTo(currentNode, endNode)) {
                        node.distTo = currentNode.distTo + graph.distFromTo(node, currentNode);
                        //reachedFrom[edge.other(currentNode)] = currentNode;
                        node.reachedFrom = currentNode;
                        pq.add(node);
                        //System.out.println("distTo: " + distTo[edge.other(currentNode)] + ", nodeIndex: " + edge.other(currentNode) + " - Reached: " + nodesReached.size() + ", reReaches: " + nodesReReached);
                    }
            }
        }

        
        
    }



    
    public ShortestPath(Model model, long startNode, GraphicsContext gc){
        this.graph = model.graph;
        distTo = new double[graph.getST().size()];
        edgeTo = new Edge[graph.getST().size()];
        //nodesDistTo = new HashMap<>();

        distTo[0] = 0.00000001;


        //HashMap<Long, Integer> st = graph.getST();
        //int currentNode = graph.getST().get(startNode.id);
        MinPQ<Double> nodeIndexQueue = new MinPQ<Double>(1);     // PQ is sorted by double "distTo" but functions return the node-Index
        nodeIndexQueue.insert(distTo[0], graph.getST().get(startNode));

        long[] keys = graph.getKeys();           // index  -> id
        Trie<Node> id2Node = model.id2node;
        nodesReached = new ArrayList<>();
        nodesReReached = 0;
        while (!nodeIndexQueue.isEmpty()) {
            int currentNode = nodeIndexQueue.delMin();
            if (keys[currentNode] == endNode) break;

            if (nodesReached.contains(currentNode)) nodesReReached++;
            else nodesReached.add(currentNode);

            // Iterates through all edges adjacent with the node
            for (Edge edge : graph.graph().adj(currentNode)) {
                if (!aStar) {
                    if (distTo[edge.other(currentNode)] == 0.0 || distTo[edge.other(currentNode)] > distTo[currentNode] + edge.weight()) {
                        distTo[edge.other(currentNode)] = distTo[currentNode] + edge.weight();
                        //reachedFrom[edge.other(currentNode)] = currentNode;
                        edgeTo[edge.other(currentNode)] = edge;
                        nodeIndexQueue.insert(distTo[edge.other(currentNode)], edge.other(currentNode));
                        //System.out.println("distTo: " + distTo[edge.other(currentNode)] + ", nodeIndex: " + edge.other(currentNode) + " - Reached: " + nodesReached.size() + ", reReaches: " + nodesReReached);
                    }
                } else{ //  If aStar is true:
                    if (distTo[edge.other(currentNode)] == 0.0 || distTo[edge.other(currentNode)] + euDist(edge.other(currentNode)) > distTo[currentNode] + edge.weight() + euDist(currentNode)) {
                        distTo[edge.other(currentNode)] = distTo[currentNode] + edge.weight();
                        //reachedFrom[edge.other(currentNode)] = currentNode;
                        edgeTo[edge.other(currentNode)] = edge;
                        nodeIndexQueue.insert(distTo[edge.other(currentNode)] + euDist(edge.other(currentNode)), edge.other(currentNode));
                        //System.out.println("distTo: " + distTo[edge.other(currentNode)] + ", nodeIndex: " + edge.other(currentNode) + " - Reached: " + nodesReached.size() + ", reReaches: " + nodesReReached);
                    }
                }
            }
        }
        System.out.println("Shortest path in km: " + distTo[graph.getST().get(endNode)]);
        pathNodes = new ArrayList<Node>();
        buildPath(startNode, endNode);
    } 

      

        private void buildPath ( long startNode, long endNode){
            pathNodes.add(id2Node.get(endNode));
            if (startNode != endNode) {
                Edge currentEdgeTo = edgeTo[graph.getST().get(endNode)];
                buildPath(startNode, keys[currentEdgeTo.other(graph.getST().get(endNode))]);
            }
        } 

        //  Returns the air-distance / Euclidean distance from given node to endNode in km
        
        



    private double getShortestPath(long endNode){
        double pathLength;
        Edge edgeTo = new Edge(0,0,0.0);
        while(edgeTo != null){

        }


        return 0.0;
    }

    int counter;
    private double path(int nodeIndex){
        System.out.println(counter++);
        if (edgeTo[nodeIndex] == null) return 0;
        return edgeTo[nodeIndex].weight() + path(edgeTo[nodeIndex].other(nodeIndex));
    } 
*/
}
