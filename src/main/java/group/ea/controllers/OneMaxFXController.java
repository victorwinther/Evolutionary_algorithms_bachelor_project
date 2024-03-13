package group.ea.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class OneMaxFXController {

    @FXML
    private Button startButton;
    @FXML
    private TextArea solutionArea;

    private int stringLength = 100; // Length of the binary string

    // Button action to start the EA
    @FXML
    private void startEvolution() {
        solutionArea.clear(); // Clear previous solutions
        new Thread(this::runEvolution).start(); // Run EA in a separate thread
    }

    // EA logic adapted from OnePlusOneEAOneMax
    private void runEvolution() {
        String parent = initializeIndividual(stringLength);
        int bestFitness = fitness(parent);

        String finalParent = parent;
        int finalBestFitness = bestFitness;
        Platform.runLater(() -> solutionArea.appendText("Initial Solution: " + finalParent + " with fitness: " + finalBestFitness + "\n"));

        int maxGenerations = 100;
        for (int generation = 1; generation <= maxGenerations; generation++) {
            String offspring = mutate(parent);
            int offspringFitness = fitness(offspring);

            if (offspringFitness > bestFitness) {
                parent = offspring;
                bestFitness = offspringFitness;
                String solutionText = "Generation " + generation + ": New solution found: " + parent + " with fitness: " + bestFitness + "\n";
                Platform.runLater(() -> solutionArea.appendText(solutionText));
            }

            if (bestFitness == stringLength) {
                int finalGeneration = generation;
                Platform.runLater(() -> solutionArea.appendText("Perfect solution found in generation " + finalGeneration + "\n"));
                break;
            }
        }
    }

    // Fitness function
    private int fitness(String individual) {
        int count = 0;
        for (int i = 0; i < individual.length(); i++) {
            if (individual.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

    // Initialize a random binary string
    private String initializeIndividual(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Math.random() > 0.5 ? '1' : '0');
        }
        return sb.toString();
    }

    // Mutation function: flips a random bit
    private String mutate(String parent) {
        int mutateIndex = (int) (Math.random() * parent.length());
        char[] chars = parent.toCharArray();
        chars[mutateIndex] = chars[mutateIndex] == '0' ? '1' : '0';
        return new String(chars);
    }
}
