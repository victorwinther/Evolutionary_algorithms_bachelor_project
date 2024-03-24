package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

public class onePlusOneEA extends Algorithm{
    String bitString;
    public onePlusOneEA(SearchSpace searchSpace, Problem problem, mainController controller) {
        super(searchSpace, problem, controller);
    }

    @Override
    public void performSingleUpdate(int generation) {
        int n = bitString.length();
        int t = 0;
        int fitness = 0;
        while(problem.computeFitness(bitString) < bitString.length()){
            String y = bitString;
            Data data = new Data(bitString, t, bestFitness, false, Optional.empty());
            for(int i = 0; i < n; i++){
                if(Math.random() < 1.0/n) {
                    y = y.substring(0, i) + (y.charAt(i) == '0' ? '1' : '0') + y.substring(i + 1);
                }
            }
            if(problem.computeFitness(y) >= problem.computeFitness(bitString)){
                if(problem.computeFitness(y) > problem.computeFitness(bitString)) {
                    data.setYesNo(true);
                }
                bitString = y;
                fitness = (int) problem.computeFitness(bitString);
                data.setBitString(bitString);
                data.setFitness(fitness);

            }
            t++;
            finalList.add(data);
        }
        stoppingMet = true;
    }

    @Override
    public void initialize() {
        bitString = searchSpace.init();
        bestFitness = (int) problem.computeFitness(bitString);
        Data data = new Data(bitString, 0, bestFitness, true, Optional.empty());
        finalList.add(data);

    }
}
