package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.TSP.Solution;
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
    Problem problem;

    Solution _sl;
    SearchSpace searchSpace;
    protected boolean stoppingMet = false;
    protected ArrayList<String> solutionList;

    protected List<Pair<Integer, Integer>> graphList;

    public List<Data> finalList = new ArrayList<>();

    protected int bestFitness;
    protected String bitString;

    int bitLength;
    protected int generation;
    private boolean hyperDone = true;
    private List<StoppingCriterion> stoppingCriteria = new ArrayList<>();

    protected double currentTemp = 10000;

    protected AlgorithmUpdateListener listener;
    public Algorithm(SearchSpace searchSpace, Problem problem) {
        this.searchSpace = searchSpace;
        bitLength = searchSpace.length;
        //sl = (Solution) problem;
        this.problem = problem;
        this.initialize();
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
        while (!checkStoppingCriteria() && !(bestFitness == 7544) ) {
            performSingleUpdate(generation);
            generation++;
            if (generation % 500000 == 0) {
                // Assuming you have a method to get the best fitness
                System.out.println("Generation " + generation + ": Best Fitness = " + bestFitness);
            }

        }
        System.out.println("Best fitness " + bestFitness + "generation" + generation);
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

    public void sendListener(mainController mainController) {
        this.listener = mainController;
    }
}
