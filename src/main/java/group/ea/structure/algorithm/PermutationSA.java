package group.ea.structure.algorithm;



import group.ea.controllers.mainController;
import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;
import javafx.util.Pair;

import java.util.ArrayList;


//TEMP CLASS WILL BE FIXED ASAP

//TODO
//MERGE THIS AND NORMAL SA
public class PermutationSA extends  Algorithm {


    Solution _sl;
    double initTemp = 100000;
    double tempReduction = 0.99;
    double currentTemp;



    public PermutationSA(SearchSpace searchSpace, Problem problem, mainController mainController) {
        super(searchSpace, problem, mainController);
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
        _mainController.solutionArea.appendText( ("Initial Solution: with fitness: " + this.bestFitness + " tempature is " + this.initTemp + "\n"));

    }

    @Override
    public void performSingleUpdate(int generation) {

        if(currentTemp < 1){
            System.out.println("too cool");
            stoppingMet = true;
            return;
        }
        _sl.twoOptMutate();
        int offspringFitness = _sl.computeFitness();

        if (offspringFitness < bestFitness) {
            bestFitness = offspringFitness;
            String solutionText = "Generation " + generation + ": New solution found:  with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
            solutionList.add(solutionText);
        }
        else if (Math.exp((bestFitness - offspringFitness) / currentTemp) < Math.random()) {
                _sl.revert();
                String solutionText = "Generation " + generation + ": New SA found:  with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
                solutionList.add(solutionText);

        }
        currentTemp *= tempReduction;
        graphList.add(new Pair<>(generation + 1, bestFitness));
    }


}