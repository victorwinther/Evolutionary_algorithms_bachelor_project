package group.ea.algorithm;

import group.ea.StoppingCriterias.MaxGenerationsCriterion;
import group.ea.controllers.mainController;
import group.ea.StoppingCriterias.StoppingCriterion;
import group.ea.problem.TSP.Solution;
import group.ea.helperClasses.Timer;
import group.ea.helperClasses.Data;
import group.ea.problem.Problem;
import group.ea.searchspace.BitString;
import group.ea.searchspace.SearchSpace;

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
    protected int mu = 0;
    protected int lambda = 0;
    Problem problem;
    private boolean paused = false;
    private boolean stopped = false;

    protected Solution _sl;

    public int getFunctionEvaluations() {
        return functionEvaluations;
    }

    protected int functionEvaluations = 0;
    protected Solution _cloneSl;

    protected boolean graphicsOn = true;

    public SearchSpace getSearchSpace() {
        return searchSpace;
    }

    public Problem getProblem() {
        return problem;
    }

    SearchSpace searchSpace;
    protected boolean stoppingMet = false;
    protected ArrayList<String> solutionList;

    protected List<Pair<Integer, Integer>> graphList;

    public List<Data> finalList = new ArrayList<>();

    protected int bestFitness;
    protected String bitString;

    public Timer getTimer() {
        return timer;
    }

    protected Timer timer;

    int bitLength;
    protected int generation;
    private List<StoppingCriterion> stoppingCriteria = new ArrayList<>();

    protected double currentTemp = 10000;

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

    public int getMaxGenerations() {
        for (StoppingCriterion criterion : stoppingCriteria) {
            if (criterion instanceof MaxGenerationsCriterion) {
                MaxGenerationsCriterion maxGenCriterion = (MaxGenerationsCriterion) criterion;
                return maxGenCriterion.getMaxGenerations();
            }
        }
        return 100000;
    }

    public abstract void performSingleUpdate(int generation);

    public abstract void initialize();

    public void runAlgorithm() {
        timer.startTimer("Time elapsed");
        while (!checkStoppingCriteria() && !stoppingMet) {
            if (!paused) {
                performSingleUpdate(generation);
                generation++;
            }
            if (stopped) {
                break;
            }
        }

        if (searchSpace instanceof BitString) {
            Data firstData = new Data(bitString, generation, bestFitness, true, Optional.empty(), true);
            firstData.setFunctionEvaluations(functionEvaluations);
            firstData.setTimeElapsed(timer.getCurrentTimer());
            listener.receiveBitstringUpdate(firstData);
        } else if ((this instanceof ACO)) {
            ACO acoInstance = (ACO) this;
            TSPDATA tspdata = new TSPDATA(_cloneSl, _cloneSl.getSolution(), generation, (int) acoInstance.bestAnt.getCost(), functionEvaluations, "ACO", true);
            tspdata.setPhermone(acoInstance.getPheromone());
            tspdata.setTimeElapsed(timer.getCurrentTimer());
            tspdata.improved();
            listener.receiveUpdate(tspdata);
        } else {
            //System.out.println("stopped in algo");
            TSPDATA tspdata = new TSPDATA(_sl, _sl.getSolution(), generation, bestFitness, functionEvaluations, "(1+1)EA", true);
            tspdata.setTimeElapsed(timer.getCurrentTimer());
            tspdata.improved();
            //listener.receiveUpdate(tspdata);

        }

        Solution.setGeneration(0);
        stoppingMet = true;
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

    public void setValues(int a, double b, double r) {
    }

    public void setMu(int a) {
        mu = a;
    }

    public void setLambda(int a) {
        lambda = a;
    }

    public void setUpdateRule(String rule) {
    }

    public void setLocalSearch(boolean search) {
    }

    public void setInitTemp(double temp) {

    }

    public void setTempReduction(double temp) {

    }


    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;

    }

    public void stop() {
        stopped = true;
        resume(); // Ensure any paused threads are released
    }
}

