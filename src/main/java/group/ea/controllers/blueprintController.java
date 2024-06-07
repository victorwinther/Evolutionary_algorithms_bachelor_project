package group.ea.controllers;

import group.ea.main;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Label iterationLabel, dimensionLabel, specialLbl1, specialLbl2, specialLbl3, specialLbl4, specialLbl5, tspProblemLbl, updateRuleLbl;
    @FXML
    private TextField iterationTxtField, dimensionTxtField, fitnessTxtField, specialTxtField1, specialTxtField2, specialTxtField3, specialTxtField4, specialTxtField5, timeBoundTxtField;
    @FXML
    private ComboBox<String> searchspaceSelector, problemSelector, algorithmSelector, TSPSelector, updateRuleSelector;
    @FXML
    private CheckBox optimumReached, fitnessBound, iterationBound, optimalSetting, timeboundCheckbox, localSearchCheckbox;
    @FXML
    Button saveButton;
    @FXML
    ListView<String> batchList;
    FileChooser fileChooser = new FileChooser();

    private Map<String, List<String>> categoryOptions = new HashMap<>();
    private HashMap<String, String> descriptions = new HashMap<>();
    private HashMap<String, List<String>> batchParameters = new HashMap<>();
    private ArrayList<String> parameters = new ArrayList<>();
    private List<ComboBox<String>> allComboBoxes;
    private List<TextField> allTextFields;
    private List<CheckBox> allCheckboxes;
    private List<TextField> allSpecialTxtFields;
    private List<Label> allSpecialLbls;
    private final String[] categories = {"searchSpace", "problem", "algorithm"};
    private ArrayList<String> dependencies = new ArrayList<>();
    private ArrayList<String> batchColumns = new ArrayList<>();
    private ArrayList<String> batchData = new ArrayList<>();
    private int idCount = 0;

    private final String[] TSPproblems = {"berlin52", "bier127", "a280"};
    private final String[] ACOupdateRules = {"AS-update", "Iteration Best (IB)", "best-so-far(BS)"};

    private String[] optionalValues;


    private Stage stage;
    public HashMap<String,String> blueprintChoices = new HashMap<>();


    public void initialize(URL arg0, ResourceBundle arg1){
        //initialize components
        TSPSelector.setVisible(false);
        tspProblemLbl.setVisible(false);
        timeboundCheckbox.setVisible(false);
        timeBoundTxtField.setVisible(false);
        updateRuleSelector.setVisible(false);
        updateRuleLbl.setVisible(false);
        localSearchCheckbox.setVisible(false);
        Schedule.getSchedules().clear();
        allComboBoxes = Arrays.asList(searchspaceSelector, problemSelector, algorithmSelector);
        allTextFields = Arrays.asList(iterationTxtField, fitnessTxtField, timeBoundTxtField, dimensionTxtField, specialTxtField1, specialTxtField2, specialTxtField3, specialTxtField4, specialTxtField5);
        allCheckboxes = Arrays.asList(optimumReached, iterationBound, fitnessBound, timeboundCheckbox);
        allSpecialTxtFields = Arrays.asList(specialTxtField1, specialTxtField2, specialTxtField3, specialTxtField4, specialTxtField5);
        allSpecialLbls = Arrays.asList(specialLbl1, specialLbl2, specialLbl3, specialLbl4, specialLbl5);
        addCategoryOptions();
        addDescriptions();
        initializeBatchParameters();
        TSPSelector.getItems().addAll(TSPproblems);
        updateRuleSelector.getItems().addAll(ACOupdateRules);
        sortComboBoxItems(allComboBoxes);

        txtFieldListener(allTextFields);
        TSPSelector.setValue(TSPSelector.getItems().getFirst());
        updateRuleSelector.setValue(updateRuleSelector.getItems().getFirst());

        hideSpecialFields();


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

    private void txtFieldListener(List<TextField> allTextFields) {
        for (TextField textField : allTextFields) {
            applyIntegerTextFormatter(textField);
        }
    }

    private void updateCategories() {
        for (ComboBox<String> comboBox : allComboBoxes){
            String orgVal = comboBox.getValue();
            String category = getComboBoxCategory(comboBox);

            comboBox.getItems().removeAll(dependencies);

            for (String item : categoryOptions.get(category)){
                if (!dependencies.contains(item) && !comboBox.getItems().contains(item)) {
                    comboBox.getItems().add(item);
                }
            }
            if (dependencies.contains(orgVal)){
                comboBox.setValue(null);
            }
        }

        sortComboBoxItems(allComboBoxes);
    }

    private void sortComboBoxItems(List<ComboBox<String>> comboBoxes) {
        for (ComboBox<String> comboBox : comboBoxes) {
            ObservableList<String> items = comboBox.getItems();
            List<String> itemList = FXCollections.observableArrayList(items);
            Collections.sort(itemList);
            comboBox.setItems(FXCollections.observableArrayList(itemList));
        }
    }

    private void addCategoryOptions() {
        categoryOptions.put("searchSpace", Arrays.asList("Bit strings", "Permutations"));
        categoryOptions.put("problem", Arrays.asList("OneMax", "LeadingOnes", "TSP"));
        categoryOptions.put("algorithm", Arrays.asList("(1+1) EA","(u+y) EA", "RLS", "(1+1) EA TSP", "Simulated Annealing","ACO", "ACO MMAS", "ACO Elitist", "Simulated Annealing TSP"));
        categoryOptions.put("tsp problems", Arrays.asList("berlin52", "bier127", "a280"));
        categoryOptions.put("updateRule", Arrays.asList("AS-update", "Iteration Best (IB)", "best-so-far(BS)"));
    }

    private void initializeBatchParameters() {
        batchParameters.put("ACO", List.of("Colony size", "Alpha", "Beta", "Update rule", "Local search"));
        batchParameters.put("ACO MMAS", List.of("Colony size", "Alpha", "Beta", "Update rule", "Local search"));
        batchParameters.put("ACO Elitist", List.of("Colony size", "Alpha", "Beta", "Update rule", "Local search"));
        batchParameters.put("(u+y) EA", List.of("u","y"));
        batchParameters.put("Fitness bound", List.of("F. Iterations"));
        batchParameters.put("Iteration bound", List.of("I. Iterations"));
        batchParameters.put("Optimum reached", List.of("Optimal"));
        batchParameters.put("TSP",List.of("TSP problem"));
        batchParameters.put("Bit strings",List.of("Dimension"));
    }

    private void addDescriptions(){
        descriptions.put("Bit strings", "The search space consisting of bit strings i.e. the set {0,1}^n, where n is the dimension.");
        descriptions.put("Permutations", "Represents all permutations in the symmetric group S_{dimension}. That are all permutations of the elements {1,...,dimension}.");

        descriptions.put("OneMax", "The fitness value of an individual is the number of ones in its genotype.");
        descriptions.put("LeadingOnes", "The number of leading ones or the length of the largest coherent block of ones from the first position in the bit string corresponds to the fitness value.");
        descriptions.put("TSP", "A classic combinatorial optimization problem where the goal is to find the shortest possible route that visits a set of cities exactly once and returns to the origin city.");

        descriptions.put("(1+1) EA", "The (1+1) EA is a simple evolutionary strategy that involves maintaining a single individual in the population, generating a mutated offspring, and replacing the current individual with the offspring only if it has higher fitness.");
        descriptions.put("(u+y) EA", "An evolutionary optimization algorithm where a population of u parents generates y offspring each generation.");
        descriptions.put("(1+1) EA TSP", "The (1+1) EA is a simple evolutionary strategy that involves maintaining a single individual in the population, generating a mutated offspring, and replacing the current individual with the offspring only if it has higher fitness.");
        descriptions.put("RLS", "A simple optimization algorithm that iteratively improves a solution by making small random changes and selecting better solutions.");
        descriptions.put("Simulated Annealing", "An optimization algorithm inspired by the annealing process in metallurgy. It iteratively explores the solution space by making random changes to the current solution.");
        descriptions.put("ACO", "A nature-inspired optimization algorithm based on the foraging behavior of ants. It uses a population of artificial ants to find optimal solutions by simulating the way real ants deposit pheromones to communicate paths to resources.");
        descriptions.put("ACO MMAS", "A nature-inspired optimization algorithm based on the foraging behavior of ants. It uses a population of artificial ants to find optimal solutions by simulating the way real ants deposit pheromones to communicate paths to resources.");
        descriptions.put("ACO Elitist", "A nature-inspired optimization algorithm based on the foraging behavior of ants. It uses a population of artificial ants to find optimal solutions by simulating the way real ants deposit pheromones to communicate paths to resources.");

        descriptions.put("Optimum reached", "Stops a run if the specified fitness bound is reached.");
        descriptions.put("Fitness bound", "Stops a run if a certain fitness is reached.");
        descriptions.put("Iteration bound", "Stops a run if a certain amount of iterations is reached");
        descriptions.put("Time elapsed", "Stops a run after a certain amount of time has passed");

    }

    private String getComboBoxCategory(ComboBox<?> comboBox) {
        int index = allComboBoxes.indexOf(comboBox);
        return categories[index];
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
            // Write data values
            writer.write("Searchspace, ");
            writer.write(searchspaceSelector.getValue() + "\n");
            writer.write("Problem, ");
            writer.write(problemSelector.getValue() + "\n");
            writer.write("Algorithm, ");
            writer.write(algorithmSelector.getValue() + "\n");

            if (searchspaceSelector.getValue().equals("Bit strings") && batchData.isEmpty()){
                writer.write("Dimension, ");
                writer.write(dimensionTxtField.getText() + "\n");
            }

            if (batchData.isEmpty()){
                writer.write("Stopping criterias, ");
                for (int i = 0; i < allCheckboxes.size(); i++){
                    CheckBox cb = allCheckboxes.get(i);
                    if (cb.isSelected()){
                        if (cb.getText().equals("Optimum reached")){
                            writer.write(cb.getText() + ", ");
                        } else {
                            writer.write(cb.getText() + ", ");
                            writer.write(allTextFields.get(i-1).getText() + ", ");
                        }

                    }
                }
                writer.write( "\n");
                if (specialTxtField1.isVisible()){
                    writer.write("Special parameters, ");
                    for (TextField txtField : allSpecialTxtFields) {
                        if (txtField.isVisible()) {
                            writer.write(txtField.getText() + ", ");
                        }
                    }
                    writer.write( "\n");
                }
                if (problemSelector.getValue().equals("TSP")){
                    writer.write("TSP problem, ");
                    writer.write(TSPSelector.getValue() + "\n");
                }
                if (algorithmSelector.getValue().equals("ACO MMAS") || algorithmSelector.getValue().equals("ACO Elitist") || algorithmSelector.getValue().equals("ACO")){
                    writer.write("ACO update rule, ");
                    writer.write(updateRuleSelector.getValue() + "\n");
                    writer.write("local search, " + localSearchCheckbox.isSelected() + "\n");
                }
            }
            else {
                // Write batch table data to the file
                writer.write("batch\n");
                for (String schedule : batchData) {
                    writer.write(schedule + "\n");
                }
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
                getValueOrDefault(TSPSelector),
                getValueOrDefault(updateRuleSelector),
                getCheckBoxValue(localSearchCheckbox),
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

    private void hideSpecialFields(){
        specialLbl1.setVisible(false);
        specialLbl2.setVisible(false);
        specialLbl3.setVisible(false);
        specialLbl4.setVisible(false);
        specialLbl5.setVisible(false);


        specialTxtField1.setVisible(false);
        specialTxtField2.setVisible(false);
        specialTxtField3.setVisible(false);
        specialTxtField4.setVisible(false);
        specialTxtField5.setVisible(false);

        optimalSetting.setVisible(false);
    }

    private void checkSpecialParameters(String selectedAlgo){
        hideSpecialFields();
        System.out.println(selectedAlgo);
        if (selectedAlgo.equals("ACO MMAS") || selectedAlgo.equals("ACO Elitist") || selectedAlgo.equals("ACO")){
            specialLbl1.setText("Colony size");
            specialLbl2.setText("Alpha");
            specialLbl3.setText("Beta");
            specialLbl1.setVisible(true);
            specialLbl2.setVisible(true);
            specialLbl3.setVisible(true);


            specialTxtField1.setVisible(true);
            specialTxtField2.setVisible(true);
            specialTxtField3.setVisible(true);

            optimalSetting.setVisible(true);

            updateRuleSelector.setVisible(true);
            updateRuleLbl.setVisible(true);
            localSearchCheckbox.setVisible(true);

        } else {
            updateRuleSelector.setVisible(false);
            updateRuleLbl.setVisible(false);
            localSearchCheckbox.setVisible(false);
        }
        if (selectedAlgo.equals("(u+y) EA")){
            specialLbl1.setText(("u"));
            specialLbl2.setText("y");
            specialLbl1.setVisible(true);
            specialLbl2.setVisible(true);

            specialTxtField1.setVisible(true);
            specialTxtField2.setVisible(true);

            optimalSetting.setVisible(true);
        }

    }

    @FXML
    void checkboxHandler(ActionEvent event){
        CheckBox checkbox = (CheckBox) event.getSource();

        if (descriptions.containsKey(checkbox.getText())) {
            explainingLabel.setText(descriptions.get(checkbox.getText()));
        }

        // Check which checkbox is clicked
        if (checkbox.getText().equals("Fitness bound")) {
            if (!checkbox.isSelected()){
                fitnessTxtField.setDisable(true);
                fitnessTxtField.clear();
            } else {
                fitnessTxtField.setDisable(false);
            }

        } else if (checkbox.getText().equals("Iteration bound")) {
            if (!checkbox.isSelected()){
                iterationTxtField.setDisable(true);
                iterationTxtField.clear();
            } else {
                iterationTxtField.setDisable(false);
            }
        }

        boolean anyCheckboxChecked = fitnessBound.isSelected() || iterationBound.isSelected() || timeboundCheckbox.isSelected();
        iterationLabel.setDisable(!anyCheckboxChecked);
        //updateBatchTable();
    }

    @FXML
    void optimalSettingsHandler(ActionEvent event){
        if (optimalSetting.isSelected()){
            if (Objects.equals(algorithmSelector.getValue(), "ACO MMAS")) {
                specialTxtField1.setText("100");
                specialTxtField2.setText("1");
                specialTxtField3.setText("20");
            }
            else if (Objects.equals(algorithmSelector.getValue(), "ACO Elitist") || Objects.equals(algorithmSelector.getValue(), "ACO")){
                specialTxtField1.setText("100");
                specialTxtField2.setText("1");
                specialTxtField3.setText("2");
            }
            else if (Objects.equals(algorithmSelector.getValue(), "(u+y) EA")){
                specialTxtField1.setText("5");
                specialTxtField2.setText("3");
            }
        }
        else {
            specialTxtField1.clear();
            specialTxtField2.clear();
            specialTxtField3.clear();
            specialTxtField4.clear();
            specialTxtField5.clear();
        }

    }

    @FXML
    void selectionHandler(ActionEvent event){
        ComboBox<?> selector = (ComboBox<?>) event.getSource();
        try {
            String selectorValue = selector.getSelectionModel().getSelectedItem().toString();

            //update depending on combobox selection
            if (selector == algorithmSelector){
                checkSpecialParameters(selectorValue);
            }

            // Show description
            if (descriptions.containsKey(selectorValue)) {
                explainingLabel.setText(descriptions.get(selectorValue));
            }


            //category dependencies logic
            if (selector == searchspaceSelector) {
                dimensionLabel.setDisable(false);
                dimensionTxtField.setDisable(false);

                if (selector.getValue().equals("Bit strings")) {
                    dependencies.addAll(Arrays.asList("TSP", "(1+1) EA TSP", "Simulated Annealing TSP","ACO", "ACO MMAS", "ACO Elitist"));
                    dependencies.removeAll(Arrays.asList("OneMax", "LeadingOnes", "(1+1) EA", "RLS", "Simulated Annealing"));
                } else if (selector.getValue().equals("Permutations")) {
                    dependencies.removeAll(Arrays.asList("TSP", "(1+1) EA TSP", "Simulated Annealing TSP", "ACO", "ACO MMAS", "ACO Elitist"));
                    dependencies.addAll(Arrays.asList("OneMax", "LeadingOnes", "(1+1) EA", "RLS", "Simulated Annealing"));
                    dimensionLabel.setDisable(true);
                    dimensionTxtField.setDisable(true);
                }
                updateCategories();
            }

            if (problemSelector.getValue().equals("TSP")){
                TSPSelector.setVisible(true);
                tspProblemLbl.setVisible(true);
            } else {
                TSPSelector.setVisible(false);
                tspProblemLbl.setVisible(false);
            }

        } catch (NullPointerException ignored) {
            TSPSelector.setVisible(false);
            tspProblemLbl.setVisible(false);
        }

    }

    @FXML
    void createNewBatch(ActionEvent event) {
        if (checkParametersFilled()) {
            showAlert("Please fill out missing information");
        }
        else {
            // Increment the id counter
            idCount = idCount + 1;

            searchspaceSelector.setDisable(true);
            problemSelector.setDisable(true);
            algorithmSelector.setDisable(true);

            ArrayList<String> scheduleParameters = new ArrayList<>(List.of("id"));

            List<String> currentSelection = getParameterSelection();
            for (String selection : currentSelection) {
                if (batchParameters.containsKey(selection)) {
                    scheduleParameters.addAll(batchParameters.get(selection));
                }
            }

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < scheduleParameters.size(); i++) {
                String category = scheduleParameters.get(i);
                String value = batchTableInitialValue(category, String.valueOf(idCount));
                builder.append(category).append(", ").append(value);
                if (i < scheduleParameters.size() - 1) {
                    builder.append("; ");
                }
            }

            batchList.getItems().add(builder.toString());

            batchData.add(builder.toString());

            //clearTxtFields();
            addSchedule();
        }
    }


    public void addSchedule(){
        Schedule newSchedule = new Schedule();

        try {
            int dimension = Integer.parseInt(dimensionTxtField.getText());
            if(dimension > 10000){
                showAlert("Dimension must be less than 10000");
                return;
            }
            newSchedule.setDimension(dimension);
            newSchedule.setSearchSpaceString(searchspaceSelector.getValue());
        } catch (Exception e) {
            showAlert("Enter only integers for dimension");
            return;

        }

        if (algorithmSelector.getValue().equals("(u+y) EA")){
            if (problemSelector.getValue().equals("TSP")){
                newSchedule.setAlgorithmString("(u+y) EA TSP");
            }
            else {
                newSchedule.setAlgorithmString("(u+y) EA");
            }
        }
        else {
            newSchedule.setAlgorithmString(algorithmSelector.getValue());
        }

        newSchedule.setProblemString(problemSelector.getValue());

        if (optimumReached.isSelected()) {
            newSchedule.setOptimumReached(true);
        }
        if (fitnessBound.isSelected()) {
            try {
                int fitnessBound = Integer.parseInt(fitnessTxtField.getText());
                newSchedule.setFitnessBound(fitnessBound);
            } catch (Exception e) {
                showAlert("Enter only integers for fitness bound");
                return;
            }
        }
        if (TSPSelector.isVisible()){
            newSchedule.setTSPProblem(TSPSelector.getValue());
        }

        if (iterationBound.isSelected()) {
            try {
                int iterationBound = Integer.parseInt(iterationTxtField.getText());
                newSchedule.setIterationBound(iterationBound);
            } catch (Exception e) {
                showAlert("Enter only integers for iteration bound");
                return ;
            }

        }
        if (algorithmSelector.getValue().equals("ACO MMAS") || algorithmSelector.getValue().equals("ACO Elitist") || algorithmSelector.getValue().equals("ACO")){
            try{
                String colonySize = specialTxtField1.getText();
                String alpha = specialTxtField2.getText();
                String beta = specialTxtField3.getText();
                String updateRule = updateRuleSelector.getValue();
                boolean localSearch = localSearchCheckbox.isSelected();
                optionalValues = new String[]{colonySize, alpha, beta};

                newSchedule.setUpdateRule(updateRule);
                newSchedule.setLocalSearch(localSearch);
                newSchedule.setOptional(optionalValues);
            } catch (Exception e) {
                showAlert("Enter only integers for iteration bound");
                return ;
            }


        }
        if (algorithmSelector.getValue().equals("(u+y) EA")) {
            try {
                int mu = Integer.parseInt(specialTxtField1.getText());
                int lambda = Integer.parseInt(specialTxtField2.getText());
                newSchedule.setMu(mu);
                newSchedule.setLambda(lambda);
            } catch (Exception e) {
                showAlert("Enter only integers for mu and lambda");
                return;
            }
        }

        newSchedule.setUpAlgorithm();
    }

    private boolean checkParametersFilled(){
        return (!iterationTxtField.isDisabled() && iterationTxtField.getText().isEmpty()) ||
                (!dimensionTxtField.isDisabled() && dimensionTxtField.getText().isEmpty()) ||
                (!fitnessTxtField.isDisabled() && fitnessTxtField.getText().isEmpty()) ||
                (specialTxtField1.isVisible() && specialTxtField1.getText().isEmpty()) ||
                (specialTxtField2.isVisible() && specialTxtField2.getText().isEmpty()) ||
                (specialTxtField3.isVisible() && specialTxtField3.getText().isEmpty()) ||
                (specialTxtField4.isVisible() && specialTxtField4.getText().isEmpty()) ||
                (specialTxtField5.isVisible() && specialTxtField5.getText().isEmpty()) ||
                (TSPSelector.getValue() == null) ||
                (updateRuleSelector.getValue() == null) ||
                (searchspaceSelector.getValue() == null) ||
                (problemSelector.getValue() == null) ||
                (algorithmSelector.getValue() == null) ;
    }

    private void applyIntegerTextFormatter(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                showAlert("Please enter only integers.");
                textField.setText(oldValue);
            }
        });
    }


    private String batchTableInitialValue(String category, String id){
        String res = "";

        if (category.equals("id")){
            res = id;
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
        else if(category.equals("Colony size")){
            res = specialTxtField1.getText();
        }
        else if(category.equals("Alpha")){
            res = specialTxtField2.getText();
        }
        else if(category.equals("Beta")){
            res = specialTxtField3.getText();
        }
        else if(category.equals("u")){
            res = specialTxtField1.getText();
        }
        else if(category.equals("y")){
            res = specialTxtField2.getText();
        }
        else if(category.equals("TSP problem")){
            res = TSPSelector.getValue();
        }
        else if(category.equals("Update rule")){
            res = updateRuleSelector.getValue();
        }
        else if(category.equals("Local search")){
            res = String.valueOf(localSearchCheckbox.isSelected());
        }
        else if(category.equals("Optimal")){
            res = "true";
        }

        return res;
    }


    @FXML
    void removeBatch(ActionEvent event){
        String selectedItem = batchList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            batchList.getItems().remove(selectedItem);
            batchData.removeIf(s -> s.equals(selectedItem));
        }
        if (batchData.isEmpty()){
            searchspaceSelector.setDisable(false);
            problemSelector.setDisable(false);
            algorithmSelector.setDisable(false);
        }
    }

    @FXML
    void saveHandler(ActionEvent event) {
        if (checkParametersFilled()){
            showAlert("Please fill out missing information");
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
        //scene.getStylesheets().add("chart-styles.css");
        stage.setScene(scene);
        stage.show();}

    @FXML
    void startMainPage(ActionEvent event) throws IOException {
        // Load the home page FXML file
        //make an array where you fill it with the chosen combobox values
        //make a hashmap of string
        if(batchData.isEmpty()){
             addSchedule();
        }

            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
            Parent root = loader.load();

            // Here you would get the controller if you need to call methods on it
            mainController controller = loader.getController();
            controller.recieveArray(Schedule.getSchedules()); // Call methods on the controller if needed

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
}
