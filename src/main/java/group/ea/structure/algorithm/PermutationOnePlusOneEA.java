package group.ea.structure.algorithm;

import group.ea.structure.TSP.City;
import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

public class PermutationOnePlusOneEA extends Algorithm {
    double chance = 0.5;
    public PermutationOnePlusOneEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();
    }

    @Override
    public void initialize() {
        graphList = new ArrayList<>();
        graphList.add(new Pair<>(0, bestFitness));
        solutionList = new ArrayList<>();
        //_mainController.solutionArea.appendText( ("Initial Solution: with fitness: " + this.bestFitness + " tempature is " + this.initTemp + "\n"));
        Data data = new Data("bitString", 0, bestFitness, false, Optional.of(currentTemp));
        finalList.add(data);
    }
    @Override
    public void performSingleUpdate(int generation) {
        // Save the current solution
        // randomly at uniform
        if(Math.random() < chance){
            _sl.twoOptMutate();
        } else {
            _sl.ls3Opt();
        }
        int offspringFitness = _sl.computeFitness();
        if(offspringFitness < bestFitness){
            bestFitness = offspringFitness;
            Data data = new Data("bitString", generation, bestFitness, false,Optional.empty() );
            finalList.add(data);
        } else {
            _sl.revert();
        }

    }
}
