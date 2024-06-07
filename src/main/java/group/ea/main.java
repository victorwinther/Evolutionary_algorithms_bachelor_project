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
        //runSingle();
    }

    public static void main(String[] args) {

        File folder = new File("./ea.jar/");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    System.out.println("File " + listOfFiles[i].getName());
                } else if (listOfFiles[i].isDirectory()) {
                    System.out.println("Directory " + listOfFiles[i].getName());
                }
            }
        }

        //runExperiment();
        //runExperiment3();
        launch(args);
        //runSingle();
        // runSingle2();
        //runExperimentTSP();
        //RUNACOTEST();


    }


    public static void runSingle() {

        int totalFitness = 0;
        int iterations = 10;
        int perfectCount = 0;
        for (int i = 0; i < iterations; i++) {
            Schedule newSchedule = new Schedule();
            newSchedule.setSearchSpaceString("Permutations");
            newSchedule.setProblemString("TSP");
            newSchedule.setAlgorithmString("u+y EA TSP");
            newSchedule.setIterationBound(100000);
            newSchedule.setUpAlgorithm();
            //newSchedule.getAlgorithm().sendListener(controller);
            newSchedule.getAlgorithm().runAlgorithm();
            int thisRunFitness = newSchedule.getAlgorithm().getFitness();
            if (thisRunFitness == 7544) {
                perfectCount++;
            }
            totalFitness += thisRunFitness;
            System.out.println(thisRunFitness);

            //controller.setSolution(newSchedule.getAlgorithm().get_sl());
        }
        System.out.println("Average iterations: " + totalFitness / iterations + " Perfect runs: " + perfectCount + " out of " + iterations);

        // controller.tspGraphics(newSchedule.getAlgorithm().get_sl());

    }

    public static void runSingle2() {
        int totalFitness = 0;
        int iterations = 10;
        int perfectCount = 0;
        for (int i = 0; i < iterations; i++) {
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
        System.out.println("Average fitness: " + totalFitness / iterations + " Perfect runs: " + perfectCount + " out of " + iterations);

        // controller.tspGraphics(newSchedule.getAlgorithm().get_sl());

    }

    private static void runExperimentTSP() {
        int iterations = 1;
        int perfectCount = 0;
        int[] iterationsLength = {5000, 10000, 20000, 40000, 80000, 160000, 320000}; // Example lengths
        int runsPerObservation = 100;
        int[] variableValue = {2, 4, 6, 8};
        int[] mu_values = {2, 4, 6, 8};
        Schedule newSchedule = new Schedule();

        List<DataPoint> dataPoints = new ArrayList<>();
        for (int mu : variableValue) {
            for (int value : variableValue) {
                int optimumAverage = 0;
                for (int length : iterationsLength) {
                    int totalFitness = 0;
                    perfectCount = 0;
                    optimumAverage = 0;
                    for (int run = 0; run < runsPerObservation; run++) {
                        newSchedule = new Schedule();
                        newSchedule.setTSPProblem("berlin52");
                        newSchedule.setSearchSpaceString("Permutations");
                        newSchedule.setProblemString("TSP");
                        newSchedule.setMu(mu);
                        newSchedule.setLambda(value);
                        newSchedule.setAlgorithmString("(u+y) EA TSP");
                        newSchedule.setIterationBound(length);
                        newSchedule.setOptimumReached(true);
                        newSchedule.setUpAlgorithm();
                        newSchedule.getAlgorithm().runAlgorithm();
                        int thisRunFitness = newSchedule.getAlgorithm().getFitness();
                        if (thisRunFitness == 7544) {
                            perfectCount++;
                            optimumAverage += newSchedule.getAlgorithm().getGeneration();
                        }
                        totalFitness += thisRunFitness;
                    }
                    dataPoints.add(new DataPoint(length, totalFitness / runsPerObservation));
                    saveDataToCSV("TSP_experimentmu+lambda2.csv", dataPoints);
                    //System.out.println("Done with length " + length + "mu and lambda value" + newSchedule.getAlgorithm().getMu() + " " + newSchedule.getAlgorithm().getLambda());
                    System.out.println(perfectCount);
                    if (perfectCount > 0) {
                        System.out.println("Average iterations for perfect runs: " + optimumAverage / perfectCount);
                    }
                }
            }
        }

        System.out.println("Experiment done");

    }

    public static void runExperiment() {
        int[] bitStringLengths = {10, 20, 50, 100, 200, 500, 1000, 2000, 3000}; // Example lengths
        int runsPerObservation = 200;

        List<DataPoint> dataPoints = new ArrayList<>();

        for (int length : bitStringLengths) {
            int totalIterations = 0;
            for (int run = 0; run < runsPerObservation; run++) {
                Schedule newSchedule = new Schedule();
                newSchedule.setSearchSpaceString("Bit strings");
                newSchedule.setDimension(length);
                newSchedule.setProblemString("LeadingOnes");
                newSchedule.setAlgorithmString("Simulated Annealing");
                newSchedule.setOptimumReached(true);
                newSchedule.setUpAlgorithm();
                newSchedule.getAlgorithm().runAlgorithm();
                int iterations = newSchedule.getAlgorithm().getGeneration();
                totalIterations = totalIterations + iterations;

            }
            dataPoints.add(new DataPoint(length, totalIterations / runsPerObservation));
            saveDataToCSV("SA_experiment_LeadingOnesAd.csv", dataPoints);
            System.out.println("Done with length " + length);
        }


        System.out.println("Experiment done");
    }

    public static void runExperiment3() {
        int[] bitStringLengths = {100, 200, 300, 400, 500}; // Example lengths
        int[] variableValue = {9};
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
                    newSchedule.getAlgorithm().setMu(1);
                    newSchedule.getAlgorithm().setMu(value);
                    ;
                    newSchedule.getAlgorithm().runAlgorithm();
                    // realMuValue = newSchedule.getAlgorithm().getMu();
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


    private static void RUNACOTEST() {
        System.out.println("INIT");
        int perfectCount = 0;
        int[] iterationsLength = {1000}; // Example lengths
        int runsPerObservation = 50;
        double[] alfaValues = {1};
        int[] betaValues = {2};
        int[] amountOfAnts = {100};
        Schedule newSchedule = new Schedule();
        Timer timer = new Timer();

        List<DataPoint> dataPoints = new ArrayList<>();
        timer.startTimer("1, 20, 20, ls = 1");
        for (int ants : amountOfAnts) {
            for (double alfa : alfaValues) {
                for (double beta : betaValues) {
                    int optimumAverage = 0;
                    for (int length : iterationsLength) {
                        int totalFitness = 0;
                        perfectCount = 0;
                        optimumAverage = 0;
                        for (int run = 0; run < runsPerObservation; run++) {

                            System.out.println();
                            System.out.println();
                            newSchedule = new Schedule();
                            newSchedule.setTSPProblem("berlin52");
                            newSchedule.setSearchSpaceString("Permutations");
                            newSchedule.setProblemString("TSP");
                            newSchedule.setOptional(new String[]{String.valueOf(ants), String.valueOf(alfa), String.valueOf(beta)});
                            newSchedule.setAlgorithmString("ACO MMAS");
                            newSchedule.setLocalSearch(false);
                            newSchedule.setUpdateRule("best-so-far(BS)");
                            newSchedule.setIterationBound(length);
                            newSchedule.setOptimumReached(true);
                            newSchedule.setUpAlgorithm();
                            newSchedule.getAlgorithm().runAlgorithm();
                            int thisRunFitness = newSchedule.getAlgorithm().getFitness();
                            if (thisRunFitness == 7544) {
                                perfectCount++;
                                optimumAverage += newSchedule.getAlgorithm().getGeneration();
                            }
                            totalFitness += thisRunFitness;
                        }
                        dataPoints.add(new DataPoint(length, totalFitness / runsPerObservation));
                        saveDataToCSV("TSP_experimentACO.csv", dataPoints);
                        System.out.println("Done with length " + length + "ANTS ALFA BETA and runs" + ants + " " + alfa + " " + beta + " " + runsPerObservation);
                        System.out.println(perfectCount);
                        timer.endTimer();
                        if (perfectCount > 0) {
                            System.out.println("Average iterations for perfect runs: " + optimumAverage / perfectCount);
                        }
                    }
                }

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
}






/*
FXMLLoader fxmlLoader = new FXMLLoader(main.class.getResource("fxml/home.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 740, 500);
        stage.setScene(scene);
        stage.show();
 */