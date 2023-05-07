import DataStructures.Graph;
import DataStructures.GraphNode;
import DataStructures.Node;
import Utils.HelperMethods;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test void testGetEV(){
        Graph graph = new Graph();
        assertEquals(0, graph.getE());
        assertEquals(0, graph.getV());
    }

    @Test void testAddNode(){
        Graph graph = new Graph();
        Node node = new Node(1,1);
        GraphNode graphNode = new GraphNode(node);
        graph.addNode(1, graphNode);
        graph.addNode(1, graphNode);
        assertEquals(0, graph.getE());
        assertEquals(1, graph.getV());
    }

    @Test void testAddEdge(){
        Graph graph = new Graph();
        graph.addNode(1, new GraphNode(new Node(1,1)));
        graph.addNode(2, new GraphNode(new Node(2,2)));
        graph.addNode(3, new GraphNode(new Node(3,3)));
        graph.addDirectedEdge(1,2,50,true);
        graph.addDirectedEdge(1,3,50,true);
        assertEquals(3, graph.getV());
        assertEquals(2, graph.getE());
    }

    @Test void testDistFromTo(){
        double methodValue = HelperMethods.distFromTo(new Node(1,1), new Node(4,5));
        assertEquals(5,methodValue);
    }

    @Test void testPrintFunctions(){

    }

    @Test void testShortestPath(){

    }
}
