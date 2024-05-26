package group.ea;

import group.ea.controllers.Schedule;
import group.ea.structure.algorithm.Algorithm;
import group.ea.structure.algorithm.RLS;
import group.ea.structure.problem.OneMax;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
// make a forloop

public class main extends Application {
    double x,y = 0;
    static TspResultController controller;
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(main.class.getResource("fxml/homePage.fxml")));
        Scene scene = new Scene(root);
        //runSingle();


        //stage.initStyle(StageStyle.UNDECORATED); // no border
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
        //runExperiment();
        launch(args);
        //runSingle();
       // runSingle2();



    }
    public static void runSingle(){
        int totalFitness = 0;
        int iterations = 100;
        int perfectCount = 0;
            for(int i = 0; i < iterations; i++) {
                Schedule newSchedule = new Schedule();
                newSchedule.setSearchSpaceString("Permutations");
                newSchedule.setProblemString("TSP");
                newSchedule.setAlgorithmString("Permutation1+1EA");
                newSchedule.setIterationBound(100000);
                newSchedule.setUpAlgorithm();
                newSchedule.getAlgorithm().runAlgorithm();
                int thisRunFitness = newSchedule.getAlgorithm().getFitness();
                if(thisRunFitness == 7544){
                    perfectCount++;
                }
                totalFitness += thisRunFitness;

                //controller.tspGraphics(newSchedule.getAlgorithm().get_sl());
            }
        System.out.println("Average fitness: " + totalFitness/iterations + " Perfect runs: " + perfectCount + " out of " + iterations);

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
    public static void runExperiment(){
        int[] bitStringLengths = {10, 20, 50, 100, 200, 500, 1000,2000}; // Example lengths
        int runsPerObservation = 200;

        List<DataPoint> dataPoints = new ArrayList<>();

        for (int length : bitStringLengths) {
            int totalIterations = 0;

            for (int run = 0; run < runsPerObservation; run++) {
                Schedule newSchedule = new Schedule();
                newSchedule.setSearchSpaceString("Bit strings");
                newSchedule.setDimension(length);
                newSchedule.setProblemString("LeadingOnes");
                newSchedule.setAlgorithmString("(1+1) EA");
                //newSchedule.setIterationBound(100000);
                newSchedule.setOptimumReached(true);
                newSchedule.setUpAlgorithm();
                newSchedule.getAlgorithm().runAlgorithm();
                int iterations = newSchedule.getAlgorithm().getGeneration();
                totalIterations = totalIterations + iterations;
            }
            dataPoints.add(new DataPoint(length, totalIterations/runsPerObservation));
            saveDataToCSV("LeadingOnes_experiment.csv", dataPoints);
            System.out.println("Done with length " + length);
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