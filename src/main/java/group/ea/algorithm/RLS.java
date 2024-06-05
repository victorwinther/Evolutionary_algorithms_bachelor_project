package group.ea.algorithm;

import group.ea.helperClasses.Data;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.Optional;

public class RLS extends Algorithm {
    public RLS(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
    }
    @Override
    public void initialize() {
        bitString = searchSpace.init();
        bestFitness = (int) problem.computeFitness(bitString);
    }
    @Override
    public void performSingleUpdate(int generation) {
        if(graphicsOn){
        if(generation == 0){
            Data firstData = new Data(bitString, 0, bestFitness, true, Optional.empty(),false);
            listener.receiveBitstringUpdate(firstData);
        }
        }
        String offspring = mutate(bitString);
        int offspringFitness = (int) problem.computeFitness(offspring);
        Data data = new Data(bitString, generation, bestFitness, false, Optional.empty(),false);
        if (offspringFitness > bestFitness) {
            bitString = offspring;
            bestFitness = offspringFitness;
            if(graphicsOn) {
                data.setBitString(bitString);
                data.setFitness(bestFitness);
                data.setYesNo(true);
            }
        }
        if(graphicsOn){
            if (checkStoppingCriteria()) {
                data.setStop(true);
            }
        }
        functionEvaluations++;
        if(graphicsOn){
            data.setTimeElapsed(timer.getCurrentTimer());
            data.setFunctionEvaluations(functionEvaluations);
            listener.receiveBitstringUpdate(data);
        }
    }
    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }
}
