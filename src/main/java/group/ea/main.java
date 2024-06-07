package group.ea;

import group.ea.controllers.Schedule;
import group.ea.controllers.mainController;
import group.ea.helperClasses.Timer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class main extends Application {
    double x, y = 0;
    static mainController controller;

    @Override
    public void start(Stage stage) throws IOException {


        //FXMLLoader loader  = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        controller = loader.getController();


        stage.setScene(scene);
        stage.show();
        stage.requestFocus(); // Request focus for the stage
        root.requestFocus();  // Request focus for the root node

    }

    public static void main(String[] args) {

        launch(args);

    }

}


