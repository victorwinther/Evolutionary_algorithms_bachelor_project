package group.ea.structure.algorithm;



import group.ea.structure.TSP.Solution;
import group.ea.structure.helperClasses.Data;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;


//TEMP CLASS WILL BE FIXED ASAP

//TODO
//MERGE THIS AND NORMAL SA
public class PermutationSA extends  Algorithm {


    double initTemp = 10000;
    double tempReduction = 0.9995;


    public PermutationSA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();
        currentTemp = initTemp;
        System.out.print("Construct done");
    }

    @Override
    public void initialize() {

    }

    @Override
    public void performSingleUpdate(int generation) {
        if(generation == 0){
            Data firstData = new Data("bitString", generation, bestFitness, true, Optional.of(currentTemp),false);
            listener.receiveBitstringUpdate(firstData);
        }
        Data data = new Data("bitString", generation, bestFitness, false, Optional.of(currentTemp),false);
        if (currentTemp < 1) {
            System.out.println("too cool");
            data.setStop(true);
            stoppingMet = true;
            return;
        }
        _sl.twoOptMutate();
        int offspringFitness = _sl.computeFitness();

        System.out.println(offspringFitness + " " + bestFitness);


        if (offspringFitness < bestFitness) {

            data.setYesNo(true);
            bestFitness = offspringFitness;
            data.setFitness(bestFitness);

        } else if (Math.exp((offspringFitness - bestFitness) / currentTemp) > Math.random()) {
            _sl.revert();
            data.setBitString("Revert");
            data.setFitness(bestFitness);

        }
        currentTemp *= tempReduction;
        functionEvaluations++;
        data.setTimeElapsed(timer.getCurrentTimer());
        data.setFunctionEvaluations(functionEvaluations);
        listener.receiveBitstringUpdate(data);


    }
}