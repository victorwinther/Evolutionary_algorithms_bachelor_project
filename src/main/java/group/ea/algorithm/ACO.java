package group.ea.algorithm;

import group.ea.problem.TSP.Solution;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.ArrayList;
import java.util.Random;

public class ACO extends Algorithm {

    protected double alpha;
    protected double beta;
    protected double evaporation = 0.5;
    protected double Q = 1.0;
    protected Random RNG = new Random();
    protected int dimension;
    protected boolean _localSearch;
    protected boolean _IBFlag;
    protected int numberOfAnts;
    protected double probabilities[];
    protected double[][] pheromone;
    protected double[][] heuristic;
    protected double[][] graph;
    protected Ant bestAnt;
    protected double fuzzyRandom = 0.00000;
    protected ArrayList<Ant> ants;
    int _generation;
    protected double bestInGeneration;
    protected boolean improvedInGeneration = false;
    int gain = 0;

    public ACO(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        dimension = _sl.getDimension();

        bestInGeneration = Double.MAX_VALUE;
        _localSearch = false;
        _IBFlag = false;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void performSingleUpdate(int generation) {
        _generation = generation;
        improvedInGeneration = false;


        if(generation == 0){
            //listener.firstSolution(_sl);
            System.out.println("Generation 0 values: " + alpha + " " + beta + " " + numberOfAnts);
        }



        if (generation > maxGeneration) {

            System.out.println("done");
            System.out.println("Best " + bestAnt.getCost());

            antToSolution(bestAnt);
            System.out.println("Fitness in solution before" + _cloneSl.computeFitness());
            if (_localSearch) {
                localSearch();
            }
            System.out.println("Fitness in solution after" + _cloneSl.computeFitness());



            TSPDATA tspdata = new TSPDATA(_cloneSl,_cloneSl.getSolution(),generation,(int) bestAnt.getCost(),gain,"ACO");
            listener.receiveUpdate(tspdata);
        }


        Ants();
        updateEvaporation();
        updatePheromone();

    }

    public void Ants() {
        int step = 0;
        for (Ant a : ants) {
            a.clearData();
        }
        placeAnts();

        // walk through city
        while (step < dimension - 1) {
            step++;
            moveAnts(step);
        }
        double temp = bestInGeneration;
        // find best
        for (Ant a : ants) {
            a.setCost(calculateAntCost(a.getTrailOfAnt()));
            if (a.getCost() < bestInGeneration - 2) {
                copyFromTo(a, bestAnt);
                bestInGeneration = a.getCost();
                improvedInGeneration = true;
            }
        }


        if (improvedInGeneration) {

            gain = (int) (temp - bestInGeneration);
            antToSolution(bestAnt);
            TSPDATA tspdata = new TSPDATA(_cloneSl,_cloneSl.getSolution(),generation,(int) bestAnt.getCost(),gain,"ACO");
            tspdata.setTimeElapsed(timer.getCurrentTimer());
            listener.receiveUpdate(tspdata);
            improvedInGeneration = false;
        }

    }


    public void updateEvaporation() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                pheromone[i][j] *= evaporation;
            }
        }
    }

    public void updatePheromoneBEST(Ant a) {
        double dTau = Q / a.getCost();
        for (int i = 0; i < dimension; i++) {
            int j = a.getTrailOfAnt()[i];
            int k = a.getTrailOfAnt()[(i + 1) % dimension];
            pheromone[j][k] += dTau;
            pheromone[k][j] = pheromone[j][k]; // Ensure symmetry
        }
    }

    public void updatePheromoneALL(){
        for(Ant a :  ants){
            updatePheromoneBEST(a);
        }
    }

    public void updatePheromone(){
        if(_IBFlag){
            // Using only the trail of the best ant, IB rule
            updatePheromoneBEST(bestAnt);
        }
        else {
            // Using the trail of all ants, standard rule
            updatePheromoneALL();
        }
    }



    public void moveAnts(int step) {
        for (Ant a : ants) {
            a.visitCity(calculateCity(step, a));
        }
    }

    private int calculateCity(int step, Ant a) {
        // First we take the random possibility of just going a way
        int t = RNG.nextInt(dimension - step);
        // Fuzzyrandom just being a number to determine how random
        double test = RNG.nextDouble();
        if (test < fuzzyRandom) {
            int cityIndex = 0;
            for (int i = 0; i < dimension; i++) {
                if (i == t && !a.visitedCity(i)) {
                    cityIndex = i;
                    break;
                }
            }
            // random city we haven't visited before
            if (cityIndex != -1) {
                return cityIndex;
            }
        }
        // Now we assume we go by calculations
        // first let's calculate the pheromone and heuristic
        calculatePheromoneHeuristic(step, a);

        // accumulative
        double r = RNG.nextDouble();
        double total = 0;
        for (int i = 0; i < dimension; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }
        return -1; // Fallback in case of an error
    }

    public void calculatePheromoneHeuristic(int step, Ant a) {
        int index = a.getTrailOfAnt(step - 1);
        double sumProb = 0.0;
        for (int i = 0; i < dimension; i++) {
            if (!a.visitedCity(i)) {
                sumProb += Math.pow(pheromone[index][i], alpha) * Math.pow(1.0 / graph[index][i], beta);
            }
        }
        for (int j = 0; j < dimension; j++) {
            if (a.visitedCity(j)) {
                probabilities[j] = 0.0;
            } else {
                double numerator = Math.pow(pheromone[index][j], alpha) * Math.pow(1.0 / graph[index][j], beta);
                probabilities[j] = numerator / sumProb;
            }
        }
    }

    public void setupStructure() {
        graph = new double[dimension][dimension];
        pheromone = new double[dimension][dimension];
        heuristic = new double[dimension][dimension];
        probabilities = new double[dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (i == j) {
                    graph[i][j] = 0;
                } else {
                    graph[i][j] = _sl.distanceBetweenIndex(i, j);
                }

                pheromone[i][j] = Q;
                heuristic[i][j] = 1.0 / _sl.distanceBetweenIndex(i, j) + 0.1;
            }
        }
    }

    public void setupAnts() {
        ants = new ArrayList<>();
        for (int i = 0; i < numberOfAnts; i++) {
            ants.add(new Ant(dimension));
        }
        placeAnts();

        bestAnt = new Ant(dimension);
        bestAnt.setCost(Double.MAX_VALUE);
    }

    public void placeAnts() {
        for (Ant a : ants) {
            a.visitCity(RNG.nextInt(dimension));
        }
    }

    public double calculateAntCost(int[] trail) {
        double cost = 0.0;
        for (int i = 0; i < dimension; i++) {
            if (i + 1 < dimension) {
                cost += _sl.distanceBetweenIndex(trail[i], trail[i + 1]);
            } else {
                cost += _sl.distanceBetweenIndex(trail[i], trail[0]);
            }
        }
        return cost;
    }

    public void localSearch() {
        for(int i = 0; i < 10; i++){
            _cloneSl.ls3Opt();
        }
    }



    public void copyFromTo(Ant from, Ant to) {
        to.clearData();
        to.setTour(new int[dimension]);
        to.setVisited(new boolean[dimension]);
        to.setCost(0.0);
        for (int i = 0; i < dimension; i++) {
            to.getTrailOfAnt()[i] = from.getTrailOfAnt()[i];
            if (i < to.getVisited().length) {
                to.getVisited()[i] = true;
            }
        }

        to.setCost(from.getCost());
    }

    public void setAlpha(double s) {
        this.alpha = s;
    }

    public void setBeta(double s) {
        this.beta = s;
    }

    public void setAnts(int s) {
        this.numberOfAnts = s;
    }

    public void setIBFlag(boolean flag){
        _IBFlag = flag;
    }

    public void setValues(int colonySize, double alpha, double beta) {
        System.out.println("Called " + colonySize +" " + alpha +" " + beta);
        setAlpha(alpha);
        setBeta(beta);
        setAnts(colonySize);
        setupAnts();
        setupStructure();
    }

    public void setUpdateRule(String rule){
        //TODO do something
    }
    public void setLocalSearch(boolean search){
        _localSearch = search;
    }

    private void antToSolution(Ant a){
        int[] list = a.getTrailOfAnt();
        _cloneSl = new Solution(_sl.get_tsp());
        _cloneSl.computeNewList(list);
    }


}
