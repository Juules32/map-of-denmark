package Controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import DataStructures.Node;
import DataStructures.GraphNode;
import Model.Model;
import View.View;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

public class Controller {
    public Model model;
    View view;

    double lastX;
    double lastY;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;

        //Updates and auto-completes address suggestions for start point
        view.fromSearch.setOnMouseClicked(e -> {
            String address = view.fromBox.getEditor().getText();
            view.fromBox.getItems().setAll(model.addresses.getRecommendations(address));
            view.choosingNewStartNode = false;
            view.choosingNewEndNode = false;
        });

        //Updates and auto-completes address suggestions for end point
        view.toSearch.setOnMouseClicked(e -> {
            String address = view.toBox.getEditor().getText();
            view.toBox.getItems().setAll(model.addresses.getRecommendations(address));
            view.choosingNewStartNode = false;
            view.choosingNewEndNode = false;
        });

        //Updates chosen start point when search bar is updated
        view.fromBox.valueProperty().addListener((options, oldValue, newValue) -> {
            if(newValue != null && newValue != "" && oldValue != newValue) {
                String[] newAddress = model.addresses.stringToList(view.fromBox.getSelectionModel().getSelectedItem());
                if(newAddress[0] != null && newAddress[1] != null && newAddress[2] != null) {
                    Node node = model.addresses.get(newAddress);            
                    view.panToChosenNode(node, true);
                }
            }
        });
        
        //Updates chosen end point when search bar is updated
        view.toBox.valueProperty().addListener((options, oldValue, newValue) -> {
            if(newValue != null && newValue != "" && oldValue != newValue) {
                String[] newAddress = model.addresses.stringToList(view.toBox.getSelectionModel().getSelectedItem());
                if(newAddress[0] != null && newAddress[1] != null && newAddress[2] != null) {
                    Node node = model.addresses.get(newAddress);            
                    view.panToChosenNode(node, false);
                }
            }
        });

        view.fromPinButton.setOnMouseClicked(e -> {
            view.choosingNewStartNode = true;
        });

        view.toPinButton.setOnMouseClicked(e -> {
            view.choosingNewEndNode = true;
        });

        //Finds shortest path between start and end node
        view.findRouteButton.setOnMouseClicked(e -> {
            
            if(view.chosenStartNode != null && view.chosenEndNode != null) {
                GraphNode nearestNeighborFrom = (GraphNode) model.wayTrees.nearestNeighbor(view.chosenStartNode);
                GraphNode nearestNeighborTo = (GraphNode) model.wayTrees.nearestNeighbor(view.chosenEndNode);
                //Used to calculate running time
                long startTime = System.currentTimeMillis();
                boolean success = model.graph.shortestPath(nearestNeighborFrom, nearestNeighborTo, view.dijkstraUsesAStar.isSelected(), view.transportOptions.getValue(), view.viewSearchedEdges.isSelected());
                if(!success) {
                    String message = "Failed to find shortest path " + (!view.dijkstraUsesAStar.isSelected() ? "without " : "") +
                            "using A* after " + ((double) System.currentTimeMillis()-startTime)/1000 + " seconds\n";
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
                    alert.show();
                    System.out.println(message);
                } else{
                    //Print total distance to end node
                    System.out.println("Shortest path in km: " + model.graph.distanceStr);

                    //Print the total running time information
                    System.out.println("Found shortest path " + (!view.dijkstraUsesAStar.isSelected() ? "without " : "") +
                            "using A* after " + ((double) System.currentTimeMillis()-startTime)/1000 + " seconds\n");
                }
            }
            
            view.redraw();
        });

