package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.ArrayList;

public class onePlusOneEA extends Algorithm{
    String bitString;
    public onePlusOneEA(SearchSpace searchSpace, Problem problem, mainController controller) {
        super(searchSpace, problem, controller);
    }

    @Override
    public void performSingleUpdate(int generation) {
        int n = bitString.length();
        int t = 1;
        int fitness = 0;
        // random select a bit within bitString to flip
        int x = (int) (Math.random() * bitString.length());
        while(problem.computeFitness(bitString) < bitString.length()){
            String y = bitString;
            for(int i = 0; i < n; i++){
                if(Math.random() < 1.0/n){
                    y = y.substring(0, i) + (y.charAt(i) == '0' ? '1' : '0') + y.substring(i + 1);
                    if(problem.computeFitness(y) >= problem.computeFitness(bitString)){
                        bitString = y;
                        fitness = (int) problem.computeFitness(bitString);
                        String solutionText = "Generation " + t + ": New solution found: " + bitString + " with fitness: " + fitness + "\n";
                        solutionList.add(solutionText);
                        graphList.add(new Pair<>(t, fitness));
                        t++;
                    }
                }
            }
        }
        solutionList.add( ("Perfect solution found in generation " + (t-1) + "\n"));
        stoppingMet = true;
    }

    @Override
    public void initialize() {
        bitString = searchSpace.init();
        bestFitness = (int) problem.computeFitness(bitString);
        solutionList = new ArrayList<>();
        graphList = new ArrayList<>();
        graphList.add(new Pair<>(0, bestFitness));
        solutionList.add(("Initial Solution: " + bitString + " with fitness: " + bestFitness + "\n"));
    }
}
