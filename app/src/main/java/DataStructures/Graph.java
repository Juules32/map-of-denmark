package DataStructures;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Stack;

import Utils.HelperMethods;
import javafx.scene.control.Alert;

public class Graph implements Serializable {
    
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

    public void addNode(long id, GraphNode node) {
        if(nodes.set(id, node)) ++V;
    }

    public void addUndirectedEdge(long fromId, long toId, int maxSpeed, boolean isCarAllowed) {
        addDirectedEdge(fromId, toId, maxSpeed, isCarAllowed);
        addDirectedEdge(toId, fromId, maxSpeed, isCarAllowed);
    }

    public void addDirectedEdge(long fromId, long toId, int maxSpeed, boolean isCarAllowed) {
        GraphNode fromNode = nodes.get(fromId);
        if(fromNode.edges == null) fromNode.edges = new ArrayList<Edge>(2);
        fromNode.edges.add(new Edge(toId, maxSpeed, isCarAllowed));
        ++E;
    }

    public void printEdge(long id) {
        if(nodes.get(id) == null) System.out.println("Node is not part of graph");
        else printEdge(nodes.get(id));
    }

    public void printEdge(GraphNode node) {
        System.out.println("lat: " + node.lat + ", lon: " + node.lon);
    }

    public void printEdges(long id) {
        if(nodes.get(id) == null) System.out.println("Node is not part of graph");
        else printEdges(nodes.get(id));
    }

    public void printEdges(GraphNode node) {
        if(node.edges != null) {
            System.out.println("The following Nodes are adjacent to node:");
            for (Edge edge : node.edges) {
                printEdge(edge.toId);
            }
        }
        else System.out.println("Node doesn't have any edges");
    }
    
    public void shortestPath(long startNodeId, long endNodeId, String type) {
        shortestPath(nodes.get(startNodeId), nodes.get(endNodeId), type);
    }

    public void shortestPath(long startNodeId, long endNodeId, boolean aStar, String type) {
        shortestPath(nodes.get(startNodeId), nodes.get(endNodeId), aStar, type, false);
    }

    public void shortestPath(GraphNode startNode, GraphNode endNode, String type) {
        shortestPath(startNode, endNode, true, type, false);
    }
    
    //Caluculates shortest distance between two graph nodes with or without A*
    public void shortestPath(GraphNode startNode, GraphNode endNode, boolean aStar, String type, boolean viewSearchedEdges) {
        
        //Used to calculate running time
        long startTime = System.currentTimeMillis();

        if(startNode == null || endNode == null || startNode.edges == null || endNode.edges == null) {
            System.out.println("Node(s) could not be found in graph!\n");
            fastestWay = null;
            searchedEdges = null;
            return;
        }

        System.out.println("ShortestPath is now running...");

        //Max speed of car
        int speed = 130;
        if (type.equals("Bicycle")) speed = 20;
        else if (type.equals("Walk")) speed = 5;
        boolean speedChanges = type.equals("Car");
        final double maxVehicleSpeed = speed;

        //Used for faster calculation
        final double inverseMaxVehicleSpeed = 1 / maxVehicleSpeed;

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
            GraphNode currentNode = pq.poll();

            //If end node is found, terminate successfully
            if (currentNode == endNode) {

                //Follow reachedFrom attribute to create way to be drawn + Calculates length
                double currentLength = 0;
                
                GraphNode currNode = endNode;

                infoBoxLat = currNode.lat;
                infoBoxLon = currNode.lon;

                while(currNode != startNode) {
                    if(currNode.lat < infoBoxLat) infoBoxLat = currNode.lat;
                    if(currNode.lon < infoBoxLon) infoBoxLon = currNode.lon;
                    nodesReached.add(currNode);
                    currentLength += HelperMethods.metersFromTo(currNode, currNode.reachedFrom);
                    currNode = currNode.reachedFrom;
                }
                nodesReached.add(currNode);

                //Set results accordingly
                double distance = currentLength;
                distanceStr = "" + ((double)(int) (distance*1000)/1000);
                timeStr = Integer.toString((int)(endNode.weightTo*111.139*60));
                vehicleStr = type.toLowerCase();

                //Print total distance to end node
                System.out.println("Shortest path in km: " + distance);

                //Clean up the graph so it can be used again
                cleanUp(startNode, endNode, viewSearchedEdges);

                //Print the total running time information
                System.out.println("Found shortest path " + (!aStar ? "without " : "") + 
                "using A* after " + ((double) System.currentTimeMillis()-startTime)/1000 + " seconds\n");
                
                //Set found way
                fastestWay = new Way(nodesReached, 100, 0, 0);
                return;
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

        //If end node is not found, clean up graph and return nothing
        cleanUp(startNode, endNode, viewSearchedEdges);
        String message = "Failed to find shortest path " + (!aStar ? "without " : "") + 
        "using A* after " + ((double) System.currentTimeMillis()-startTime)/1000 + " seconds\n";
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.show();
        System.out.println(message);
        fastestWay = null;
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

            //If 
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

                    if(viewSearchedEdges) {
                        foundEdges.add(currentNode);
                        foundEdges.add(toNode);
                        foundEdges.add(null);
                    }
                    
                }
            }
        }
        searchedEdges = new Way(foundEdges, 155, 155 , 155);
        System.out.println("Cleaned " + nodesCleaned + " nodes sucessfully!");
    }
}