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
        //runExperimentTSP();
        //RUNACOTEST();

    }
    private static void runExperimentTSP() {
        int iterations = 1;
        int perfectCount = 0;
        int[] iterationsLength = {50000,100000,500000,1000000}; // Example lengths
        int runsPerObservation = 100;
        Schedule newSchedule = new Schedule();

        List<DataPoint> dataPoints = new ArrayList<>();

                int optimumAverage = 0;
                double cpuAverage = 0;
                for (int length : iterationsLength) {
                    int totalFitness = 0;
                    perfectCount = 0;
                    optimumAverage = 0;
                    cpuAverage = 0;
                    for (int run = 0; run < runsPerObservation; run++) {
                        newSchedule = new Schedule();
                        newSchedule.setTSPProblem("berlin52");
                        newSchedule.setSearchSpaceString("Permutations");
                        newSchedule.setProblemString("TSP");
                        newSchedule.setAlgorithmString("Simulated Annealing TSP");
                        newSchedule.setIterationBound(length);
                        newSchedule.setOptimumReached(true);
                        newSchedule.setUpAlgorithm();
                        newSchedule.getAlgorithm().runAlgorithm();
                        int thisRunFitness = newSchedule.getAlgorithm().getFitness();
                        cpuAverage += newSchedule.getAlgorithm().getTimer().getCurrentTimer();
                        if (thisRunFitness == 7544) {
                            perfectCount++;
                            optimumAverage += newSchedule.getAlgorithm().getGeneration();
                        }
                        totalFitness += thisRunFitness;
                    }
                    dataPoints.add(new DataPoint(length, totalFitness / runsPerObservation));
                    System.out.println("Done with length " + length + " CPU Average + " + cpuAverage / runsPerObservation);
                    saveDataToCSV("TSP_SA.csv", dataPoints);
                    //System.out.println("Done with length " + length + "mu and lambda value" + newSchedule.getAlgorithm().getMu() + " " + newSchedule.getAlgorithm().getLambda());
                    System.out.println(perfectCount);
                    if (perfectCount > 0) {
                        System.out.println("Average iterations for perfect runs: " + optimumAverage / perfectCount);
                    }

        }

        System.out.println("Experiment done");
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
    private static void RUNACOTEST() {
        System.out.println("INIT");
        int perfectCount = 0;
        int[] iterationsLength = {50,100}; // Example lengths
        int runsPerObservation = 50;
        double [] alfaValues = {1};
        int [] betaValues = {2};
        int [] amountOfAnts = {100};
        Schedule newSchedule = new Schedule();
        Timer timer = new Timer();

        List<DataPoint> dataPoints = new ArrayList<>();
        timer.startTimer("1, 20, 20, ls = 1");
        for(int ants : amountOfAnts){
            for(double alfa : alfaValues) {
                for(double beta : betaValues){
                    int optimumAverage = 0;
                    double cpuAverage = 0;
                    for (int length : iterationsLength) {
                        cpuAverage = 0;
                        int totalFitness = 0;
                        perfectCount = 0;
                        optimumAverage = 0;
                        for (int run = 0; run < runsPerObservation; run++) {
                            newSchedule= new Schedule();
                            newSchedule.setTSPProblem("berlin52");
                            newSchedule.setSearchSpaceString("Permutations");
                            newSchedule.setProblemString("TSP");
                            newSchedule.setOptional(new String[]{String.valueOf(ants), String.valueOf(alfa), String.valueOf(beta)});
                            newSchedule.setAlgorithmString("ACO Elitist");
                            newSchedule.setLocalSearch(true);
                            newSchedule.setUpdateRule("best-so-far(BS)");
                            newSchedule.setIterationBound(length);
                            newSchedule.setOptimumReached(true);
                            newSchedule.setUpAlgorithm();
                            newSchedule.getAlgorithm().runAlgorithm();
                            int thisRunFitness = newSchedule.getAlgorithm().getFitness();
                            cpuAverage += newSchedule.getAlgorithm().getTimer().getCurrentTimer();
                            if (thisRunFitness == 7544) {
                                perfectCount++;
                                optimumAverage += newSchedule.getAlgorithm().getGeneration();
                            }
                            totalFitness += thisRunFitness;
                        }
                        dataPoints.add(new DataPoint(length, totalFitness / runsPerObservation));
                        saveDataToCSV("TSP_experimentACO.csv", dataPoints);
                        System.out.println("Done with length " + length + "ANTS ALFA BETA and runs" + ants + " " + alfa + " " + beta + " " + runsPerObservation);
                        System.out.println("Done with length " + length + " CPU Average + " + cpuAverage / runsPerObservation);
                        System.out.println(perfectCount);
                        timer.endTimer();
                        if(perfectCount > 0){
                            System.out.println("Average iterations for perfect runs: " + optimumAverage / perfectCount);
                        }
                    }
                }

            }
        }

        System.out.println("Experiment done");

    }

}





