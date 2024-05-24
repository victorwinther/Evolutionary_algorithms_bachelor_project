package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;

import java.util.Optional;

public class onePlusOneEA extends Algorithm {

    public onePlusOneEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
    }

    @Override
    public void performSingleUpdate(int gen) {
        int n = bitString.length();
        generation = 0;
        while (!checkStoppingCriteria()) {
            StringBuilder y = new StringBuilder(bitString);
            Data data = new Data(bitString, generation, bestFitness, false, Optional.empty());
            for (int i = 0; i < n; i++) {
                if (Math.random() <= 1.0 / n) {
                    y.setCharAt(i, y.charAt(i) == '0' ? '1' : '0');
                }
            }
            String yString = y.toString();
            double yFitness = problem.computeFitness(yString);
            //double currentFitness = problem.computeFitness(bitString);

            if (yFitness >= bestFitness) {
                if (yFitness > bestFitness) {
                    data.setYesNo(true);
                }
                bitString = yString;
                bestFitness = (int) yFitness;
                data.setBitString(bitString);
                data.setFitness(bestFitness);

            }
            generation++;
            finalList.add(data);
            if (finalList.size() > 1000) { // Example threshold, adjust based on your use case
                finalList.clear(); // Or implement a more sophisticated memory management strategy
            }
        }
        stoppingMet = true;
    }

    @Override
    public void initialize() {
        bitString = searchSpace.init();
        bestFitness = (int) problem.computeFitness(bitString);
    }
}
