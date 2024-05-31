package group.ea.structure.algorithm;

import group.ea.structure.TSP.City;
import group.ea.structure.TSP.Solution;
import group.ea.structure.TSP.TSPParser;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.*;

public class PermutationuPlusyEA extends Algorithm {
    double chance = 0.5;
    int lambda;
    int mu;
    Random rand = new Random();
    Solution bestSolution;

    ArrayList<Solution> population = new ArrayList<>();

    public PermutationuPlusyEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();
    }

    public void setMu(int mu) {this.mu = mu;}
    public void setLambda(int lambda) {this.lambda = lambda;}

    @Override
    public void initialize() {
        for (int i = 0; i < mu; i++) {
            Solution solution = new Solution((TSPParser) searchSpace);

            solution.shuffle(500);
            population.add(solution);

        }


    }

    @Override
    public void performSingleUpdate(int gen) {
        int n = searchSpace.length;

        while (true) {
            boolean threeOpt = false;
            for (int i = 0; i < lambda; i++) {
                if (generation == 0) {
                    //   listener.firstSolution(population.get(0));
                }
                Solution parent = population.get(rand.nextInt(mu)).clone();
                double tempChance = Math.random();

                if (tempChance < chance) {
                    //_sl.twoOptMutate2();
                    //_sl.ls3Opt();
                    parent.twoOptMutate();
                    threeOpt = false;

                } else {
                    //_sl.ls3Opt();
                    parent.random3Opt();
                    threeOpt = true;
                }
                population.add(parent);
            }
            population = selectFittest(population, mu);
            bestSolution = population.get(0);

            bestFitness = bestSolution.computeFitness();
            //System.out.println(bestFitness);
            TSPDATA tspdata = new TSPDATA(bestSolution, bestSolution.getSolution(), generation, bestFitness, bestSolution.getImprovement,"(u+y)EA");
            listener.receiveUpdate(tspdata);
            if (checkStoppingCriteria()) {
                break;
            }
            generation++;
            //System.out.println(generation);

        }
    }

    private ArrayList<Solution> selectFittest(List<Solution> newPopulation, int mu) {
        // Use a lambda expression to call the computeFitness() method
/*
        System.out.println("New population before sort ");
        for (Solution s : newPopulation) {
            System.out.println(s.computeFitness() + " ");
        }
        */

            newPopulation.sort(Comparator.comparingDouble(Solution::computeFitness));
/*
            System.out.println("New population after sort ");
            for (Solution s : newPopulation) {
                System.out.println(s.computeFitness() + " ");
            }
        */


            return new ArrayList<>(newPopulation.subList(0, mu));
        }
    }



