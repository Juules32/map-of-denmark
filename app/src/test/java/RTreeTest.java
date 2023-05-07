import org.junit.jupiter.api.Test;

import DataStructures.RTree;
import Model.Model;
import View.ResizableCanvas;
import javafx.scene.canvas.GraphicsContext;

import static org.junit.jupiter.api.Assertions.*;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.BeforeAll;

public class RTreeTest {
    static Model testModel;

    static RTree testRTreeWays;
    static RTree testRTreeAreas;

    ResizableCanvas canvas = new ResizableCanvas(1200, 700);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    /*
    @BeforeAll static void setup() {
        try {
            testModel = Model.load("data/bornholm.osm", new FileInputStream("data/bornholm.osm"));
        } catch (ClassNotFoundException | IOException | XMLStreamException | FactoryConfigurationError e) {
            e.printStackTrace();
        }

        testRTreeWays = new RTree(testModel.ways.get(5), true);
        testRTreeAreas = new RTree(testModel.areas.get(5), false);
    }

    @Test void rangeSearchTest() {
        
        double[] viewBox = {-100, 100, -100, 100};
        testRTreeWays.rangeSearch(testRTreeWays.root, viewBox, "Default", gc);

        double[] viewBox2 = {10000, 10000, 10000, 10000};
        testRTreeWays.rangeSearch(testRTreeWays.root, viewBox2, "Default", gc);

    }

    @Test void boxSearchTest() {
        double[] viewBox = {-100, 100, -100, 100};
        testRTreeWays.boxSearch(testRTreeWays.root, viewBox, gc);

        double[] viewBox2 = {10000, 10000, 10000, 10000};
        testRTreeWays.boxSearch(testRTreeWays.root, viewBox2, gc);

    }
    */
}
