package group.ea.controllers;

import group.ea.main;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;


public class blueprintController implements Initializable {

    @FXML
    private Label explainingLabel;
    @FXML
    private Label iterationLabel;
    @FXML
    private TextField iterationTxtField, bitStringSize;
    @FXML
    private TableView<String> batchTable;
    @FXML
    private ComboBox<String> searchspaceSelector;
    @FXML
    private ComboBox<String> problemSelector;
    @FXML
    private ComboBox<String> algorithmSelector;
    @FXML
    private ComboBox<String> stoppingcriteriaSelector;
    @FXML
    private ComboBox<String> displaySelector;
    @FXML
    private ChoiceBox<Integer> stringLength;
    @FXML
    private CheckBox optimumCheck, fitnessCheck, iterationCheck;

    @FXML
    private ChoiceBox<String> choiceBox;

    @FXML
    Button saveButton;
    FileChooser fileChooser = new FileChooser();

    private Map<String, List<String>> categoryOptions = new HashMap<>();
    private HashMap<String, String> descriptions = new HashMap<>();
    private HashMap<String, List<String>> batchParameters = new HashMap<>();
    private List<ComboBox<String>> allComboBoxes;
    private final String[] categories = {"searchSpace", "problem", "algorithm", "stopping", "display"};
    private ArrayList<String> dependencies = new ArrayList<>();
    private ArrayList<String> batchColumns = new ArrayList<>();
    private ArrayList<ArrayList<String>> batchData = new ArrayList<>();


    private final String[] searchspaces = {"Bit strings", "Permutations"};
    private final String[] problems = {"OneMax", "LeadingOnes", "BinVal", "Trap", "Jump_k"};
    private final String[] algorithms = {"(1+1) EA", "RLS", "Generic EA", "Simulated Annealing", "Ant System"};
    private final String[] criterias = {"Optimum reached", "Fitness bound", "Iteration bound"};

    private final String[] stopping = {"Optimum reached", "Fitness bound", "Iteration bound"};
    private Stage stage;
    public String[] blueprintChoices = new String[6];




    public void initialize(URL arg0, ResourceBundle arg1){
        //initialize components
        allComboBoxes = Arrays.asList(searchspaceSelector, problemSelector, algorithmSelector, stoppingcriteriaSelector, displaySelector);
        addCategoryOptions();
        addDescriptions();
        initializeBatchParameters();
        searchspaceSelector.getItems().addAll(searchspaces);
        problemSelector.getItems().addAll(problems);
        algorithmSelector.getItems().addAll(algorithms);
        stoppingcriteriaSelector.getItems().addAll(criterias);
        stringLength.getItems().addAll(10, 100, 200, 300, 400, 500);
        stringLength.setValue(100);
        //choiceBox.getItems().addAll(stopping);
        searchspaceSelector.setValue("Permutation");
        problemSelector.setValue("TSP");
        algorithmSelector.setValue("TEMP");
        stoppingcriteriaSelector.setValue("Optimum reached");


        //initialize filechooser object
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        //Fill comboboxes
        for (ComboBox<String> comboBox : allComboBoxes) {
            String category = getComboBoxCategory(comboBox);
            comboBox.setItems(FXCollections.observableArrayList(categoryOptions.get(category)));
        }

        explainingLabel.setWrapText(true);

    }

    private void updateCategories() {
        if (dependencies.size() == 0){
            for (ComboBox<String> comboBox : allComboBoxes){
                String category = getComboBoxCategory(comboBox);

                for (String item : categoryOptions.get(category)){
                    if (!dependencies.contains(item) && !comboBox.getItems().contains(item)) {
                        comboBox.getItems().add(item);
                    }
                }
            }

        }
        else {
            for (ComboBox<String> comboBox : allComboBoxes) {
                comboBox.getItems().removeAll(dependencies);
            }
        }
    }

