package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;

import java.util.ArrayList;
import java.util.Random;

//TODO
/*
FIX SO IT CAN BOTH THE BEST ANt AND EVERY ANT
TRY TO GET IT ON BITSTRING?
MAKE 1+1 EA work on TSP
MERGE SO IT WORKS WITH GRAPH

 */


public class ACO extends Algorithm{

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
    protected double[][] herustic;
    protected double[][] graph;
    protected Ant bestAnt;
    protected double fuzzyRandom = 0.001;
    protected ArrayList<Ant> ants;
    protected Solution _sl;
    protected double bestInGeneration;


    public ACO (SearchSpace searchSpace, Problem problem){
        super(searchSpace, problem);
        _sl = (Solution) problem;
        dimension = _sl.getDimension();
        setupAnts();
        setupStructure();
        bestInGeneration = Double.MAX_VALUE;
        _localSearch = false;
    }
    @Override
    public void initialize(){
    }

    @Override
    public void performSingleUpdate(int generation){
        if(generation > maxGeneration){
            if(_localSearch){
                localSearch(3);
            }
            System.out.println("done");
            System.out.println("Best" + bestAnt.getCost());
            for(int i = 0; i< dimension; i++){
                System.out.print(bestAnt.getTrailOfAnt()[i] + " ");
            }
            stoppingMet = true;
            return;
        }

        Ants();
        updateEvaporation();
        //Using only the trail of the best ant
        updatePheromone(bestAnt);

    }

    //First step of an update, here we take an enitre walkthrough of a city, first we clear data, then we place ants, then we go for the walk.
    public void Ants(){
        int step = 0;
        for(Ant a : ants){
            a.clearData();

        }
        placeAnts();

        //walk through city
        while (step < dimension - 1){
            step++;
            moveAnts(step);
        }

        //find best
        for(Ant a : ants){
            a.setCost(calculateAntCost(a.getTrailOfAnt()));
            if(a.getCost() < bestInGeneration){
                bestAnt = a;
                bestInGeneration = a.getCost();
            }
        }
    }

    //Then the trails will evaporate
    public void updateEvaporation(){
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                pheromone[i][j] *= evaporation;
            }
        }
    }

    //Then we add which paths has walked the most
    public void updatePheromone(Ant a) {
        double dTau = Q / a.getCost();
        for (int i = 0; i < dimension; i++) {
            int j = a.getTrailOfAnt()[i];
            int k = a.getTrailOfAnt()[(i + 1) % dimension];
            pheromone[j][k] += dTau;
            pheromone[k][j] = pheromone[j][k]; // Ensure symmetry
        }
    }

    //Helper function, this is figuring out which city ID to go to
    public void moveAnts(int step){
        for(Ant a : ants){
            a.visitCity(calculateCity(step, a));
        }
    }
    private int calculateCity(int step, Ant a){
        //First we take the random possiblity of just going a way
        int t = RNG.nextInt(dimension - step);
        //Fuzzyrandom just being a number to determine how random
        double test = RNG.nextDouble();
        if( test < fuzzyRandom){
            int cityIndex = 0;
            for (int i = 0; i < dimension; i++) {
                if (i == t && !a.visitedCity(i)) {
                    cityIndex = i;
                    break;
                }
            }
            // random city we havent visited before
            if(cityIndex != -1){
                return cityIndex;

            }

        }
        //Now we assume we go by calculations
        //first lets calculate the pheremone and heustic
        calculatePheremoneHeustic(step, a);

        //accumalative
        double r = RNG.nextDouble();
        double total = 0;
        for (int i = 0; i < dimension; i++) {
            total += probabilities[i];
            if (total >= r) {
                return i;
            }
        }
        System.out.println(total + "Total");
        return -1;
    }

    //math to use pheremones and heurestic
    /*
    SOURCES:



     */
    public void calculatePheremoneHeustic(int step, Ant a){
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

    public void setupStructure(){
        graph = new double[dimension][dimension];
        pheromone = new double[dimension][dimension];
        herustic = new double[dimension][dimension];
        probabilities = new double[dimension];

        for (int i = 0; i < dimension; i++){
            for (int j = 0; j < dimension; j++){
                if(i == j){
                    graph[i][j] = 0;
                }
                else{
                    graph[i][j] = _sl.distanceBetweenIndex(i,j);
                }

                pheromone[i][j] = Q;
                herustic[i][j] = 1.0 / _sl.distanceBetweenIndex(i, j) + 0.1;
            }
        }


    }

    public void setupAnts(){
        ants = new ArrayList<>();
        for(int i = 0; i < numberOfAnts; i++){
            ants.add(i, new Ant(dimension));
        }
        placeAnts();

        bestAnt = new Ant(dimension);
    }

    public void placeAnts(){
        for(Ant a : ants){
            a.visitCity(RNG.nextInt(dimension));
        }
    }

    public double calculateAntCost(int[] trail){
        double cost = 0.0;
        for (int i = 0; i < dimension; i++){
            if (i + 1 < dimension) {
                cost += _sl.distanceBetweenIndex(trail[i], trail[i+1]);
            } else {
                cost += _sl.distanceBetweenIndex(trail[i], trail[0]);
            }
        }
        return cost;
    }


    //FIX
    public void localSearch(int width){
        int step = 0;
        Ant temp = new Ant(dimension);
        System.out.println(bestAnt.getCost() + " best Before");
        copyFromTo(bestAnt, temp);
        System.out.println(temp.getCost() + " temp Before");
        while(step < dimension){
            for(int i = (step - width); i < (2 * width) + step; i++ ){
                for(int j = (step - width); j < (2 * width) + step - 1; j++ ){
                        if(i == j ){
                            continue;
                        }
                        temp.localMutate((i+dimension) % dimension, (j+dimension) % dimension);
                        temp.setCost(calculateAntCost(temp.getTrailOfAnt()));
                        if ( temp.getCost() < bestInGeneration){
                            copyFromTo(temp, bestAnt);
                            bestInGeneration = temp.getCost();
                            System.out.println("Swapping " + temp.getTrailOfAnt()[(i+dimension) % dimension] + " with "
                                    + temp.getTrailOfAnt()[(j+dimension) % dimension]);

                        }
                    }
            }
            copyFromTo(bestAnt, temp);
            System.out.println(bestAnt.getCost() + " best last");
            System.out.println(temp.getCost() + " temp last");
            step++;
        }

    }

    //FUCK POINTERE lol
    public void copyFromTo(Ant from, Ant to){
        to.setTour(new int[dimension]);
        to.setVisited(new boolean[dimension]);
        to.setCost(0.0);
        for(int i = 0; i < dimension; i++){
            to.getTrailOfAnt()[i] = from.getTrailOfAnt()[i];
            if (i < to.getVisited().length) {
                to.getVisited()[i] = true;
            }
        }
        to.setCost(from.getCost());
    }

    public void setAlpha(double s){
        this.alpha = s;
    }
    public void setBeta(double s){
        this.beta = s;
    }
    public void setAnts(int s){
        this.numberOfAnts = s;
    }

    public void setValues(double a, double b, int r){
        System.out.println("Called " + a + b + r );
        setAlpha(a);
        setBeta(b);
        setAnts(r);
    }

}
