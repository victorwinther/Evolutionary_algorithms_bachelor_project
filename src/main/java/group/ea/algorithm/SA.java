package group.ea.algorithm;

import group.ea.helperClasses.Data;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.Optional;

public class SA extends Algorithm {


    double initTemp;
    double tempReduction;
    double currentTemp;


    public SA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        bestFitness = (int) problem.computeFitness(bitString);
        initTemp = 1;
        tempReduction = 0.99;
        currentTemp = initTemp;
    }

    @Override
    public void initialize() {
        bitString = searchSpace.init();
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


        functionEvaluations++;


        if (offspringFitness > bestFitness) {
            bitString = offspring;
            bestFitness = offspringFitness;
            Data data = new Data(bitString, generation, bestFitness, true, Optional.of(currentTemp),false);
            data.setTimeElapsed(timer.getCurrentTimer());
            data.setFunctionEvaluations(functionEvaluations);
            listener.receiveBitstringUpdate(data);
        } else if (offspringFitness == bestFitness) {
            bitString = offspring;
        } else {
            double SARate = Math.random();
            double SAEnergy = Math.exp((offspringFitness - bestFitness) / currentTemp);

            if (SAEnergy > SARate) {
                bitString = offspring;
                bestFitness = offspringFitness;
                //data.setTemp(Optional.of(currentTemp));
            }
        }
        currentTemp *= tempReduction;
        System.out.println(currentTemp);
    }

    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }
    @Override
    public void setInitTemp(double temp){
        initTemp = temp;
    }
    @Override
    public void setTempReduction(double temp){
        tempReduction = temp;
    }

}
