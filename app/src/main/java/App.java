
import java.io.InputStream;

import Controller.Controller;
import Model.Model;
import View.View;
import javafx.application.Application;
import javafx.stage.Stage;

public class App extends Application {
    Model model;
    View view;
    Controller controller;
    
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        //Filename of file to be loaded
        String filename = "bornholm.osm";

        //Get the InputStream from the resources folder
        InputStream filenameStream = getClass().getClassLoader().getResourceAsStream(filename);

        //Model part of MVC
        model = Model.load(filename, filenameStream);

        //View part of MVC
        view = new View(model, primaryStage);

        //Controller part of MVC
        controller = new Controller(model, view);
    }

    @Override
    public void stop() {
        System.out.println("Closed application!");
    }
}
