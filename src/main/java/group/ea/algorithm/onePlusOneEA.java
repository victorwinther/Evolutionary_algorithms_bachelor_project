package group.ea.algorithm;

import group.ea.helperClasses.Data;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.Optional;

public class onePlusOneEA extends Algorithm {

    public onePlusOneEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
    }

    @Override
    public void performSingleUpdate(int gen) {
        int n = bitString.length();
        generation = 0;
        Data firstData = new Data(bitString, generation, bestFitness, true, Optional.empty(), false);
        listener.receiveBitstringUpdate(firstData);
        while (!checkStoppingCriteria()) {
            StringBuilder y = new StringBuilder(bitString);
            for (int i = 0; i < n; i++) {
                if (Math.random() <= 1.0 / n) {
                    y.setCharAt(i, y.charAt(i) == '0' ? '1' : '0');
                }
            }
            String yString = y.toString();
            double yFitness = problem.computeFitness(yString);
            //double currentFitness = problem.computeFitness(bitString);
            functionEvaluations++;
            if (yFitness >= bestFitness) {
                if(yFitness > bestFitness) {
                    Data data = new Data(bitString, generation, bestFitness, true, Optional.empty(), false);
                    data.setFunctionEvaluations(functionEvaluations);
                    data.setTimeElapsed(timer.getCurrentTimer());
                    listener.receiveBitstringUpdate(data);
                }
                bitString = yString;
                bestFitness = (int) yFitness;
            }
            generation++;
        }
        generation--;
        stoppingMet = true;
    }

    @Override
    public void initialize() {
        bitString = searchSpace.init();
        bestFitness = (int) problem.computeFitness(bitString);
    }
}
