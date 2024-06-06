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
    protected double fuzzyRandom = 0.002;
    protected ArrayList<Ant> ants;
    int _generation;
    protected double bestInGeneration;
    protected boolean improvedInGeneration = false;
    boolean flag;
    int _foundCity;
    int gain = 0;
    int _depth = 20;

    public ACO(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        dimension = _sl.getDimension();
        bestInGeneration = Double.MAX_VALUE;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void performSingleUpdate(int gen) {
        improvedInGeneration = false;





        if (generation > (getMaxGenerations() - 2)) {

            System.out.println("done");
            System.out.println("Best " + bestAnt.getCost());

            antToSolution(bestAnt);
            //System.out.println("Fitness in solution before" + _cloneSl.computeFitness());
            if (_localSearch) {
                localSearch();
            }


            TSPDATA tspdata = new TSPDATA(_cloneSl,_cloneSl.getSolution(),generation,(int) bestAnt.getCost(),gain,"ACO");
            tspdata.setTimeElapsed(timer.getCurrentTimer());
            tspdata.setFunctionEvaluations(functionEvaluations);
            tspdata.setStopped(true);
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
            functionEvaluations++;
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
            tspdata.setFunctionEvaluations(functionEvaluations);
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

        // first let's calculate the pheromone and heuristic
        calculatePheromoneHeuristic(step, a);
        if(flag){
            return _foundCity;
        }

        // accumulative
        double rand = RNG.nextDouble();
        double city = 0;
        for (int i = 0; i < dimension; i++) {
            city += probabilities[i];
            if (city >= rand) {
                return i;
            }
        }
        System.out.println("Error in calculateCity");
        return -1; // Fallback in case of an error
    }

    public void calculatePheromoneHeuristic(int step, Ant a) {
        flag = false;
        int index = a.getTrailOfAnt(step - 1);
        double sumProb = 0.0;
        for (int i = 0; i < dimension; i++) {
            if (!a.visitedCity(i)) {
                double pheromoneValue = pheromone[index][i];
                double graphValue = graph[index][i];
                sumProb += Math.pow(pheromoneValue, alpha) * Math.pow(1.0 / graphValue, beta);
            }
        }

        for (int j = 0; j < dimension; j++) {
            if (a.visitedCity(j)) {
                probabilities[j] = 0.0;
            } else if(sumProb <= 0.0){
                System.out.println("SumProb is 0, going into the next best city");
                flag = true;
                _foundCity = nnChooseBestCity(step, a);
            }
            else {
                double numerator = Math.pow(pheromone[index][j], alpha) * Math.pow(1.0 / graph[index][j], beta);
                probabilities[j] = numerator / sumProb;
            }
        }
    }

    public int nnChooseBestCity(int step, Ant a){
        int index;
        int next;
        int temp;
        double valueBest;
        double help;
        next = dimension;
        index = a.getTrailOfAnt()[step - 1];
        valueBest = -1;
        antToSolution(a);
        localSearch();
        for (int i = 0; i < _depth; i++) {
            temp = _cloneSl.getSolution().get(i).getId() % dimension;
            if (!a.visitedCity(i)) {
                help = Math.pow(pheromone[index][temp], alpha) * Math.pow(1.0 / graph[index][temp], beta);
                if (help > valueBest) {
                    valueBest = help;
                    next = i;
                }
            }
        }
        if(next == dimension){
            return chooseNextBestCity(step, a);
        }
        else {
            System.out.println("Error in nnchooseNextBestCity " + next);
            return next;
        }

    }

    public int chooseNextBestCity(int step, Ant a){
        int index = a.getTrailOfAnt(step - 1);
        int next = dimension;
        double valueBest = -1;
        for(int i = 0; i < dimension; i++){
            if(!a.visitedCity(i)){
                double help = Math.pow(pheromone[index][i], alpha) * Math.pow(1.0 / graph[index][i], beta);
                if(help > valueBest){
                    valueBest = help;
                    next = i;
                }
            }
        }
        System.out.println("Error in chooseNextBestCity " + next);
        return next;
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

        if (rule.equals("AS-update")){
            setIBFlag(false);
        }
        else if (rule.equals("best-so-far(BS)")){
            setIBFlag(true);
        }
        else if (rule.equals("Iteration Best (IB)")){
            setIBFlag(false);
        }
    }
    public void setLocalSearch(boolean search){
        _localSearch = search;
    }

    private void antToSolution(Ant a){
        int[] list = a.getTrailOfAnt();
        _cloneSl = new Solution(_sl.get_tsp());
        _cloneSl.computeNewList(list);
    }

    @Override
    public int getFitness() {
        return (int)bestAnt.getCost();
    }

}
