package group.ea.algorithm;

import group.ea.helperClasses.Data;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.*;
public class uPlusyEA extends Algorithm{
    ArrayList<String> population = new ArrayList<>();
    Random rand = new Random();
    public uPlusyEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
    }


    @Override
    public void performSingleUpdate(int gen) {
        int n = searchSpace.length;
        generation = 0;
        Data firstData = new Data(population.get(0), generation, bestFitness, true, Optional.empty(),false);
        listener.receiveBitstringUpdate(firstData);
        //System.out.println("Performing single update og lambda værdi = " + lambda);
        while (true) {
            List<String> newPopulation = new ArrayList<>(population);
            Data data = new Data(bitString, generation, bestFitness, false, Optional.empty(),false);

            for (int i = 0; i < lambda; i++) {
                String parent = population.get(rand.nextInt(mu));
                String offspring = parent;
                for (int j = 0; j < n; j++) {
                    if (Math.random() <= 1.0 / n) {
                        offspring = offspring.substring(0, j) + (offspring.charAt(j) == '0' ? '1' : '0') + offspring.substring(j + 1);
                    }
                }
                newPopulation.add(offspring);
            }

            population = selectFittest(newPopulation, mu);
            int oldFitness = bestFitness;

            bestFitness = (int) problem.computeFitness(population.get(0));
            if(bestFitness > oldFitness){
                data.setFitness(bestFitness);
                data.setYesNo(true);
            }

            data.setTimeElapsed(timer.getCurrentTimer());
            functionEvaluations+=mu;
            data.setFunctionEvaluations(functionEvaluations);
            listener.receiveBitstringUpdate(data);
            generation++;

        }

    }

    private ArrayList<String> selectFittest(List<String> newPopulation, int mu) {
        newPopulation.sort(Comparator.comparingDouble(problem::computeFitness).reversed());
        return new ArrayList<>(newPopulation.subList(0, mu));
    }



    @Override
    public void initialize() {
        System.out.println("Initializing u+lambda EA" + mu + " + " + lambda);
        if (mu > 0) {
            for (int i = 0; i < mu; i++) {
                bitString = searchSpace.init();
                population.add(bitString);
            }
        }
    }
}
