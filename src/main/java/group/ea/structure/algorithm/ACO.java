package group.ea.structure.algorithm;

import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

public class ACO extends Algorithm {

    protected double alpha = 1.0;
    protected double beta = 2.0;
    protected double evaporation = 0.6;
    protected double Q = 5.2;
    protected Random RNG = new Random();
    protected int dimension;
    protected boolean _localSearch;
    protected int numberOfAnts = 100;
    protected double probabilities[];
    protected double[][] pheromone;
    protected double[][] heuristic;
    protected double[][] graph;
    protected Ant bestAnt;
    protected double fuzzyRandom = 0.001;
    protected ArrayList<Ant> ants;
    protected Solution _sl;
    protected double bestInGeneration;
    protected boolean improvedInGeneration = false;
    int gain = 0;

    public ACO(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        dimension = _sl.getDimension();
        setupAnts();
        setupStructure();
        bestInGeneration = Double.MAX_VALUE;
        _localSearch = false;
    }

    @Override
    public void initialize() {
    }

    @Override
    public void performSingleUpdate(int generation) {
        improvedInGeneration = false;
        if(generation == 0){
            listener.firstSolution(_sl);
        }

        if (generation > maxGeneration) {
            if (_localSearch) {
                localSearch();
            }
            System.out.println("done");
            System.out.println("Best " + bestAnt.getCost());
            for (int i = 0; i < dimension; i++) {
                System.out.print(bestAnt.getTrailOfAnt()[i] + " ");
            }
            antToSolution(bestAnt);
            _sl.printSolution();
            stoppingMet = true;
            return;
        }

        Ants();
        updateEvaporation();
        // Using only the trail of the best ant
        updatePheromone(bestAnt);
        if(improvedInGeneration){
            antToSolution(bestAnt);
            TSPDATA tspdata = new TSPDATA(_sl,_sl.getSolution(),generation,(int) bestAnt.getCost(),gain);
            listener.receiveUpdate(tspdata);
        }
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
            if (a.getCost() < bestInGeneration) {

                bestAnt = a;
                bestInGeneration = a.getCost();
                improvedInGeneration = true;
            }
        }
        gain =(int) (temp - bestInGeneration);
    }

    public void updateEvaporation() {
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                pheromone[i][j] *= evaporation;
            }
        }
    }

    public void updatePheromone(Ant a) {
        double dTau = Q / a.getCost();
        for (int i = 0; i < dimension; i++) {
            int j = a.getTrailOfAnt()[i];
            int k = a.getTrailOfAnt()[(i + 1) % dimension];
            pheromone[j][k] += dTau;
            pheromone[k][j] = pheromone[j][k]; // Ensure symmetry
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

    }

    // The placeAnt method should place the ant at the starting city (index 0 for simplicity)
    private void placeAnt(Ant ant, int phase) {
        ant.visitCity(0);
    }

    public void copyFromTo(Ant from, Ant to) {
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

    public void setValues(double a, double b, int r) {
        System.out.println("Called " + a + b + r);
        setAlpha(a);
        setBeta(b);
        setAnts(r);
    }

    private void antToSolution(Ant a){
        int[] list = a.getTrailOfAnt();
        System.out.println("Ant to solution");
        _sl.setSolution(_sl.computeNewList(list));
    }

    @Override
    public Solution get_sl(){
        antToSolution(bestAnt);
        return _sl;
    }

}
