package group.ea.structure.algorithm;

import group.ea.structure.TSP.City;
import group.ea.structure.TSP.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TSPDATA {
    public int optCase;
    public Solution solution;
    public int generation;
    public int fitness;
    public ArrayList<City> slSolution;
    public double improvement;
    private long timeElapsed = 0;
    public City X1,X2,X3,X4,X5,X6;

    public boolean opt3;

   // private static List<TspResultController> listeners = new ArrayList<>();
    static ArrayList<TSPDATA> allSolutions = new ArrayList<>();
    public TSPDATA (Solution solution, ArrayList<City> slSolution, int generation, int fitness, double improvement, City X1, City X2, City X3, City X4, Optional<City> A5, Optional<City> A6, Optional<Integer> optCase, boolean b) {
        this.solution = solution;
        this.generation = generation;
        this.fitness = fitness;
        this.improvement = improvement;
        this.X1 = X1;
        this.X2 = X2;
        this.X3 = X3;
        this.X4 = X4;
        this.X5 = A5.orElse(null); // Assign null if A5 is not present
        this.X6 = A6.orElse(null); // Assign null if A6 is not present
        this.optCase = optCase.orElse(-1); // Assign null if optCase is not present
        this.opt3 = b;
        this.slSolution = slSolution;
        allSolutions.add(this);
    }

    public TSPDATA(Solution solution, ArrayList<City> slSolution, int generation, int fitness, double improvement){
        this.solution = solution;
        this.generation = generation;
        this.fitness = fitness;
        this.improvement = improvement;
        this.slSolution = slSolution;
        allSolutions.add(this);
    }





    public Solution getSolution() {
        return solution;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }

    public int getGeneration() {
        return generation;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    public double getImprovement() {
        return improvement;
    }

    public void setImprovement(double improvement) {
        this.improvement = improvement;
    }


}
