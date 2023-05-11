import org.junit.jupiter.api.Test;

import DataStructures.Node;
import DataStructures.RTree;
import DataStructures.RTrees;
import Model.Model;
import View.ResizableCanvas;
import javafx.scene.canvas.GraphicsContext;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.BeforeAll;

public class RTreesTest {
    static Model testModel;

    static RTrees testRTreeWays;
    static RTrees testRTreeAreas;

    ResizableCanvas canvas = new ResizableCanvas(1200, 700);
    GraphicsContext gc = canvas.getGraphicsContext2D();


    @BeforeAll static void setup() {
        try {
            testModel = Model.load("data/aakirkeby.osm", new FileInputStream("data/aakirkeby.osm"));
        } catch (ClassNotFoundException | IOException | XMLStreamException | FactoryConfigurationError e) {
            e.printStackTrace();
        }

        testRTreeWays = testModel.wayTrees;
        testRTreeAreas = testRTreeWays = testModel.areaTrees;
    }

    @Test void getRTreeTest() {
        assert(testRTreeWays.get(5) instanceof RTree);
    }

    @Test void rangeSearchTest() {
        
        double[] viewBox = {-100, 100, -100, 100};
        testRTreeWays.rangeSearch(viewBox, 5, "Default", gc);

        double[] viewBox2 = {10000, 10000, 10000, 10000};
        testRTreeWays.rangeSearch(viewBox2, 5, "Default", gc);

    }

    @Test void boxSearchTest() {
        double[] viewBox = {-100, 100, -100, 100};
        testRTreeWays.boxSearch(viewBox, 5, gc);

        double[] viewBox2 = {10000, 10000, 10000, 10000};
        testRTreeWays.boxSearch(viewBox2, 5, gc);

    }

    @Test void nearestNeighborAlternateInputTest() {
        assertEquals(-55.05501937866211, testRTreeWays.nearestNeighbor(new Node(-55.055F, 8.35F)).lat);
    }
    
    @Test void nearestNeighborVarietyTest() {
        assertEquals(-55.05501937866211, testRTreeWays.nearestNeighbor(-55.055, 8.35).lat);
        assertEquals(-55.11634826660156, testRTreeWays.nearestNeighbor(-53, 0).lat);
        assertEquals(-55.11634826660156, testRTreeWays.nearestNeighbor(-55.05, 0).lat);
        assertEquals(-55.11668014526367, testRTreeWays.nearestNeighbor(-57, 0).lat);
        assertEquals(-55.06290054321289, testRTreeWays.nearestNeighbor(-53, 10).lat);
        assertEquals(-55.10866165161133, testRTreeWays.nearestNeighbor(-55.05, 10).lat);
        assertEquals(-55.11106491088867, testRTreeWays.nearestNeighbor(-57, 10).lat);
    }
}
