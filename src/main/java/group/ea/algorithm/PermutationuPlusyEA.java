package group.ea.algorithm;

import group.ea.problem.TSP.Solution;
import group.ea.problem.TSP.TSPParser;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.*;

public class PermutationuPlusyEA extends Algorithm {
    double chance = 0.5;
    Random rand = new Random();


    ArrayList<Solution> population = new ArrayList<>();

    public PermutationuPlusyEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();
    }


    @Override
    public void initialize() {
        if(mu > 0) {
            for (int i = 0; i < mu; i++) {
                Solution solution = new Solution((TSPParser) searchSpace);
                solution.shuffle(500);
                population.add(solution);
                functionEvaluations++;
            }
        }
    }

    @Override
    public void performSingleUpdate(int gen) {
        int n = searchSpace.length;

        while (!checkStoppingCriteria()) {
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
            functionEvaluations += lambda;
            population = selectFittest(population, mu);
            _sl = population.getFirst();
            int oldBestFitness = bestFitness;
            bestFitness = _sl.computeFitness();

            //System.out.println(bestFitness);
            if(graphicsOn) {

                if (oldBestFitness > bestFitness) {
                    TSPDATA tspdata = new TSPDATA(_sl, _sl.getSolution(), generation, bestFitness, _sl.getImprovement, "(u+y)EA");
                    tspdata.setFunctionEvaluations(functionEvaluations);
                    tspdata.setTimeElapsed(timer.getCurrentTimer());
                    listener.receiveUpdate(tspdata);
                }

            }
            generation++;
        }
        generation--;
        stoppingMet = true;
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



