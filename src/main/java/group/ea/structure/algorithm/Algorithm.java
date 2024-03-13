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

    protected List<Pair<Integer,Integer>> graphList;
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
    public void iterate(int generations){
        for(int i = 0; i < generations; i++){
            performSingleUpdate(i);
            if(stoppingMet){
                break;
            }
        }
        stoppingMet = true;

    }
    public boolean stoppingCriteriaMet(){
        return stoppingMet;
    }
    public void runAlgorithm(){
        while (!this.stoppingCriteriaMet()) {
            this.iterate(1000);
        }
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
        if(i < solutionList.size()-1) {
            Platform.runLater(() -> {
                _mainController.solutionArea.appendText(solutionList.get(i));
            });
            i++;
        } else {
            _mainController.stopGraphics();
        }
    }

    int y = 0;
    public void graphGraphics(){
        if(y < graphList.size()-1) {
            Platform.runLater(() -> {
                //_mainController.makeGraphics(graphList.get(i).getKey(), graphList.get(i).getValue());
                _mainController.series.getData().add(new XYChart.Data<>(graphList.get(y).getKey(), graphList.get(y).getValue()));
            });
            y++;
        } else {
            _mainController.stopGraphics();
        }

    }
}


