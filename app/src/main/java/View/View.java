package View;


import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.stage.Stage;

import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.util.ArrayList;

import DataStructures.Node;
import Model.Model;
import Utils.HelperMethods;

public class View {
    public Model model;
    public Stage stage;

    //The canvas and GraphicsContext used for drawing elements
    public ResizableCanvas canvas = new ResizableCanvas(1200, 700);
    public GraphicsContext gc = canvas.getGraphicsContext2D();

    //The linear transformation used to zoom and pan
    Affine trans;

    //The visible bounding box of the screen
    double x1visible;
    double y1visible;
    double x2visible;
    double y2visible;

    //The borders of the blue debugging box
    public double[] blueBorders = new double[4];

    //The pane is initialized with canvas and viewed in scene
    public BorderPane pane = new BorderPane(canvas);
    public Scene scene = new Scene(pane);

    //The top pane containing interactive components
    HBox topPane = new HBox();

    //The components of topPane
    public ComboBox<String> fromBox = new ComboBox<>();
    public ComboBox<String> toBox = new ComboBox<>();
    public Button fromSearch = new Button("Search");
    public Button toSearch = new Button("Search");
    public Button fromPinButton = new Button();
    public Button toPinButton = new Button();
    public ComboBox<String> transportOptions = new ComboBox<>();
    public Button findRouteButton = new Button("Go!");
    public Button toggleDebug = new Button("Toggle Debug mode");
    public ComboBox<String> themeChooser = new ComboBox<>();
    public Button toggleTheme = new Button("Toggle Theme");

    //Pane containing debugging options
    public VBox debugOptions = new VBox();

    //The components of debugOptions
    public Button setBlue = new Button("Set new blue rectangle");
    public CheckBox dijkstraUsesAStar = new CheckBox("Use A* heuristic for dijkstra");
    public CheckBox viewSearchedEdges = new CheckBox("Calculate searched edges");
    public CheckBox viewRouteDescription = new CheckBox("View route description");
    public CheckBox viewRedBoxesCheckBox = new CheckBox("View red bounding boxes");
    public CheckBox wayVisibility = new CheckBox("View ways");
    public CheckBox areaVisibility = new CheckBox("View areas");
    public CheckBox toggleDetailLevel = new CheckBox("Manual detail level");
    public Label currentDetailLevelLabel = new Label();
    public TextField setCurrentDetailLevel = new TextField(); 
    Label frameRateLabel = new Label();
    AnimationTimer frameRateMeter = new AnimationTimer() {
        private final long[] frameTimes = new long[100];
        private int frameTimeIndex = 0;
        private boolean arrayFilled = false;
        private long timeAtLastUpdate = 0;
        @Override
        public void handle(long now) {
            long oldFrameTime = frameTimes[frameTimeIndex];
            frameTimes[frameTimeIndex] = now;
            frameTimeIndex = (frameTimeIndex + 1) % frameTimes.length;
            if (frameTimeIndex == 0) {
                arrayFilled = true;
            }

            if (arrayFilled) {
                long elapsedNanos = now - oldFrameTime;
                long elapsedNanosPerFrame = elapsedNanos / frameTimes.length;
                double frameRate = 1_000_000_000.0 / elapsedNanosPerFrame;
                if(now-timeAtLastUpdate > 200000000) {
                    frameRateLabel.setText("Framerate: " + ((int) frameRate));
                    timeAtLastUpdate = now;
                }
            }
        }
    };
    public ComboBox<String> browseFiles = new ComboBox<>();
    public Button createObjButton = new Button("Create .obj of current file");

    //Used to determine how many layers of the RTree are drawn
    public int currentDetailLevel;

    //Various bools to know how to handle events
    public boolean debugging = false;
    public boolean choosingNewBlue = false;
    public boolean choosingNewStartNode = false;
    public Node chosenStartNode = null;
    public Node chosenEndNode = null;
    public boolean choosingNewEndNode = false;
    public boolean isDarkMode = false;
    
