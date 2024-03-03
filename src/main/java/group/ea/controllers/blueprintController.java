package group.ea.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;


public class blueprintController implements Initializable {
    
    @FXML
    private Label explainingLabel;
    @FXML
    private ComboBox<String> searchspaceSelector;
    @FXML
    private ComboBox<String> problemSelector;
    @FXML
    private ComboBox<String> algorithmSelector;
    @FXML
    private ComboBox<String> stoppingcriteriaSelector;


    private final String[] searchspaces = {"Bit strings", "Permutations"};
    private final String[] problems = {"OneMax", "LeadingOnes", "BinVal", "Trap", "Jump_k"};
    private final String[] algorithms = {"(1+1) EA", "RLS", "Generic EA", "Simulated Annealing", "Ant System"};
    private final String[] criterias = {"Optimum reached", "Fitness bound", "Iteration bound"};




    public void initialize(URL arg0, ResourceBundle arg1){
        searchspaceSelector.getItems().addAll(searchspaces);
        problemSelector.getItems().addAll(problems);
        algorithmSelector.getItems().addAll(algorithms);
        stoppingcriteriaSelector.getItems().addAll(criterias);

        explainingLabel.setWrapText(true);
    }

    @FXML
    void selectionHandler(ActionEvent event){
        ComboBox<?> selector = (ComboBox<?>) event.getSource();
        if (selector.getValue() == "Bit strings"){
            explainingLabel.setText("The search space consisting of but strings i.e the set {0,1}^n, where n is the dimension");
        }
        else if (selector.getValue() == "Permutations"){
            explainingLabel.setText("Represents all permutations in the symmetric group S_{dimension}. That are all permutations of the elements {1,...,dimension}.");
        }
        else if (selector.getValue() == "OneMax"){
            explainingLabel.setText("The fitness value of an individual is the number of ones in its genotype.");
        }
        else if (selector.getValue() == "LeadingOnes"){
            explainingLabel.setText("The number of leading ones or the length of the largest coherent block of ones from the first position in the bit string corresponds to the fitness value.");
        }
        else if (selector.getValue() == "(1+1) EA"){
            explainingLabel.setText("The (1+1) EA is a simple evolutionary strategy that involves maintaining a single individual in the population, generating a mutated offspring, and replacing the current individual with the offspring only if it has higher fitness.");
        }
        else if (selector.getValue() == "Optimum reached"){
            explainingLabel.setText("Stops a run if the specified fitness bound is reached.");
        }
    }
    @FXML
    void closeProgram(ActionEvent event) {
        Platform.exit();
    }


}
