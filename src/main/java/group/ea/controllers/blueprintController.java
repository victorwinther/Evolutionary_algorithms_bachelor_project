package group.ea.controllers;

import group.ea.main;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
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

    @FXML
    Button saveButton;




    private final String[] searchspaces = {"Bit strings", "Permutations"};
    private final String[] problems = {"OneMax", "LeadingOnes", "BinVal", "Trap", "Jump_k"};
    private final String[] algorithms = {"(1+1) EA", "RLS", "Generic EA", "Simulated Annealing", "Ant System"};
    private final String[] criterias = {"Optimum reached", "Fitness bound", "Iteration bound"};
    private Stage stage;
    public String[] blueprintChoices = new String[5];




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
    void returnHome(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        Scene scene = new Scene(root);
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();

        Platform.runLater(root::requestFocus);
        stage.setScene(scene);
        stage.show();}

    @FXML
    void startMainPage(ActionEvent event) throws IOException {
        // Load the home page FXML file
        //make an array where you fill it with the chosen combobox values
        blueprintChoices[0] = searchspaceSelector.getValue();
        blueprintChoices[1] = problemSelector.getValue();
        blueprintChoices[2] = algorithmSelector.getValue();
        blueprintChoices[3] = stoppingcriteriaSelector.getValue();
        blueprintChoices[4] = "Random";



        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        Parent root = loader.load();

        // Here you would get the controller if you need to call methods on it
        mainController controller = loader.getController();
        controller.recieveArray(blueprintChoices); // Call methods on the controller if needed

        // Set the scene to the home page
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // Optional: If you need the home page to gain focus or perform additional setup
        Platform.runLater(() -> {
            root.requestFocus();
            // Any additional setup can go here
        });

        stage.setScene(scene);
        stage.show();
    }

    public String getSearchspaceSelector() {
        return searchspaceSelector.getValue();
    }
    public String getProblemSelector() {
        return problemSelector.getValue();
    }
    public String getAlgorithmSelector() {
        return algorithmSelector.getValue();
    }
    /*
    public String getSelectioncriteriaSelector() {
        return stoppingcriteriaSelector.getValue();
    }
*/
}
