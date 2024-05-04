package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class SA extends Algorithm {


    double initTemp = 2;
    double tempReduction = 0.99;
    double currentTemp;


    public SA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        bestFitness = (int) problem.computeFitness(bitString);
        currentTemp = initTemp;

    }

    @Override
    public void initialize() {
        bitString = searchSpace.init();
    }

    @Override
    public void performSingleUpdate(int generation) {
        String offspring = mutate(bitString);
        int offspringFitness = (int) problem.computeFitness(offspring);
        Data data = new Data(bitString, generation, bestFitness, false, Optional.empty());

        if (offspringFitness > bestFitness) {
            bitString = offspring;
            bestFitness = offspringFitness;
            data.setYesNo(true);
        } else if (offspringFitness == bestFitness) {
            bitString = offspring;
        } else {
            double SARate = Math.random();
            double SAEnergy = Math.exp((offspringFitness - bestFitness) / currentTemp);

            if (SAEnergy > SARate) {
                bitString = offspring;
                bestFitness = offspringFitness;
                data.setTemp(Optional.of(currentTemp));
            }
        }
        finalList.add(data);
        currentTemp *= tempReduction;
    }

    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }

}
