package group.ea.algorithm;

import group.ea.controllers.mainController;
import group.ea.StoppingCriterias.StoppingCriterion;
import group.ea.problem.TSP.Solution;
import group.ea.helperClasses.Timer;
import group.ea.helperClasses.Data;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {
    public int mu = 1;
    public int lambda = 1;
    Problem problem;

    protected Solution _sl;
    protected int functionEvaluations = 0;
    protected Solution _cloneSl;

    public SearchSpace getSearchSpace() {
        return searchSpace;
    }
    public Problem getProblem(){
        return problem;
    }
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
        while (!checkStoppingCriteria()) {
            performSingleUpdate(generation);
            generation++;
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
    public  void setValues(int a, double b, double r){
    }
    public void setMu(int a){}
    public void setLambda(int a){}

    public void setUpdateRule(String rule){}
    public void setLocalSearch(boolean search){}
}
