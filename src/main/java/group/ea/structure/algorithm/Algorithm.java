package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Circle;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class Algorithm {
    Problem problem;
    SearchSpace searchSpace;
    protected final mainController _mainController;
    protected boolean stoppingMet = false;
    protected ArrayList<String> solutionList;

    protected List<Pair<Integer, Integer>> graphList;

   // public ArrayList<HashMap<String,HashMap<String,String>>> finalList = new ArrayList<>();
    public List<Data> finalList = new ArrayList<>();
    // create arraylist with string and integers


    protected int bestFitness;
    private boolean hyperDone = true;

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
    public int i = 0;
    private final Object lock = new Object();
    private volatile int oldI = -1; // Initialize oldI to -1 so it's different from the initial i value.
    public void sliderController() {
/*
        synchronized (lock) {
            if (i == oldI) {
                System.out.println("not changed");
                // If i has not changed since the last run, skip this execution.
                return;
            }
            Platform.runLater(() -> {
                synchronized (lock) {
                    System.out.println("old i "+oldI + " i "+i);
                    runGraphics(i);
                    oldI = i; // Update oldI only after runGraphics is actually called.
                    i++;
                }
            });
        }

 */
        if(i != finalList.size()) {
            Data data = finalList.get(i);
        if(data.getImproved()) {
            runGraphics(i);
            i++;
        } else {
            while (!data.getImproved() && i < finalList.size()) {
                i++;
                data = finalList.get(i);
            }
        }
    }
    }

    public void runGraphics(int i) {
        int generation = 0;
        String bitString = null;
        Data data = finalList.get(i);

        bitString = data.getBitString();
        generation = data.getGeneration();
        int fitness = data.getFitness();
        Optional<Double> temp = data.getTemp();
        double max = finalList.size()-1;
        _mainController.generationSlider.setMax(max);
        _mainController.generationSlider.setBlockIncrement((double) finalList.size() /10/4);
        _mainController.generationSlider.setMajorTickUnit((double) finalList.size() /10);
        _mainController.generationSlider.adjustValue(i);

        if(data.getImproved()) {
            if (_mainController.isHypercubeSelected()) {
                Circle circle = _mainController.booleanHypercubeVisualization.getDisplayCoordinates(bitString, false);
                if (circle != null) {
                    if (lastCircle != null) {
                        _mainController.booleanHypercubeVisualization.hypercubePane.getChildren().remove(lastCircle);
                    }
                    if (i + 1 == finalList.size()) {
                        Circle perfectCircle = _mainController.booleanHypercubeVisualization.getDisplayCoordinates(bitString, true);
                        _mainController.booleanHypercubeVisualization.hypercubePane.getChildren().add(perfectCircle);
                    } else {
                        lastCircle = circle;
                        _mainController.booleanHypercubeVisualization.hypercubePane.getChildren().add(circle);
                    }
                }
            }

            if (_mainController.isTextSelected()) {
                if (i == 0) {
                    String initialText;
                    if (temp.isPresent()) {
                        initialText = ("Initial Solution: " + bitString + " with fitness: " + fitness + " temperature is " + temp + "\n");
                    } else {
                        initialText = ("Initial Solution: " + bitString + " with fitness: " + fitness + "\n");
                    }
                    _mainController.solutionArea.appendText(initialText);
                } else {


                    String solutionText;
                    if (temp.isPresent()) {
                        solutionText = "Generation " + generation + ": New SA found: " + bitString + " with fitness: " + fitness + " temperature is " + temp + "\n";
                    } else {
                        solutionText = "Generation " + generation + ": New solution found: " + bitString + " with fitness: " + fitness + "\n";
                    }
                    _mainController.solutionArea.appendText(solutionText);
                }
            }
            if (_mainController.isGraphSelected()) {
                _mainController.series.getData().add(new XYChart.Data<>(generation, fitness));
            }

            if (i == finalList.size() - 1) {
                String finalText = "Perfect solution found in generation " + generation + "\n";
                _mainController.solutionArea.appendText(finalText);
                _mainController.stopGraphics();
                }
            }
        }


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
        if (i == solutionList.size() && y == graphList.size() && hyperDone) {
            _mainController.stopGraphics();

        }
    }

    int y = 0;

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
        if (i == solutionList.size() && y == graphList.size() && hyperDone) {
            _mainController.stopGraphics();
        }
    }

    Circle lastCircle = null;

    public void hyperCubeGraphics() {
        Circle circle = _mainController.booleanHypercubeVisualization.getNextCircle();
        if (circle != null) {
            if (lastCircle != null) {
                _mainController.booleanHypercubeVisualization.hypercubePane.getChildren().remove(lastCircle);
            }
            lastCircle = circle;
            _mainController.booleanHypercubeVisualization.hypercubePane.getChildren().add(circle);
            if (_mainController.booleanHypercubeVisualization.isDone) {
                hyperDone = true;
                if (i == solutionList.size() && y == graphList.size()) {
                    _mainController.stopGraphics();
                }
            } else {
                hyperDone = false;
            }
        }
    }
}
