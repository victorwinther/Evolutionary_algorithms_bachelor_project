package group.ea.controllers;

import group.ea.main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;


import java.io.IOException;
import java.util.Objects;

public class mainController {
    @FXML
    private Button btnPlot, btnConnect, btnTable;

    @FXML
    private BorderPane mainBorderPane;




    @FXML
    void menuChangeHandler(ActionEvent event) throws IOException {
        if (event.getSource() == btnPlot){
            changeContent("plotPage");
        }
        else if (event.getSource() == btnConnect){
            changeContent("connectPage");
        }
        else if (event.getSource() == btnTable){
            changeContent("tablePage");
        }
    }

    private void changeContent(String page) throws IOException {
        Parent root = null;

        root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/" + page + ".fxml")));
        mainBorderPane.setCenter(root);
    }
}