package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;

import java.util.ArrayList;

public class RLS extends Algorithm{
    String bitString;
    private final mainController thismainController;
    private boolean stoppingMet;

    int generation;

    ArrayList<String> solutionList = new ArrayList<>();


    public RLS(SearchSpace searchSpace, Problem problem, mainController mainController) {
        super(searchSpace, problem);
        thismainController = mainController;
    }
    @Override
    public void initialize() {
        bitString = searchSpace.init();
        System.out.println("bitstring is" + bitString);
    }

    @Override
    public void iterate() {
        int bestFitness = (int) problem.computeFitness(bitString);

        String finalParent = bitString;
        int finalBestFitness = bestFitness;
       solutionList.add( ("Initial Solution: " + finalParent + " with fitness: " + finalBestFitness + "\n"));
        thismainController.solutionArea.appendText("Initial Solution: " + finalParent + " with fitness: " + finalBestFitness + "\n");
        int maxGenerations = 50000;
        for (int generation = 1; generation <= maxGenerations; generation++) {
            if(!thismainController.isRunning) break;
            String offspring = mutate(bitString);
            int offspringFitness = (int) problem.computeFitness(offspring);

            if (offspringFitness > bestFitness) {
                bitString = offspring;
                bestFitness = offspringFitness;
                String solutionText = "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + bestFitness + "\n";
                solutionList.add(solutionText);
            }
            if (bestFitness == bitString.length()) {
                int finalGeneration = generation;
                solutionList.add( ("Perfect solution found in generation " + finalGeneration + "\n"));
                break;
            }
        }
        stoppingMet = true;
    }


    @Override
    public void performSingleUpdate() {
        int bestFitness = (int) problem.computeFitness(bitString);
        String finalParent = bitString;
        int finalBestFitness = bestFitness;
        String offspring = mutate(bitString);
        int offspringFitness = (int) problem.computeFitness(offspring);

        if (offspringFitness > bestFitness) {
            bitString = offspring;
            bestFitness = offspringFitness;
            thismainController.solutionArea.appendText( "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + bestFitness + "\n");

        }
        if (bestFitness == bitString.length()) {
            int finalGeneration = generation;
            thismainController.solutionArea.appendText("Perfect solution found in generation " + finalGeneration + "\n");
            stoppingMet = true;
            thismainController.stopAlgorithm();
        }
        generation++;
    }
    int i = 0;
    @Override
    public void updateGraphics() {
        if(i < solutionList.size()-1) {
            Platform.runLater(() -> {
                thismainController.solutionArea.appendText(solutionList.get(i));
            });
            i++;
        } else {
            thismainController.stopAlgorithm();
        }
    }

    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }

    @Override
    public boolean stoppingCriteriaMet() {
        return stoppingMet;
    }

    @Override
    public void runAlgorithm() {
        this.initialize();
        while (!this.stoppingCriteriaMet()) {
            this.iterate();
        }
    }
}