    public View(Model model, Stage primaryStage) {
        this.model = model;
        this.stage = primaryStage;

        this.stage.getIcons().add(new Image("icon.png"));

        primaryStage.setTitle("Map of Denmark");
        
        

        //Search boxes are styled
        fromBox.setEditable(true);
        fromBox.setPromptText("From");
        toBox.setEditable(true);
        toBox.setPromptText("To");
        ImageView fromPinImage = new ImageView(new Image("pinFrom.png"));
        ImageView toPinImage = new ImageView(new Image("pinTo.png"));
        fromPinImage.setFitWidth(13);
        fromPinImage.setFitHeight(17);
        toPinImage.setFitWidth(13);
        toPinImage.setFitHeight(17);
        fromPinButton.setGraphic(fromPinImage);
        toPinButton.setGraphic(toPinImage);

        //Updates which files can be chosen to load
        updateFileOptions();

        //Theme chooser options are initialized
        themeChooser.getItems().addAll(
            "Default", "High Saturation", "Dimmed", "Brightened",
            "Inverted", "Inverted and High Saturation", 
            "Inverted and Dimmed", "Inverted and Brightened"
        );
        themeChooser.getSelectionModel().selectFirst();

        //Transport options are set
        transportOptions.getItems().addAll("Car", "Bicycle", "Walk");
        transportOptions.getSelectionModel().selectFirst();
        
        //Checkbox default values are set
        dijkstraUsesAStar.setSelected(true);
        viewRouteDescription.setSelected(true);
        wayVisibility.setSelected(true);
        areaVisibility.setSelected(true);
        viewRedBoxesCheckBox.setSelected(true);

        //Default blue bounding box values are set
        blueBorders[0] = 100;
        blueBorders[1] = 100;
        blueBorders[2] = canvas.getWidth()-320;
        blueBorders[3] = canvas.getHeight()-100;

        currentDetailLevelLabel = new Label("Current detail level: " + currentDetailLevel);
        frameRateMeter.start();

        //Panes are spaced and padded appropriately
        topPane.setSpacing(10);
        topPane.setPadding(new Insets(20, 20, 20, 20));
        topPane.getChildren().addAll(
            fromPinButton, fromBox, fromSearch,
            toPinButton, toBox, toSearch,
            transportOptions, findRouteButton,
            themeChooser, toggleTheme, toggleDebug
        );

        debugOptions.setSpacing(10);
        debugOptions.setPadding(new Insets(20, 20, 20, 20));
        debugOptions.getChildren().addAll(
            setBlue, dijkstraUsesAStar, viewSearchedEdges, viewRouteDescription,
            viewRedBoxesCheckBox, wayVisibility, areaVisibility, 
            toggleDetailLevel, currentDetailLevelLabel, frameRateLabel, 
            browseFiles, createObjButton
        );

        trans = new Affine();

        //'Camera' pans and zooms according to min and max lat and lon values
        pan(-model.minlon, -model.maxlat);
        zoom(0, 0, canvas.getHeight() / (Math.abs(model.maxlat - model.minlat)));

        redraw();
        
        //primaryStage shows contents of scene
        pane.setTop(topPane);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //Updates and redraws all visuals
    public void redraw() {

        //Updates visual edges
        Point2D a, b;
        if(debugging) {
            a = mousetoModel(blueBorders[0], blueBorders[1]);
            b = mousetoModel(blueBorders[2], blueBorders[3]);
        } else {
            a = mousetoModel(0, 0);
            b = mousetoModel(canvas.getWidth(), canvas.getHeight());
        }
        x1visible = a.getX();
        x2visible = b.getX();
        y1visible = a.getY();
        y2visible = b.getY();

        //Updates current detail level
        if(!toggleDetailLevel.isSelected()) {
            currentDetailLevel = (int) (1/Math.cbrt(Math.abs((x2visible-x1visible)*(y2visible-y1visible))));
        }
        if(currentDetailLevel > 9) currentDetailLevel = 9;
        if(currentDetailLevel < 0) currentDetailLevel = 0;

        //Draws blue background
        gc.setTransform(new Affine());
        gc.setFill(Color.rgb(171, 211, 223));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        
        //Draws RTrees depending on current detail level
        gc.setTransform(trans);
        gc.setLineWidth(2/Math.sqrt(trans.determinant()));
        double[] viewBox = {y1visible, y2visible, x1visible, x2visible};
        
        //Note: areas are drawn before lines
        if(areaVisibility.isSelected()) {
            model.areaTrees.rangeSearch(viewBox, currentDetailLevel, themeChooser.getValue(), gc);
        }
        if(wayVisibility.isSelected()) {
            model.wayTrees.rangeSearch(viewBox, currentDetailLevel, themeChooser.getValue(), gc);
        }

        if(debugging) {
            gc.setLineWidth(0.3/Math.sqrt(trans.determinant()));
            //Draws blue box inside which elements are drawn
            gc.beginPath();
            gc.moveTo(x1visible, y1visible);
            gc.lineTo(x2visible, y1visible);
            gc.lineTo(x2visible, y2visible);
            gc.lineTo(x1visible, y2visible);
            gc.lineTo(x1visible, y1visible);
            gc.setStroke(Color.BLUE);
            gc.stroke();

            //Draws bounding boxes of RTrees
            if(viewRedBoxesCheckBox.isSelected()) {
                if(wayVisibility.isSelected()) {
                    model.wayTrees.boxSearch(viewBox, currentDetailLevel, gc);
                }
                if(areaVisibility.isSelected()) {
                    model.areaTrees.boxSearch(viewBox, currentDetailLevel, gc);
                }
            }
        }

        //If start point has been chosen
        if(chosenStartNode != null) {
            gc.setFill(Color.BLACK);
            double size = 12/Math.sqrt(trans.determinant());
            gc.fillOval(chosenStartNode.lon - 0.5*size, chosenStartNode.lat - 0.5*size, size, size);
        }

        //If end point has been chosen
        if(chosenEndNode != null) {
            gc.setFill(Color.RED);
            double size = 12/Math.sqrt(trans.determinant());
            gc.fillOval(chosenEndNode.lon - 0.5*size, chosenEndNode.lat - 0.5*size, size, size);
        }

        // Draws the shortest path if one has been found
        if(model.graph.fastestWay != null) {
            gc.setLineWidth(1/Math.sqrt(trans.determinant()));
            HelperMethods.drawWay(model.graph.searchedEdges, true, null, gc);
            gc.setLineWidth(2/Math.sqrt(trans.determinant()));
            HelperMethods.drawWay(model.graph.fastestWay, gc);

            if(viewRouteDescription.isSelected()) {
                double lat = model.graph.infoBoxLat;
                double lon = model.graph.fastestWay.nodes[model.graph.fastestWay.nodes.length/2].lon;
                double factor = 150/Math.sqrt(trans.determinant());
                gc.setLineWidth(0.3/Math.sqrt(trans.determinant()));
    
                gc.beginPath();
                gc.moveTo(lon-factor, lat-factor);
                gc.lineTo(lon+factor, lat-factor);
                gc.lineTo(lon+factor, lat-0.3*factor);
                gc.lineTo(lon-factor, lat-0.3*factor);
                gc.setFill(Color.WHITE);
                gc.fill();
    
                gc.setFill(Color.BLACK);
                gc.setFont(new Font(15/Math.sqrt(trans.determinant())));
                gc.fillText("Distance to target: " + model.graph.distanceStr + " kilometers", lon-0.9*factor, lat-0.7*factor);
                gc.fillText("Time by " + model.graph.vehicleStr + ": " + model.graph.timeStr + " minutes", lon-0.9*factor, lat-0.5*factor);
            }
        }

        //Location of scale bar
        Point2D scaleBorderStart = mousetoModel(canvas.getWidth()-100, canvas.getHeight()-25);
        Point2D scaleBorderFin = mousetoModel(canvas.getWidth()-200, canvas.getHeight()-25);

        //Draws scale bar and its length
        gc.setStroke(Color.BLACK);
        gc.setFill(Color.BLACK);
        gc.setFont(new Font(15/Math.sqrt(trans.determinant())));
        double[] distances = {1000, 500, 200, 100, 50, 20, 10, 5, 2, 1, 0.5, 0.2, 0.1, 0.05, 0.02, 0.01, 0.005, 0.002, 0.001};
        for (double currChoice : distances) {
            Point2D currChoiceLength = new Point2D(scaleBorderStart.getX(), scaleBorderStart.getX()+(0.00898311174*currChoice));
            if(currChoiceLength.getY()-currChoiceLength.getX() < scaleBorderStart.getX()-scaleBorderFin.getX()) {
                if(currChoice >= 1) {
                    gc.fillText("  " + (int) currChoice + " km", scaleBorderStart.getX(), scaleBorderStart.getY());

                } else {
                    gc.fillText("  " + (int) (currChoice*1000) + " m", scaleBorderStart.getX(), scaleBorderStart.getY());
                }
                gc.setLineWidth(5/Math.sqrt(trans.determinant()));
                gc.beginPath();
                gc.moveTo(currChoiceLength.getX(), scaleBorderStart.getY());
                gc.lineTo(currChoiceLength.getX()-(currChoiceLength.getY()-currChoiceLength.getX()), scaleBorderStart.getY());
                gc.stroke();
                break;
            }
        }
    }

    //Used to move the current view
    public void pan(double dx, double dy) {
        trans.prependTranslation(dx, dy);
    }

    //Used to zoom in the current view
    public void zoom(double dx, double dy, double factor) {
        trans.prependTranslation(-dx, -dy);
        trans.prependScale(factor, factor);
        pan(dx, dy);
        currentDetailLevelLabel.setText("Current detail level: " + currentDetailLevel);
    }

    //Used to get model coordinates from mouse position
    public Point2D mousetoModel(double lastX, double lastY) {
        try {
            return trans.inverseTransform(lastX, lastY);
        } catch (NonInvertibleTransformException e) {
            throw new RuntimeException(e);
        }
    }

    //Used to get mouse position from model coordinates
    public Point2D modelToMouse(double x, double y) {
        return trans.transform(x, y);
    }

    public void panToChosenNode(Node node, boolean isStartNode) {
        Point2D mouseCoords = modelToMouse(node.lon, node.lat);
        double x = mouseCoords.getX();
        double y = mouseCoords.getY();
        
        if(node != null) {
            pan(canvas.getWidth()/2-x, canvas.getHeight()/2-y);
            if(isStartNode) chosenStartNode = node;
            else chosenEndNode = node;
            choosingNewStartNode = false; 
            choosingNewEndNode = false;

            model.graph.fastestWay = null;

            redraw();
        }
    }

    public void updateFileOptions() {
        try {
            //Get all files in data folder
            File[] listOfFiles = new File("data/").listFiles();

            //Only add .osm and .obj files
            ArrayList<String> fileOptions = new ArrayList<>();
            for (File file : listOfFiles) {
                if(file.getName().endsWith(".osm") || file.getName().endsWith(".obj")) {
                    fileOptions.add(file.getName());
                }
            }
            browseFiles.getItems().setAll(fileOptions);
        } catch (NullPointerException e) {
            System.out.println("No data folder found!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data folder not found! Please create a folder called \"data\" in the same directory as the .jar file and put the .osm and .obj files in there.");
            alert.show();
        }
    }
}