        //When a new file is selected
        view.browseFiles.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {

            // Try initializing new MVC with this file
            try {
                String filename = newValue;
                FileInputStream filenameStream = new FileInputStream(new File("data/" + filename));
                System.out.println("Selected Map: " + filename);
        
                this.model = Model.load(filename, filenameStream);
                this.view = new View(this.model, view.stage);
                new Controller(this.model, this.view);

            } catch (IOException | XMLStreamException | FactoryConfigurationError | ClassNotFoundException e) {
                System.out.println("Error loading file!");
            }
        });

        view.canvas.setOnMousePressed(e -> {

            //Updates last mouse position when mouse is pressed down
            lastX = e.getX();
            lastY = e.getY();
            
            //Sets start node of path if choosingNewStartNode is enabled
            if(view.choosingNewStartNode) {
                Point2D clickedPoint = view.mousetoModel(e.getX(), e.getY());
                Node node = model.wayTrees.nearestNeighbor(clickedPoint.getY(), clickedPoint.getX());
                view.panToChosenNode(node, true);
            }

            //Sets end node of path if choosingNewEndNode is enabled
            if(view.choosingNewEndNode) {
                Point2D clickedPoint = view.mousetoModel(e.getX(), e.getY());
                Node node = model.wayTrees.nearestNeighbor(clickedPoint.getY(), clickedPoint.getX());
                view.panToChosenNode(node, false);
            }

        });

        //Pans by the difference from last to current xy (dx and dy) on mouse drag
        view.canvas.setOnMouseDragged(e -> {

            //Sets new edges of blue debugging box
            if(view.choosingNewBlue) {
                //Also adjusts so start values are always smallest
                if(lastX < e.getX()) {
                    view.blueBorders[0] = lastX;
                    view.blueBorders[2] = e.getX();

                } else {
                    view.blueBorders[0] = e.getX();
                    view.blueBorders[2] = lastX;
                }
                if(lastY < e.getY()) {
                    view.blueBorders[1] = lastY;
                    view.blueBorders[3] = e.getY();
                } else {
                    view.blueBorders[1] = e.getY();
                    view.blueBorders[3] = lastY;
                }
            }

            //Pans by the difference from last to current xy
            else {
                double dx = e.getX() - lastX;
                double dy = e.getY() - lastY;
                view.pan(dx, dy);
    
                lastX = e.getX();
                lastY = e.getY();
            }

            view.redraw();
        });

        //Disables choosingNewBlue after choosing new blue box
        view.canvas.setOnMouseReleased(e -> {
            if(view.choosingNewBlue) view.choosingNewBlue = false;
        });

        //Zooms in and out on scroll
        view.canvas.setOnScroll(e -> {
            double factor = e.getDeltaY();
            view.zoom(e.getX(), e.getY(), Math.pow(1.01, factor));
            view.redraw();
        });

        //Redraws upon toggling checkBoxes
        view.wayVisibility.setOnMouseClicked(e -> view.redraw());
        view.areaVisibility.setOnMouseClicked(e -> view.redraw());
        view.viewRedBoxesCheckBox.setOnMouseClicked(e -> view.redraw());
        view.viewRouteDescription.setOnMouseClicked(e -> view.redraw());

        //Redraws upon selecting new theme
        view.themeChooser.getSelectionModel().selectedItemProperty()
        .addListener((options, oldValue, newValue) -> view.redraw());

        //Toggles whether to show debug options
        view.toggleDebug.setOnMouseClicked(e -> {
            view.debugging = !view.debugging;
            if(view.pane.getRight() == null) {
                view.pane.setRight(view.debugOptions);
            } else view.pane.setRight(null);
            view.redraw();
        });

        //Enables choosing new blue box
        view.setBlue.setOnMouseClicked(e -> view.choosingNewBlue = true);

        //Switches between manual and automatic detail level settings
        view.toggleDetailLevel.setOnMouseClicked(e -> {
            if(view.toggleDetailLevel.isSelected()) {
                view.debugOptions.getChildren().set(view.debugOptions.getChildren()
                .indexOf(view.currentDetailLevelLabel), view.setCurrentDetailLevel);
            }
            else {
                view.debugOptions.getChildren().set(view.debugOptions.getChildren()
                .indexOf(view.setCurrentDetailLevel), view.currentDetailLevelLabel);
            }
        });

        //If enter is pressed, detail level updates
        view.setCurrentDetailLevel.setOnKeyPressed(e -> {
            if( e.getCode() == KeyCode.ENTER ) {
                view.currentDetailLevel = Integer.parseInt(view.setCurrentDetailLevel.getText());
                view.redraw();
            }
        });

        //Saves to new .obj file on click
        view.createObjButton.setOnMouseClicked(e -> {
            try {
                model.save(model.filename+".obj");
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            //Updates list of files to load
            view.updateFileOptions();

        });

        //Redraws when window width changes
        view.stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            view.canvas.setWidth(newVal.intValue());
            if(Math.abs(newVal.intValue() - oldVal.intValue()) == 1) {
                view.redraw();
            }
        });

        //Redraws when window height changes
        view.stage.heightProperty().addListener((obs, oldVal, newVal) -> {
            view.canvas.setHeight(newVal.intValue());
            if(Math.abs(newVal.intValue() - oldVal.intValue()) == 1) {
                view.redraw();
            }
        });

        //Toggles dark theme visuals
        view.toggleTheme.setOnAction(e -> {
            view.isDarkMode = !view.isDarkMode;
            if (view.isDarkMode) {
                view.scene.getStylesheets().add("dark-theme.css");
            } else {
                view.scene.getStylesheets().remove("dark-theme.css");
            }
        });
    }
}