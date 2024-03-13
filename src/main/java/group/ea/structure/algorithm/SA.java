package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;

import java.util.ArrayList;

public class SA extends  Algorithm {


    String bitString;
    double initTemp = 2;
    double tempReduction = 0.99;
    double currentTemp;



    public SA(SearchSpace searchSpace, Problem problem, mainController mainController) {
        super(searchSpace, problem, mainController);
        bestFitness = (int) problem.computeFitness(bitString);
        currentTemp = initTemp;

    }
    @Override
    public void initialize() {
        bitString = searchSpace.init();
        System.out.println("bitstring is " + bitString + "\n");
        solutionList = new ArrayList<>();
        _mainController.solutionArea.appendText( ("Initial Solution: " + bitString + " with fitness: " + this.bestFitness + " tempature is " + this.initTemp + "\n"));

    }

    @Override
    public void performSingleUpdate(int generation) {
        String offspring = mutate(bitString);
        int offspringFitness = (int) problem.computeFitness(offspring);


        if (offspringFitness > bestFitness) {
            bitString = offspring;
            bestFitness = offspringFitness;
            //_mainController.solutionArea.appendText( "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + bestFitness + " tempature is " + currentTemp + "\n");
            // add to solution list
            String solutionText = "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
            solutionList.add(solutionText);

            if (bestFitness == bitString.length()) {
                //_mainController.solutionArea.appendText("Perfect solution found in generation " + generation + "\n");
                solutionList.add( ("Perfect solution found in generation " + generation + "\n"));
                stoppingMet = true;
                //_mainController.stopAlgorithm();
            }
        }
        else if ( offspringFitness == bestFitness) {
            bitString = offspring;
        }
        else {
            double SARate = Math.random();
            double SAEnergy = Math.exp((offspringFitness - bestFitness) / currentTemp);

            if(SAEnergy > SARate){
                bitString = offspring;
                bestFitness = offspringFitness;
                String solutionText = "Generation " + generation + ": New SA found: " + bitString + " with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
                solutionList.add(solutionText);

            }
        }

        currentTemp *= tempReduction;
    }

    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }

}
