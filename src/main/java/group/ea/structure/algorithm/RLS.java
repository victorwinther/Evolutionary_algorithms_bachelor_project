package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;

import java.util.ArrayList;

public class RLS extends Algorithm{
    String bitString;



    public RLS(SearchSpace searchSpace, Problem problem, mainController mainController) {
        super(searchSpace, problem, mainController);
        bestFitness = (int) problem.computeFitness(bitString);

    }
    @Override
    public void initialize() {
        bitString = searchSpace.init();
        System.out.println("bitstring is" + bitString + "\n");
        solutionList = new ArrayList<>();
        _mainController.solutionArea.appendText( ("Initial Solution: " + bitString + " with fitness: " + bestFitness + "\n"));

    }

    @Override
    public void performSingleUpdate(int generation) {
        String offspring = mutate(bitString);
        int offspringFitness = (int) problem.computeFitness(offspring);


        if (offspringFitness > bestFitness) {
            bitString = offspring;
            bestFitness = offspringFitness;
            _mainController.solutionArea.appendText( "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + bestFitness + "\n");

        }
        if (bestFitness == bitString.length()) {
            _mainController.solutionArea.appendText("Perfect solution found in generation " + generation + "\n");
            stoppingMet = true;
            _mainController.stopAlgorithm();
        }
    }

    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }


}
