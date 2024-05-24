package group.ea.controllers;

import group.ea.structure.TSP.Solution;
import group.ea.structure.TSP.TSPParser;
import group.ea.structure.algorithm.*;
import group.ea.structure.problem.LeadingOnes;
import group.ea.structure.problem.OneMax;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;

import java.util.ArrayList;

public class Schedule {
    private String searchSpaceString, problemString, algorithmString;
    private int id, dimension, fitnessBound = 0, iterationBound = 0;
    private Algorithm algorithm; // Hold an instance of the Algorithm
    private SearchSpace searchSpace; // To keep the search space instance
    private Problem problem; // To keep the problem instance
    private String criterias = "";
    private boolean optimumReached;
    private String[] optionalValues;

    private int finishedIterations = 0;

    private static ArrayList<Schedule> schedules = new ArrayList<>();

    private int numberOfRuns = 1;
    boolean tspBool = false;
    private ArrayList<StoppingCriterion> stoppingCriteria;

    public Schedule() {
    }

    public void setRuns(int runs) {
        numberOfRuns = runs;
    }

    public int getRuns() {
        return numberOfRuns;
    }

    public void addSchedule(Schedule schedule) {
        schedules.add(schedule);
    }

    public static ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setOptimumReached(boolean b) {
        optimumReached = b;
    }

    public boolean getOptimumReached() {
        return optimumReached;
    }

    public void setIterationBound(int i) {
        iterationBound = i;
    }

    public int getIterationBound() {
        return iterationBound;
    }

    public void setFitnessBound(int i) {
        fitnessBound = i;
    }

    public int getFitnessBound() {
        return fitnessBound;
    }

    public String getAlgorithmString() {
        return algorithmString;
    }

    public void setAlgorithmString(String algorithmString) {
        this.algorithmString = algorithmString;
    }

    public String getSearchSpaceString() {
        return searchSpaceString;
    }

    public void setSearchSpaceString(String searchSpaceString) {
        this.searchSpaceString = searchSpaceString;
    }

    public String getProblemString() {
        return problemString;
    }

    public void setProblemString(String problemString) {
        this.problemString = problemString;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setTSP(boolean b) {
        tspBool = b;
    }
    public String getCriterias(){
        return criterias;
    }
    public boolean getTSP() {
        return tspBool;
    }

    public void setUpAlgorithm() {
        criterias = "";
        switch (this.searchSpaceString) {

            case "Bit strings":
                this.searchSpace = new BitString(this.dimension);
                break;
            case "Permutations":
                this.searchSpace = new TSPParser("src/main/java/group/ea/controllers/berlin52.txt");
                break;
        }

        // Setup problem
        switch (this.problemString) {
            case "OneMax":
                this.problem = new OneMax(this.searchSpace);
                break;
            case "LeadingOnes":
                this.problem = new LeadingOnes(this.searchSpace);
                break;
            case "TSP":
                tspBool = true;
                assert this.searchSpace instanceof TSPParser;
                this.problem = new Solution((TSPParser) this.searchSpace);
                break;
        }

        // Setup algorithm
        switch (this.algorithmString) {
            case "RLS":
                this.algorithm = new RLS(this.searchSpace, this.problem);
                break;
            case "Simulated Annealing":
                this.algorithm = new SA(this.searchSpace, this.problem);
                break;
            case "(1+1) EA":
                this.algorithm = new onePlusOneEA(this.searchSpace, this.problem);
                break;
            case "UY (1+1 EA":
                this.algorithm = new uPlusyEA(this.searchSpace, this.problem);
                break;
            case "Permutation1+1EA":
                this.algorithm = new PermutationOnePlusOneEA(this.searchSpace, this.problem);
                break;
            case "TEMP":
                System.out.println("temp");
                this.algorithm = new PermutationSA(this.searchSpace, this.problem);
                algorithm.addStoppingCriterion(new TempStopping());
                break;
            case "Ant System":
                System.out.println("ANT");
                this.algorithm = new ACO(this.searchSpace, this.problem);
                this.algorithm.setValues(Double.parseDouble(optionalValues[0]), Double.parseDouble(optionalValues[1]), Integer.parseInt(optionalValues[2]));
                System.out.println(optionalValues[0]);
                algorithm.addStoppingCriterion(new TempStopping());
                break;
            default:
                this.algorithm = null;
                break;
        }


        if (optimumReached) {
            this.algorithm.addStoppingCriterion(new OptimumReached());
            criterias += "Optimum";
        }
        if (fitnessBound != 0) {
            System.out.println("Fitness bound: " + getFitnessBound());
           this.algorithm.addStoppingCriterion(new MaxFitnessCriterion(getFitnessBound()));
            criterias += " Fitness";
        }
        if (iterationBound != 0) {
            this.algorithm.addStoppingCriterion((new MaxGenerationsCriterion(getIterationBound())));
            criterias += " Iteration";
        }
        addSchedule(this);

    }
    public void run() {
        if (this.algorithm != null) {
            this.algorithm.runAlgorithm();
        }
    }

    public Problem getProblem() {
        return problem;
    }

    public SearchSpace getSearchSpace() {
        return searchSpace;
    }

    public int getFinishedIterations() {
        return finishedIterations;
    }

    public void setFinishedIterations(int finishedIterations) {
        this.finishedIterations = finishedIterations;
    }

    public void setOptional(String[] set){
        this.optionalValues = set;
    }


}
