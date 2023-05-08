import DataStructures.Graph;
import DataStructures.GraphNode;
import DataStructures.Node;
import DataStructures.Trie;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    @Test void addEdgeTest(){
        Trie<GraphNode> nodes = new Trie<>();
        nodes.set(1, new GraphNode(new Node(1,1)));
        nodes.set(2, new GraphNode(new Node(2,2)));
        nodes.set(3, new GraphNode(new Node(3,3)));
        Graph graph = new Graph(nodes);
        graph.addNode(3,new GraphNode(new Node(3,3)));
        graph.addDirectedEdge(1,2,50,true);
        graph.addDirectedEdge(1,3,50,true);
        assertEquals(2, graph.getE());
    }

    @Test void printFunctionsTest(){
        Graph graph = new Graph();
        assertFalse(graph.printEdges(1));

        graph.addNode(1, new GraphNode(new Node(0,0)));
        assertFalse(graph.printEdges(1));

        graph.addNode(2, new GraphNode(new Node(1,0)));
        graph.addDirectedEdge(1, 2, 0, false);
        assertTrue(graph.printEdges(1));

        graph.addDirectedEdge(1, 3, 0, false);
        assertFalse(graph.printEdges(1));
    }

    @Test void shortestPathTest(){
        // Builds basic graph from some nodes 1-10 with lon/lat 1-10
        Graph graph = new Graph();
        for (int i=1; i<=10; i++) graph.addNode(i, new GraphNode(new Node(i,i)));
        for (int i=1; i<=9; i++) graph.addUndirectedEdge(i, i+1,50,true);

        // adds faster diversity for car
        graph.addUndirectedEdge(3, 7, 130, true);

        // So comparators return 0 in a case
        graph.addNode(12, new GraphNode(new Node(3,3)));
        graph.addDirectedEdge(2, 12, 50, true);

        assertEquals(11, graph.getV());
        assertEquals(21, graph.getE());

        GraphNode closeNode = new GraphNode(new Node(0.9f, 0.9f));
        graph.addNode(0, closeNode);
        graph.addUndirectedEdge(0, 1, 50, true);

        // Tests results from shortestPath
        graph.shortestPath(2, 10, "Car");
        assertNotNull(graph.fastestWay);
        assertEquals("1257.394", graph.distanceStr);
        assertEquals("1044", graph.timeStr);
        assertEquals("car", graph.vehicleStr);
        assertEquals(6,graph.fastestWay.nodes.length);

        // aStar false
        graph.shortestPath(2, 10, false, "Bicycle");
        assertNotNull(graph.fastestWay);
        assertEquals("1257.394", graph.distanceStr);
        assertEquals("3772", graph.timeStr);
        assertEquals("bicycle", graph.vehicleStr);
        assertEquals(9,graph.fastestWay.nodes.length);

        // Checks null-handlers
        graph.shortestPath(null, null, false, "Walk", false);
        assertEquals(null, graph.fastestWay);
        graph.addNode(11, new GraphNode(new Node(11,11)));
        graph.shortestPath(11, 1, true, "Walk");
        assertEquals(null, graph.fastestWay);
        graph.shortestPath(1, 11, true, "Walk");
        assertEquals(null, graph.fastestWay);
        graph.shortestPath(1, 100, false ,"Walk");
        assertEquals(null, graph.fastestWay);

        // Nodes for unfindable paths
        GraphNode gn1 = new GraphNode(new Node(13,13));
        GraphNode gn2 = new GraphNode(new Node(14,14));
        graph.addNode(13, gn1);
        graph.addNode(14, gn2);
        graph.addUndirectedEdge(13, 14, 80, true);

        // Unfindable paths with and without aStar (id 11 not connected to graph)
        graph.shortestPath(1, 14, false, "Walk");
        assertEquals(null, graph.fastestWay);
        graph.shortestPath(closeNode, gn2, true, "Walk", true);
        assertEquals(null, graph.fastestWay);
    }
}