    private void addCategoryOptions() {
        categoryOptions.put("searchSpace", Arrays.asList("Bit strings", "Permutations"));
        categoryOptions.put("problem", Arrays.asList("OneMax", "LeadingOnes", "TSP"));
        categoryOptions.put("algorithm", Arrays.asList("(1+1) EA", "RLS", "Generic EA", "Simulated Annealing", "Ant System", "TEMP"));
        categoryOptions.put("stopping", Arrays.asList("Optimum reached", "Fitness bound", "Iteration bound"));
        categoryOptions.put("display", Arrays.asList("Table", "Graph"));
    }

    private void initializeBatchParameters() {
        batchParameters.put("Ant System", List.of("ants", "colony"));
        batchParameters.put("RLS", List.of("size"));
        batchParameters.put("Graph", List.of("test"));
        batchParameters.put("Fitness bound", List.of("Iteration"));
        batchParameters.put("Iteration bound", List.of("Iteration"));
    }

    private void addDescriptions(){
        descriptions.put("Bit strings", "The search space consisting of bit strings i.e. the set {0,1}^n, where n is the dimension.");
        descriptions.put("Permutations", "Represents all permutations in the symmetric group S_{dimension}. That are all permutations of the elements {1,...,dimension}.");

        descriptions.put("OneMax", "The fitness value of an individual is the number of ones in its genotype.");
        descriptions.put("LeadingOnes", "The number of leading ones or the length of the largest coherent block of ones from the first position in the bit string corresponds to the fitness value.");
        descriptions.put("TSP", "Traveling sales person");

        descriptions.put("(1+1) EA", "The (1+1) EA is a simple evolutionary strategy that involves maintaining a single individual in the population, generating a mutated offspring, and replacing the current individual with the offspring only if it has higher fitness.");
        descriptions.put("RLS", "description TODO");
        descriptions.put("Generic EA", "description TODO");
        descriptions.put("Simulated Annealing", "description TODO");
        descriptions.put("Ant System", "description TODO");

        descriptions.put("Optimum reached", "Stops a run if the specified fitness bound is reached.");
        descriptions.put("Fitness bound", "Stops running if a certain fitness is reached.");
        descriptions.put("Iteration bound", "Stops running if a certain amount of iterations is reached");

        descriptions.put("Table", "Display the fitness in a table.");
        descriptions.put("Graph", "Display the fitness in a graph.");
    }

    private String getComboBoxCategory(ComboBox<?> comboBox) {
        int index = allComboBoxes.indexOf(comboBox);
        return categories[index];
    }

