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
        System.out.print("initial is ");
        graphList = new ArrayList<>();
        graphList.add(new Pair<>(0, bestFitness));
        System.out.println();
        solutionList = new ArrayList<>();
        //_mainController.solutionArea.appendText( ("Initial Solution: with fitness: " + this.bestFitness + " tempature is " + this.initTemp + "\n"));
        Data data = new Data("bitString", 0, bestFitness, false, Optional.of(currentTemp));
        finalList.add(data);
    }

    @Override
    public void performSingleUpdate(int generation) {
        Data data = new Data("bitString", generation, bestFitness, false, Optional.of(currentTemp));
        if (currentTemp < 1) {
            System.out.println("too cool");
            stoppingMet = true;
            return;
        }
        _sl.twoOptMutate();
        int offspringFitness = _sl.computeFitness();

        System.out.println(offspringFitness + " " + bestFitness);

        if (offspringFitness < bestFitness) {
            //listener.tspGraphics(_sl);
            System.out.println("Better in generation " + generation);
            data.setYesNo(true);
            bestFitness = offspringFitness;
            data.setFitness(bestFitness);
            String solutionText = "Generation " + generation + ": New solution found:  with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
            solutionList.add(solutionText);
        } else if (Math.exp((offspringFitness - bestFitness) / currentTemp) > Math.random()) {
            System.out.println("Revert in generation " + generation);
            _sl.revert();
            data.setBitString("Revert");
            //data.setYesNo(true);
            data.setFitness(bestFitness);
            String solutionText = "Generation " + generation + ": New SA found:  with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
            System.out.println("Test");
            solutionList.add(solutionText);

        }
        currentTemp *= tempReduction;
        finalList.add(data);
        graphList.add(new Pair<>(generation + 1, bestFitness));

    }
}
