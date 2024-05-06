package group.ea.controllers;

import group.ea.main;
import group.ea.structure.helperClasses.BatchRow;
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
    private Label iterationLabel, dimensionLabel;
    @FXML
    private TextField iterationTxtField, dimensionTxtField, fitnessTxtField;
    @FXML
    private TableView<BatchRow> batchTable;
    @FXML
    private ComboBox<String> searchspaceSelector;
    @FXML
    private ComboBox<String> problemSelector;
    @FXML
    private ComboBox<String> algorithmSelector;
    @FXML
    private CheckBox optimumReached, fitnessBound, iterationBound;
    @FXML
    Button saveButton;
    FileChooser fileChooser = new FileChooser();

    private Map<String, List<String>> categoryOptions = new HashMap<>();
    private HashMap<String, String> descriptions = new HashMap<>();
    private HashMap<String, List<String>> batchParameters = new HashMap<>();
    private List<ComboBox<String>> allComboBoxes;
    private final String[] categories = {"searchSpace", "problem", "algorithm"};
    private ArrayList<String> dependencies = new ArrayList<>();
    private ArrayList<String> batchColumns = new ArrayList<>();
    private ArrayList<ArrayList<String>> batchData = new ArrayList<>();


    private final String[] searchspaces = {"Bit strings", "Permutations"};
    private final String[] problems = {"OneMax", "LeadingOnes", "BinVal", "Trap", "Jump_k"};
    private final String[] algorithms = {"(1+1) EA", "RLS", "Generic EA", "Simulated Annealing", "Ant System"};


    private Stage stage;
    public HashMap<String,String> blueprintChoices = new HashMap<>();
    //make a hashmap of string




    public void initialize(URL arg0, ResourceBundle arg1){
        //initialize components
        Schedule.getSchedules().clear();
        allComboBoxes = Arrays.asList(searchspaceSelector, problemSelector, algorithmSelector);
        addCategoryOptions();
        addDescriptions();
        initializeBatchParameters();
        searchspaceSelector.getItems().addAll(searchspaces);
        problemSelector.getItems().addAll(problems);
        algorithmSelector.getItems().addAll(algorithms);

        searchspaceSelector.setValue("Bitstring");
        problemSelector.setValue("OneMax");
        algorithmSelector.setValue("(1+1) EA");
        optimumReached.setSelected(true);
        dimensionTxtField.setText("100");


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
    }

    private void initializeBatchParameters() {
        batchParameters.put("Ant System", List.of("ants", "colony"));
        batchParameters.put("RLS", List.of("size"));
        batchParameters.put("Graph", List.of("test"));
        batchParameters.put("Fitness bound", List.of("F. Iterations"));
        batchParameters.put("Iteration bound", List.of("I. Iterations"));
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
        List<String> currentSelection = getParameterSelection();
        batchColumns.clear(); // Clear previous batch columns
        batchColumns.addAll(List.of("id", "No. runs", "Dimension"));

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
                TableColumn<BatchRow, String> column = new TableColumn<>(columnName);
                column.setCellValueFactory(data -> new SimpleStringProperty(batchTableInitialValue(columnName, String.valueOf(data.getValue().getId()))));
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

        for (ArrayList<String> row : batchData) {
            for (String element : row) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }


    // Helper method to check if a column already exists in batchTable
    private boolean columnExists(String columnName) {
        for (TableColumn<BatchRow, ?> existingColumn : batchTable.getColumns()) {
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
            // Write header line
            writer.write("Searchspace,Problem,Algorithm");

            // Array of checkboxes and their corresponding text fields
            CheckBox[] checkboxes = {optimumReached, fitnessBound, iterationBound};
            TextField[] textFields = {fitnessTxtField, iterationTxtField};

            // Loop through checkboxes and write column headers for selected ones
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].isSelected()) {
                    writer.write("," + checkboxes[i].getText());
                }
            }
            writer.write("\n");

            // Write data values
            writer.write(searchspaceSelector.getValue() + ",");
            writer.write(problemSelector.getValue() + ",");
            writer.write(algorithmSelector.getValue());
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].isSelected()) {
                    writer.write(", true" );
                }
            }
            writer.write("\n");

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

    private List<String> getParameterSelection() {
        return Arrays.asList(
                getValueOrDefault(searchspaceSelector),
                getValueOrDefault(problemSelector),
                getValueOrDefault(algorithmSelector),
                getCheckBoxValue(optimumReached),
                getCheckBoxValue(fitnessBound),
                getCheckBoxValue(iterationBound)
        );
    }

    private String getValueOrDefault(ComboBox<?> comboBox) {
        return comboBox.getValue() != null ? comboBox.getValue().toString() : "";
    }

    private String getCheckBoxValue(CheckBox checkBox){
        return checkBox.isSelected() ? checkBox.getText() : "";
    }

    @FXML
    void checkboxHandler(ActionEvent event){
        CheckBox checkbox = (CheckBox) event.getSource();

        if (descriptions.containsKey(checkbox.getText())) {
            explainingLabel.setText(descriptions.get(checkbox.getText()));
        }

        // Check which checkbox is clicked
        if (checkbox.getText().equals("Fitness bound")) {
            fitnessTxtField.setDisable(!checkbox.isSelected());
        } else if (checkbox.getText().equals("Iteration bound")) {
            iterationTxtField.setDisable(!checkbox.isSelected());
        }

        boolean anyCheckboxChecked = fitnessBound.isSelected() || iterationBound.isSelected();
        iterationLabel.setDisable(!anyCheckboxChecked);
        updateBatchTable();
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
        if (selector == searchspaceSelector) {
                dimensionLabel.setDisable(false);
                dimensionTxtField.setDisable(false);
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
        // Increment the id counter
        int id = batchTable.getItems().size() + 1;

        // Create a new row with default values
        BatchRow newRow = new BatchRow(id);

        //add id
        //newRow.addData(String.valueOf(id));

        for (String category : batchColumns){
            newRow.addData(batchTableInitialValue(category, String.valueOf(id)));
        }

        // Fill the rest of the columns with "0"s
        for (int i = 1; i < batchColumns.size(); i++) {
            newRow.addData("0");
        }

        // Add the new row to the batch table
        batchTable.getItems().add(newRow);

        // Add the new row data to the batch data list
        batchData.add((ArrayList<String>) newRow.getRowData());
        addSchedule();


        }
        public void addSchedule(){
            Schedule newSchedule = new Schedule();
            if ((!dimensionLabel.isDisable() && dimensionTxtField.getText().equals("")) ||( !fitnessTxtField.isDisable() && fitnessTxtField.getText().equals("")) || (!iterationTxtField.isDisable() && iterationTxtField.getText().equals(""))) {
                showAlert("Please fill out missing information");
            }
            else {
                try {
                    int dimension = Integer.parseInt(dimensionTxtField.getText());
                    newSchedule.setDimension(dimension);
                    newSchedule.setSearchSpaceString(searchspaceSelector.getValue());
                } catch (Exception e) {
                    showAlert("Enter only integers for dimension");
                    return;

                }
                newSchedule.setProblemString(problemSelector.getValue());
                newSchedule.setAlgorithmString(algorithmSelector.getValue());
                if (optimumReached.isSelected())
                    newSchedule.setOptimumReached(true);
                if (fitnessBound.isSelected()) {
                    try {
                        int fitnessBound = Integer.parseInt(fitnessTxtField.getText());
                        newSchedule.setFitnessBound(fitnessBound);
                        System.out.println("fitness bound: " + fitnessBound);
                    } catch (Exception e) {
                        showAlert("Enter only integers for fitness bound");
                        return;
                    }
                }

                if (iterationBound.isSelected()) {
                    try {
                        int iterationBound = Integer.parseInt(iterationTxtField.getText());
                        newSchedule.setIterationBound(iterationBound);
                        System.out.println("iteration bound: " + iterationBound);
                    } catch (Exception e) {
                        showAlert("Enter only integers for iteration bound");
                        return ;
                    }

                }
                    newSchedule.setUpAlgorithm();
            }
        }

    private String batchTableInitialValue(String category, String id){
        String res = "0";

        if (category.equals("id")){
            res = id;
        }
        else if (category.equals("No. runs")){
            res = "1";
        }
        else if (category.equals("Dimension") && !dimensionTxtField.isDisable()){
            res = dimensionTxtField.getText();
        }
        else if(category.equals("F. Iterations") && !fitnessTxtField.isDisable()){
            res = fitnessTxtField.getText();
        }
        else if(category.equals("I. Iterations") && !iterationTxtField.isDisable()){
            res = iterationTxtField.getText();
        }



        return res;
    }


    @FXML
    void removeBatch(ActionEvent event){
        //TODO
    }

    @FXML
    void saveHandler(ActionEvent event) {
        if (!dimensionLabel.isDisable() && dimensionTxtField.getText().equals("")){
            showAlert("Dimension must be filled when \"" + searchspaceSelector.getValue() + "\" is chosen");
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
        scene.getStylesheets().add("chart-styles.css");
        stage.setScene(scene);
        stage.show();}

    @FXML
    void startMainPage(ActionEvent event) throws IOException {
        // Load the home page FXML file
        //make an array where you fill it with the chosen combobox values
        //make a hashmap of string
        if(batchData.size() == 0){
             addSchedule();
        }

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
            Parent root = loader.load();

            // Here you would get the controller if you need to call methods on it
            mainController controller = loader.getController();
            controller.recieveArray(Schedule.getSchedules()); // Call methods on the controller if needed
        System.out.println(Schedule.getSchedules().toString());

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
