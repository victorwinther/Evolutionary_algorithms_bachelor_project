package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;
import javafx.util.Pair;

import java.util.ArrayList;

public class RLS extends Algorithm{
    String bitString;

    public RLS(SearchSpace searchSpace, Problem problem, mainController mainController) {
        super(searchSpace, problem, mainController);


    }
    @Override
    public void initialize() {
        bitString = searchSpace.init();
        bestFitness = (int) problem.computeFitness(bitString);
        System.out.println("bitstring is" + bitString + "\n");
        solutionList = new ArrayList<>();
        graphList = new ArrayList<>();
        graphList.add(new Pair<>(0, bestFitness));
       // _mainController.solutionArea.appendText( ("Initial Solution: " + bitString + " with fitness: " + bestFitness + "\n"));
        solutionList.add(("Initial Solution: " + bitString + " with fitness: " + bestFitness + "\n"));
    }

    @Override
    public void performSingleUpdate(int generation) {
        String offspring = mutate(bitString);
        int offspringFitness = (int) problem.computeFitness(offspring);
        if (offspringFitness > bestFitness) {
            bitString = offspring;
            bestFitness = offspringFitness;
            String solutionText = "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + bestFitness + "\n";
            solutionList.add(solutionText);
            graphList.add(new Pair<>(generation, bestFitness));
            //_mainController.solutionArea.appendText( "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + bestFitness + "\n");

        }
        if (bestFitness == bitString.length()) {
            //_mainController.solutionArea.appendText("Perfect solution found in generation " + generation + "\n");
            solutionList.add( ("Perfect solution found in generation " + generation + "\n"));
            graphList.add(new Pair<>(generation, bestFitness));
            stoppingMet = true;
            //_mainController.stopAlgorithm();
        }
    }

    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }


}
