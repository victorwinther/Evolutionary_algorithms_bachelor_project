package group.ea.structure.algorithm;



import group.ea.controllers.mainController;
import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;


//TEMP CLASS WILL BE FIXED ASAP

//TODO
//MERGE THIS AND NORMAL SA
public class PermutationSA extends  Algorithm {


    Solution _sl;
    double initTemp = 10000;
    double tempReduction = 0.9995;
    double currentTemp;


    public PermutationSA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();
        currentTemp = initTemp;
        System.out.print("Construct done");
    }

    @Override
    public void initialize() {
        System.out.print("initial is ");
        graphList = new ArrayList<>();
        graphList.add(new Pair<>(0, bestFitness));
        System.out.println();
        solutionList = new ArrayList<>();
        //_mainController.solutionArea.appendText( ("Initial Solution: with fitness: " + this.bestFitness + " tempature is " + this.initTemp + "\n"));
        Data data = new Data("bitString", 0, bestFitness, false, Optional.of(currentTemp));
        finalList.add(data);
    }

    @Override
    public void performSingleUpdate(int generation) {
        Data data = new Data("bitString", generation, bestFitness, false, Optional.of(currentTemp));
        if (currentTemp < 1) {
            System.out.println("too cool");
            stoppingMet = true;
            return;
        }
        _sl.twoOptMutate();
        int offspringFitness = _sl.computeFitness();


        System.out.println(offspringFitness + " " + bestFitness);


        if (offspringFitness < bestFitness) {
            System.out.println("Better in generation " + generation);
            data.setYesNo(true);
            bestFitness = offspringFitness;
            data.setFitness(bestFitness);
            String solutionText = "Generation " + generation + ": New solution found:  with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
            solutionList.add(solutionText);
        } else if (Math.exp((offspringFitness - bestFitness) / currentTemp) > Math.random()) {
            System.out.println("Revert in generation " + generation);
            _sl.revert();
            data.setBitString("Revert");
            //data.setYesNo(true);
            data.setFitness(bestFitness);
            String solutionText = "Generation " + generation + ": New SA found:  with fitness: " + bestFitness + " tempature is " + currentTemp + "\n";
            System.out.println("Test");
            solutionList.add(solutionText);

        }
        currentTemp *= tempReduction;
        finalList.add(data);
        graphList.add(new Pair<>(generation + 1, bestFitness));
    }
}
/*
    public void sliderController(){

            int maxY = 400; // Replace with the actual maximum Y value of your canvas
            if (i == 0 && _mainController.showTSPgraph.isSelected()) {
                int prevX = 0;
                int prevY = 0;
                for (int j = 0; j < _sl.getListLength();j++) {
                    System.out.println("i er " + j + " og listlength er " + _sl.getListLength());
                    int x = _sl.getXSolution(j);
                    int y = maxY - _sl.getYSolution(j); // Subtract the y-coordinate from maxY to mirror it
                    Circle circle = new Circle(x/4, y/4, 3);
                    circle.setFill(Color.RED);
                    _mainController.tspVisualization.getChildren().add(circle);
                    if (j > 0) { // Draw line from the previous point to the current point
                        Line line = new Line(prevX / 4.0, prevY / 4.0, x / 4.0, y / 4.0);
                        line.setStroke(Color.BLUE);
                        _mainController.tspVisualization.getChildren().add(line);
                    }
                    prevX = x;
                    prevY = y;
                    System.out.println(x + " x og er y" + y);

            }
        }
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

}

 */