    private void updateBatchTable() {
        // Construct parameters for batch table
        List<String> currentSelection = getSelection();
        batchColumns.clear(); // Clear previous batch columns
        batchColumns.addAll(List.of("No. runs", "Dimension"));

        // Update batch columns based on the current selection
        for (String selection : currentSelection) {
            if (batchParameters.containsKey(selection)) {
                batchColumns.addAll(batchParameters.get(selection));
            }
        }

        // Remove columns not present in batchColumns
        batchTable.getColumns().removeIf(column -> !batchColumns.contains(column.getText()));

        // Add new columns from batchColumns
        for (String columnName : batchColumns) {
            if (!columnExists(columnName)) {
                TableColumn<String, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()));
                column.setCellFactory(TextFieldTableCell.forTableColumn()); // Set cell factory for editing
                column.setOnEditCommit(event -> {
                    int rowIndex = event.getTablePosition().getRow();
                    int colIndex = batchColumns.indexOf(columnName);

                    while (batchData.size() <= rowIndex) {
                        batchData.add(new ArrayList<>(batchColumns.size()));
                    }

                    while (batchData.get(rowIndex).size() < batchColumns.size()) {
                        batchData.get(rowIndex).add("");
                    }

                    // Update the data in the ArrayList
                    batchData.get(rowIndex).set(colIndex, event.getNewValue());
                });

                batchTable.getColumns().add(column);
            }
        }

    }

    // Helper method to check if a column already exists in batchTable
    private boolean columnExists(String columnName) {
        for (TableColumn<String, ?> existingColumn : batchTable.getColumns()) {
            if (existingColumn.getText().equals(columnName)) {
                return true;
            }
        }
        return false;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void saveDataToFile(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Write ComboBox selections to the file in CSV format
            if (!iterationTxtField.isDisable()){
                writer.write("Searchspace,Problem,Algorithm,StoppingCriteria,StoppingCriteriaIterations,Display\n");
                writer.write(searchspaceSelector.getValue() + ",");
                writer.write(problemSelector.getValue() + ",");
                writer.write(algorithmSelector.getValue() + ",");
                writer.write(stoppingcriteriaSelector.getValue() + ",");
                writer.write(iterationTxtField.getText() + ",");
                writer.write(displaySelector.getValue() + "\n");
            }
            else {
                writer.write("Searchspace,Problem,Algorithm,StoppingCriteria,Display\n");
                writer.write(searchspaceSelector.getValue() + ",");
                writer.write(problemSelector.getValue() + ",");
                writer.write(algorithmSelector.getValue() + ",");
                writer.write(stoppingcriteriaSelector.getValue() + ",");
                writer.write(displaySelector.getValue() + "\n");
            }

            // Write batch table data to the file
            writer.write("batch\n");
            for (String columnName : batchColumns) {
                writer.write(columnName + ",");
            }
            writer.write("\n");

            for (ArrayList<String> rowData : batchData) {
                for (String cellValue : rowData) {
                    if (cellValue.equals("")){
                        writer.write("0,");
                    }
                    else {
                        writer.write(cellValue + ",");
                    }

                }
                writer.write("\n");
            }

            System.out.println("Data saved to file: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Error saving data to file: " + e.getMessage());
        }
    }

    private List<String> getSelection() {
        return Arrays.asList(
                getValueOrDefault(searchspaceSelector),
                getValueOrDefault(problemSelector),
                getValueOrDefault(algorithmSelector),
                getValueOrDefault(stoppingcriteriaSelector),
                getValueOrDefault(displaySelector)
        );
    }

    private String getValueOrDefault(ComboBox<?> comboBox) {
        return comboBox.getValue() != null ? comboBox.getValue().toString() : "";
    }

    @FXML
    void selectionHandler(ActionEvent event){
        ComboBox<?> selector = (ComboBox<?>) event.getSource();
        String selectorValue = selector.getSelectionModel().getSelectedItem().toString();

        //show description
        if (descriptions.containsKey(selectorValue)) {
            explainingLabel.setText(descriptions.get(selectorValue));
        }

        //check if iteration is needed
        if (selector == stoppingcriteriaSelector) {
            if (selector.getValue().equals("Fitness bound") || selector.getValue().equals("Iteration bound")) {
                iterationLabel.setDisable(false);
                iterationTxtField.setDisable(false);
            } else {
                iterationLabel.setDisable(true);
                iterationTxtField.setDisable(true);
            }
        }

        //category dependencies logic
        if (selector == searchspaceSelector) {
            if (selector.getValue().equals("Bit strings")) {
                dependencies.add("TSP");
            } else if (selector.getValue().equals("Permutations")) {
                dependencies.remove("TSP");
            }
        }

        //update depending on combobox selection
        updateCategories();
        updateBatchTable();
    }

    @FXML
    void createNewBatch(ActionEvent event) {
        /*
        ObservableList<String> newRow = FXCollections.observableArrayList();
        batchTable.getColumns().forEach(column -> {
            newRow.add("0"); // Add "0" to each cell
        });
        */

        batchTable.getItems().add("0");
    }

    @FXML
    void saveHandler(ActionEvent event) {
        if (!iterationTxtField.isDisable() && iterationTxtField.getText().equals("")){
            showAlert("Iteration must be filled when \"" + stoppingcriteriaSelector.getValue() + "\" is chosen");
        }
        else{
            File file = fileChooser.showSaveDialog(stage);
            if (file != null) {
                saveDataToFile(file);
            }
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
        blueprintChoices[4] = iterationTxtField.isDisable() ? "" : iterationTxtField.getText();
        blueprintChoices[5] = String.valueOf(stringLength.getValue());



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
