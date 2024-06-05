package group.ea;

import group.ea.controllers.Schedule;
import group.ea.controllers.mainController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class main extends Application {
    double x,y = 0;
    static mainController controller;
    @Override
    public void start(Stage stage) throws IOException {


        //FXMLLoader loader  = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        controller = loader.getController();
        Platform.runLater(root::requestFocus);

       // stage.initStyle(StageStyle.UNDECORATED); // no border
        // Load the FXML file
        /*
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(main.class.getResource("fxml/singlePage.fxml")));
        Parent root = loader.load();

        Platform.runLater(root::requestFocus); // don't focus any element initially

        controller = loader.getController();
*/

        root.setOnMousePressed( event -> {
            x = event.getSceneX();
            y = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);
        });

        // Set up the scene and stage
        //Scene scene = new Scene(root);
        stage.setScene( scene);
        stage.show();
        //runSingle();
    }

    public static void main(String[] args) {
       // runExperiment();
        //runExperiment3();
        launch(args);
       //runSingle();
       // runSingle2();
       // runExperimentTSP();



    }



    public static void runSingle(){

        int totalFitness = 0;
        int iterations = 10;
        int perfectCount = 0;
            for(int i = 0; i < iterations; i++) {
                Schedule newSchedule = new Schedule();
                newSchedule.setSearchSpaceString("Permutations");
                newSchedule.setProblemString("TSP");
                newSchedule.setAlgorithmString("u+y EA TSP");
                newSchedule.setIterationBound(100000);
                newSchedule.setUpAlgorithm();
                //newSchedule.getAlgorithm().sendListener(controller);
                newSchedule.getAlgorithm().runAlgorithm();
                int thisRunFitness = newSchedule.getAlgorithm().getFitness();
                if(thisRunFitness == 7544){
                    perfectCount++;
                }
                totalFitness += thisRunFitness;
                System.out.println(thisRunFitness);

                //controller.setSolution(newSchedule.getAlgorithm().get_sl());
            }
        System.out.println("Average iterations: " + totalFitness/iterations + " Perfect runs: " + perfectCount + " out of " + iterations);

               // controller.tspGraphics(newSchedule.getAlgorithm().get_sl());

    }
    public static void runSingle2(){
        int totalFitness = 0;
        int iterations = 10;
        int perfectCount = 0;
        for(int i = 0; i < iterations; i++) {
            Schedule newSchedule = new Schedule();
            newSchedule.setSearchSpaceString("Bit strings");
            newSchedule.setDimension(1000);
            newSchedule.setProblemString("OneMax");
            newSchedule.setAlgorithmString("UY (1+1 EA");
            newSchedule.setIterationBound(100000);
            newSchedule.setOptimumReached(true);
            newSchedule.setUpAlgorithm();
            newSchedule.getAlgorithm().runAlgorithm();
            int thisRunFitness = newSchedule.getAlgorithm().getGeneration();



            totalFitness += thisRunFitness;

            //controller.tspGraphics(newSchedule.getAlgorithm().get_sl());
        }
        System.out.println("Average fitness: " + totalFitness/iterations + " Perfect runs: " + perfectCount + " out of " + iterations);

        // controller.tspGraphics(newSchedule.getAlgorithm().get_sl());

    }
    private static void runExperimentTSP() {
        int iterations = 1;
        int perfectCount = 0;
        int[] iterationsLength = {100000,200000,300000,400000,500000,600000,700000,800000,900000,1000000}; // Example lengths

        int runsPerObservation = 200;
        List<DataPoint> dataPoints = new ArrayList<>();
        int optimumAverage = 0;

            for (int length : iterationsLength) {
                int totalFitness = 0;
                perfectCount = 0;
                optimumAverage = 0;

                for (int run = 0; run < runsPerObservation; run++) {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setSearchSpaceString("Permutations");
                    newSchedule.setProblemString("TSP");
                    newSchedule.setAlgorithmString("1+1 EA TSP");
                    newSchedule.setIterationBound(length);
                    newSchedule.setUpAlgorithm();
                    newSchedule.getAlgorithm().runAlgorithm();
                    int thisRunFitness = newSchedule.getAlgorithm().getFitness();
                    if (thisRunFitness == 7544) {
                        perfectCount++;
                        optimumAverage += newSchedule.getAlgorithm().getGeneration();
                    }

                    totalFitness += thisRunFitness;
                    // System.out.println("Done with run nr " + run + "with " + iterations + " iterations");

                    //controller.setSolution(newSchedule.getAlgorithm().get_sl());
                }
                dataPoints.add(new DataPoint(length, totalFitness / runsPerObservation));
                saveDataToCSV("TSP_experiment.csv", dataPoints);
                System.out.println("Done with length " + length);
                System.out.println(perfectCount);
                if (perfectCount > 0) {
                    System.out.println(optimumAverage / perfectCount);
                } else {
                    System.out.println("No perfect runs");
                }
            }

        System.out.println("Experiment done");

    }
    public static void runExperiment(){
        int[] bitStringLengths = {100,200,300,400,500}; // Example lengths
        int [] variableValue = {1};
        int runsPerObservation = 200;

        List<DataPoint> dataPoints = new ArrayList<>();
        for (int value : variableValue) {
            for (int length : bitStringLengths) {
                int totalIterations = 0;
                for (int run = 0; run < runsPerObservation; run++) {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setSearchSpaceString("Bit strings");
                    newSchedule.setDimension(length);
                    newSchedule.setProblemString("LeadingOnes");
                    newSchedule.setAlgorithmString("UY (1+1 EA");
                    //newSchedule.setIterationBound(100000);
                    newSchedule.setOptimumReached(true);
                    newSchedule.setUpAlgorithm();
                    newSchedule.getAlgorithm().lambda = 1;
                    newSchedule.getAlgorithm().runAlgorithm();
                    int iterations = newSchedule.getAlgorithm().getGeneration();
                    totalIterations = totalIterations + iterations;
                    //System.out.println("Done with run nr " + run + "with " + iterations + " iterations");
                }
                dataPoints.add(new DataPoint(length, totalIterations / runsPerObservation));
                saveDataToCSV("OneMaxUYmuLeadingOnes_experiment.csv", dataPoints);
                System.out.println("Done with length " + length + " and value " + value);
            }

        }
        System.out.println("Experiment done");
       // saveDataToCSV("onemax_experiment.csv", dataPoints);
    }

    public static void runExperiment3(){
        int[] bitStringLengths = {100,200,300,400,500}; // Example lengths
        int [] variableValue = {9};
        int runsPerObservation = 200;
        int realMuValue = 1;

        List<DataPoint> dataPoints = new ArrayList<>();
        for (int value : variableValue) {
            for (int length : bitStringLengths) {
                int totalIterations = 0;

                for (int run = 0; run < runsPerObservation; run++) {
                    Schedule newSchedule = new Schedule();
                    newSchedule.setSearchSpaceString("Bit strings");
                    newSchedule.setDimension(length);
                    newSchedule.setProblemString("OneMax");
                    newSchedule.setAlgorithmString("UY (1+1 EA");
                    //newSchedule.setIterationBound(100000);
                    newSchedule.setOptimumReached(true);
                    newSchedule.setUpAlgorithm();
                    newSchedule.getAlgorithm().lambda = 1;
                    newSchedule.getAlgorithm().mu = value;
                    newSchedule.getAlgorithm().runAlgorithm();
                    realMuValue = newSchedule.getAlgorithm().mu;
                    int iterations = newSchedule.getAlgorithm().getGeneration();
                    totalIterations = totalIterations + iterations;
                    //System.out.println("Done with run nr " + run + "with " + iterations + " iterations");
                }
                dataPoints.add(new DataPoint(length, totalIterations / runsPerObservation));
                saveDataToCSV("OneMaxUYMU_experiment.csv", dataPoints);
                System.out.println("Done with length " + length + " and value " + realMuValue);
            }

        }
        System.out.println("Experiment done");
        // saveDataToCSV("onemax_experiment.csv", dataPoints);
    }
    public static void saveDataToCSV(String filename, List<DataPoint> dataPoints) {
        try (FileWriter writer = new FileWriter(filename)) {
            writer.append("BitStringLength,Iterations\n");
            for (DataPoint dataPoint : dataPoints) {
                writer.append(dataPoint.getBitStringLength()).append(",")
                        .append(dataPoint.getIterations()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    }





/*
FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("fxml/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 740, 500);
        stage.setScene(scene);
        stage.show();
 */