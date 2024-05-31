package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.TSP.Solution;
import group.ea.structure.helperClasses.Timer;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Algorithm {
    public int mu = 1;
    public int lambda = 1;
    Problem problem;

    Solution _sl;
    SearchSpace searchSpace;
    protected boolean stoppingMet = false;
    protected ArrayList<String> solutionList;

    protected List<Pair<Integer, Integer>> graphList;

    public List<Data> finalList = new ArrayList<>();

    protected int bestFitness;
    protected String bitString;
    protected Timer timer;

    int bitLength;
    protected int generation;
    private boolean hyperDone = true;
    private List<StoppingCriterion> stoppingCriteria = new ArrayList<>();

    protected double currentTemp = 10000;
    protected int maxGeneration = 998;

    protected AlgorithmUpdateListener listener;
    public Algorithm(SearchSpace searchSpace, Problem problem) {
        this.searchSpace = searchSpace;
        bitLength = searchSpace.length;
        //sl = (Solution) problem;
        this.problem = problem;
        this.initialize();
        timer = new Timer();
    }
    public void addStoppingCriterion(StoppingCriterion criterion) {
        stoppingCriteria.add(criterion);
    }

    protected boolean checkStoppingCriteria() {
        for (StoppingCriterion criterion : stoppingCriteria) {
            if (criterion.isMet(this)) {
                return true;
            }
        }
        return false;
    }

    public abstract void performSingleUpdate(int generation);

    public abstract void initialize();

    public void runAlgorithm() {
        timer.startTimer("Time elapsed");
        while (!checkStoppingCriteria()  && (bestFitness != 7544)) {
       // while(bestFitness != 7544) {


            performSingleUpdate(generation);


            generation++;
            if (generation % 1000000 == 0) {
                //System.out.println("1m + fitness = " + bestFitness);

            }


        }
        //System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness + "total generation sl" + Solution.getGeneration());
        Solution.setGeneration(0);
        stoppingMet = true;
        //finalList.get(finalList.size()-1).setYesNo(true);
    }

    public void clearAndContinue(int i, int newI) {

    }
    public int getBitStringLength() {
        return bitLength;
    }

    public int getFitness() {
        return bestFitness;
    }

    public int getGeneration() {
        return generation;
    }

    public int getCurrentTemp() {
        return (int) currentTemp;
    }
    public Solution get_sl() {
        return _sl;
    }

    public void sendListener(mainController controller) {
        this.listener = controller;
        //System.out.println("Listener set" + listener + " Controller sent= "+ controller);
    }
    public  void setValues(int a, double b, double r){

    }
}
