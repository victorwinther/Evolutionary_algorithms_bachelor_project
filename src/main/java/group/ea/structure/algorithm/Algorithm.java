package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Algorithm {
    Problem problem;

    Solution _sl;
    SearchSpace searchSpace;
    protected final mainController _mainController;
    protected boolean stoppingMet = false;
    protected ArrayList<String> solutionList;

    protected List<Pair<Integer, Integer>> graphList;

    public List<Data> finalList = new ArrayList<>();

    protected int bestFitness;
    protected String bitString;

    int bitLength;
    protected int generation;
    private boolean hyperDone = true;
    private List<StoppingCriterion> stoppingCriteria = new ArrayList<>();

    public Algorithm(SearchSpace searchSpace, Problem problem, mainController controller) {
        this.searchSpace = searchSpace;
        bitLength = searchSpace.length;
        //sl = (Solution) problem;
        this.problem = problem;
        _mainController = controller;
        this.initialize();
    }
    public void addStoppingCriterion(StoppingCriterion criterion) {
        stoppingCriteria.add(criterion);
    }

    protected boolean checkStoppingCriteria() {
        for (StoppingCriterion criterion : stoppingCriteria) {
            if (criterion.isMet(this)) {
                return true;
            }
        }
        return false;
    }

    public abstract void performSingleUpdate(int generation);

    public abstract void initialize();

    public void runAlgorithm() {
        while (!checkStoppingCriteria()) {
            performSingleUpdate(generation);
            generation++;
        }
        stoppingMet = true;
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
    public int i = 0;
    public void sliderController() {
        if(i != finalList.size()) {
            Data data = finalList.get(i);
            if(data.getImproved()) {
                runGraphics(i);
                i++;
            } else {
                while (!data.getImproved() && i < finalList.size()-1) {
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
        _mainController.generationSlider.setBlockIncrement(10);
        _mainController.generationSlider.setMajorTickUnit(50);
        _mainController.generationSlider.setSnapToTicks(true);
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

    public void clearAndContinue(int i, int newI) {

    }
    public int getBitStringLength() {
        return bitLength;
    }

    public int getFitness() {
        return bestFitness;
    }

    public int getGeneration() {
        return generation;
    }
}
