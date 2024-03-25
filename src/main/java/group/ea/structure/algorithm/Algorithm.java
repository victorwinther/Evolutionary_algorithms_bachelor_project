package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public abstract class Algorithm {
    Problem problem;
    SearchSpace searchSpace;
    protected final mainController _mainController;
    protected boolean stoppingMet = false;
    protected ArrayList<String> solutionList;

    protected List<Pair<Integer, Integer>> graphList;
    // create arraylist with string and integers


    protected int bestFitness;

    public Algorithm(SearchSpace searchSpace, Problem problem, mainController controller) {
        this.searchSpace = searchSpace;
        this.problem = problem;
        _mainController = controller;
        this.initialize();
    }

    public abstract void performSingleUpdate(int generation);

    public abstract void initialize();

    public void iterate(int generations) {
        for (int i = 0; i < generations; i++) {
            performSingleUpdate(i);
            if (stoppingMet) {
                break;
            }
        }
        stoppingMet = true;

    }

    public boolean stoppingCriteriaMet() {
        return stoppingMet;
    }

    public void runAlgorithm() {
        while (!this.stoppingCriteriaMet()) {
            this.iterate(10000);
        }
        System.out.println("Best fitness found: " + bestFitness);
    }

    /*
        public void updateGraphics(){
            for(String solution : solutionList ) {
                Platform.runLater(() -> {
                    _mainController.solutionArea.appendText(solution);
                });
            }
            _mainController.stopAlgorithm();
        }
    */
    int i = 0;

    public void updateGraphics() {
        if (i == 0) {
            Platform.runLater(() -> {
                _mainController.solutionArea.appendText(solutionList.get(0));
            });
            i++;
        }
        if (i < solutionList.size()) {
            final int currentIndex = i;
            Platform.runLater(() -> {
                _mainController.solutionArea.appendText(solutionList.get(currentIndex));
            });
            i++;
        }
        if (i == solutionList.size() && y == graphList.size()) {
            _mainController.stopGraphics();
        }
    }
    int y =0;

    public void graphGraphics() {
        if (y == 0) {
            Platform.runLater(() -> {
                _mainController.series.getData().add(new XYChart.Data<>(graphList.get(0).getKey(), graphList.get(0).getValue()));
            });
            y++;
        }
        if (y < graphList.size()) {
            final int currentIndex = y;
            Platform.runLater(() -> {
                _mainController.series.getData().add(new XYChart.Data<>(graphList.get(currentIndex).getKey(), graphList.get(currentIndex).getValue()));
            });
            y++;
        }
        if (i == solutionList.size() && y == graphList.size()) {
            _mainController.stopGraphics();
        }
    }
}


