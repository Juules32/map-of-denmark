package DataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

import Utils.HelperMethods;

public class Graph implements Serializable {
    
    //The nodes in the graph
    public Trie<GraphNode> nodes;

    //Amount of verteces in the graph
    private long V = 0;

    //Amount of edges in the graph
    private long E = 0;

    //Default speed value
    public final int DEFAULT_SPEED = 80;

    //Current information found with dijkstra
    public Way fastestWay = null;
    public Way searchedEdges = null;
    public String distanceStr, timeStr, vehicleStr;
    public double infoBoxLat, infoBoxLon;

    public Graph() {
        this.nodes = new Trie<GraphNode>();
    }

    public Graph(Trie<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public long getV() {return V;}
    public long getE() {return E;}

    //Adds node to the graph
    public void addNode(long id, GraphNode node) {
        if(nodes.set(id, node)) ++V;
    }

    //Adds undirected edge between two nodes
    public void addUndirectedEdge(long fromId, long toId, int maxSpeed, boolean isCarAllowed) {
        addDirectedEdge(fromId, toId, maxSpeed, isCarAllowed);
        addDirectedEdge(toId, fromId, maxSpeed, isCarAllowed);
    }

    //Adds directed edge between from one node to another
    public void addDirectedEdge(long fromId, long toId, int maxSpeed, boolean isCarAllowed) {
        GraphNode fromNode = nodes.get(fromId);
        if(fromNode.edges == null) fromNode.edges = new ArrayList<Edge>(2);
        fromNode.edges.add(new Edge(toId, maxSpeed, isCarAllowed));
        ++E;
    }

    //Helper methods for printing Node and Edge information
    public boolean printNode(long id) {
        if(nodes.get(id) == null) {
            System.out.println("Node is not part of graph");
            return false;
        }
        else {
            printNode(nodes.get(id));
            return true;
        }
    }

    public void printNode(GraphNode node) {
        System.out.println("lat: " + node.lat + ", lon: " + node.lon);
    }

    public boolean printEdges(long id) {
        if(nodes.get(id) == null) {
            System.out.println("Node is not part of graph");
            return false;
        }
        else {
            return printEdges(nodes.get(id));
        }
    }

    public boolean printEdges(GraphNode node) {
        if(node.edges == null) {
            System.out.println("Node doesn't have any edges");
            return false;
        }
        else {
            System.out.println("The following Nodes are adjacent to node:");

            boolean result = true;
            for (Edge edge : node.edges) {
                if(!printNode(edge.toId)) result = false;
            }
            return result;
        }
    }
    
    public boolean shortestPath(long startNodeId, long endNodeId, String type) {
        return shortestPath(nodes.get(startNodeId), nodes.get(endNodeId), type);
    }

    public boolean shortestPath(long startNodeId, long endNodeId, boolean aStar, String type) {
        return shortestPath(nodes.get(startNodeId), nodes.get(endNodeId), aStar, type, false);
    }

    public boolean shortestPath(GraphNode startNode, GraphNode endNode, String type) {
        return shortestPath(startNode, endNode, true, type, false);
    }
    
    //Caluculates shortest distance between two graph nodes with or without A*
    public boolean shortestPath(GraphNode startNode, GraphNode endNode, boolean aStar, String type, boolean viewSearchedEdges) {
        if(startNode == null || endNode == null || startNode.edges == null || endNode.edges == null) {
            System.out.println("Node(s) could not be found in graph!\n");
            fastestWay = null;
            return false;
        }

        System.out.println("ShortestPath is now running...");

        //Speed is by default car maximum speed
        int speed = 130;
        if (type.equals("Bicycle")) speed = 20;
        else if (type.equals("Walk")) speed = 5;
        final double maxVehicleSpeed = speed;

        //Used for faster calculation
        final double inverseMaxVehicleSpeed = 1 / maxVehicleSpeed;

        //If transport type is car, speed is variable
        boolean speedChanges = type.equals("Car");

        //Used for finding weight
        double currentNodeWeight;
        
        //Used to visualize found path
        ArrayList<Node> nodesReached = new ArrayList<>();
    
        //PriorityQueue used to see which node should be considered next
        PriorityQueue<GraphNode> pq;

        //Without A*, the compare method exclusively compares distance or time from start node
        if(!aStar) {
            pq = new PriorityQueue<>(10, new Comparator<GraphNode>() {
                @Override
                public int compare(GraphNode o1, GraphNode o2) {
                    if (o1.weightTo < o2.weightTo) return -1;
                    if (o1.weightTo > o2.weightTo) return 1;
                    return 0;
                }
            });
        }

        //Otherwise, run with A* with added minimum distance or time to endNode
        else {
            pq = new PriorityQueue<>(10, new Comparator<GraphNode>() {
                @Override
                public int compare(GraphNode o1, GraphNode o2) {
                    if (o1.weightTo + HelperMethods.distFromTo(o1, endNode) * inverseMaxVehicleSpeed < 
                        o2.weightTo + HelperMethods.distFromTo(o2, endNode) * inverseMaxVehicleSpeed) 
                    return -1;
                    if (o1.weightTo + HelperMethods.distFromTo(o1, endNode) * inverseMaxVehicleSpeed > 
                        o2.weightTo + HelperMethods.distFromTo(o2, endNode) * inverseMaxVehicleSpeed) 
                    return 1;
                    return 0;
                }
            });
        }
        
        //Start node distance to is initialized and added to priority queue
        startNode.weightTo = 0;
        pq.add(startNode);

        //The main part of dijkstra's algorithm
        while (!pq.isEmpty()) {

            //The current node is set to the first element in the priority queue
            GraphNode currentNode = pq.poll();

            //If end node is found, terminate successfully
            if (currentNode == endNode) {

                //Follow reachedFrom attribute to create way to be drawn + Calculates length
                double currentLength = 0;
                
                GraphNode currNode = endNode;

                infoBoxLat = currNode.lat;
                infoBoxLon = currNode.lon;

                while(currNode != startNode) {

                    //Updates information box position based on route
                    if(currNode.lat < infoBoxLat) infoBoxLat = currNode.lat;
                    if(currNode.lon < infoBoxLon) infoBoxLon = currNode.lon;

                    //Adds node to resulting way
                    nodesReached.add(currNode);

                    //The length of the way is updated
                    currentLength += HelperMethods.metersFromTo(currNode, currNode.reachedFrom);
                    
                    //The current node is set to the next node in the chain
                    currNode = currNode.reachedFrom;
                }

                //The final node is added
                nodesReached.add(currNode);

                //Results are set accordingly
                double distance = currentLength;
                distanceStr = "" + ((double)(int) (distance*1000)/1000);
                timeStr = Integer.toString((int)(endNode.weightTo*111.139*60));
                vehicleStr = type.toLowerCase();

                //Graph is cleaned so it can be used again
                cleanUp(startNode, endNode, viewSearchedEdges);
                
                //Found way is set
                fastestWay = new Way(nodesReached, 100, 0, 0);
                return true;
            }

            //If a dead end is found, continue
            if(currentNode.edges == null) continue;

            //Add adjacent node to pq if its weight is larger
            for (Edge edge : currentNode.edges) {

                GraphNode toNode = nodes.get(edge.toId);

                //If traveling by car, weight depends on .maxSpeed
                if (speedChanges) currentNodeWeight = currentNode.weightTo + HelperMethods.distFromTo(currentNode, toNode) / edge.maxSpeed;
                
                //Otherwise, weight depends on the fixed maximum speed
                else {
                    
                    //Edge is not considered if cycling or walking on road with max speed 100 or more
                    if(edge.maxSpeed >= 100) continue;
                    currentNodeWeight = currentNode.weightTo + HelperMethods.distFromTo(currentNode, toNode) * inverseMaxVehicleSpeed;
                }
                if (currentNodeWeight < toNode.weightTo) {
                    toNode.weightTo = currentNodeWeight;
                    toNode.reachedFrom = currentNode;
                    pq.add(toNode);
                }
            }
        }

        //If end node is not found, clean up graph and return false
        cleanUp(startNode, endNode, viewSearchedEdges);
        fastestWay = null;
        return false;
    }
    
    //Performs DFS on all nodes used in dijkstra call and resets them
    private void cleanUp(GraphNode startNode, GraphNode endNode, boolean viewSearchedEdges) {
        Stack<GraphNode> stack = new Stack<GraphNode>();
        ArrayList<Node> foundEdges = new ArrayList<>();

        startNode.weightTo = Double.POSITIVE_INFINITY;
        startNode.reachedFrom = null;
        stack.push(startNode);

        int nodesCleaned = 0;

        while (!stack.isEmpty()) {
            GraphNode currentNode = stack.pop();

            //If a dead end is found, reset the node and continue
            if(currentNode.edges == null) {
                currentNode.weightTo = Double.POSITIVE_INFINITY;
                currentNode.reachedFrom = null;
                continue;
            }

            // Iterates through all edges adjacent to the node
            for (Edge edge : currentNode.edges) {
                GraphNode toNode = nodes.get(edge.toId);
                if (toNode.weightTo != Double.POSITIVE_INFINITY) {
                    toNode.weightTo = Double.POSITIVE_INFINITY;
                    toNode.reachedFrom = null;
                    ++nodesCleaned;
                    stack.push(toNode);

                    //If all searched edges are to be viewed, add current link
                    if(viewSearchedEdges) {
                        foundEdges.add(currentNode);
                        foundEdges.add(toNode);
                        foundEdges.add(null);
                    }
                    
                }
            }
        }

        //Make a way from found edges
        searchedEdges = new Way(foundEdges, 155, 155, 155);
        
        System.out.println("Cleaned " + nodesCleaned + " nodes sucessfully!");
    }
}