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
            String y = bitString;
            Data data = new Data(bitString, generation, bestFitness, false, Optional.empty());
            for (int i = 0; i < n; i++) {
                if (Math.random() <= 1.0 / n) {
                    y = y.substring(0, i) + (y.charAt(i) == '0' ? '1' : '0') + y.substring(i + 1);
                }
            }
            if (problem.computeFitness(y) >= problem.computeFitness(bitString)) {
                if (problem.computeFitness(y) > problem.computeFitness(bitString)) {
                    data.setYesNo(true);
                }
                bitString = y;
                bestFitness = (int) problem.computeFitness(bitString);
                data.setBitString(bitString);
                data.setFitness(bestFitness);

            }
            generation++;
            finalList.add(data);
        }
        stoppingMet = true;
    }

    @Override
    public void initialize() {
        bitString = searchSpace.init();
        bestFitness = (int) problem.computeFitness(bitString);
    }
}
