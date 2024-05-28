package group.ea.structure.algorithm;

import group.ea.structure.TSP.Solution;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Optional;

public class PermutationOnePlusOneEA extends Algorithm {
    double chance = 1;
    int noImprovementCounter = 0; // Counter to track iterations without improvement
    final int RESTART_THRESHOLD = 1000000; // Threshold for restarting the algorithm



    public PermutationOnePlusOneEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();

    }

    @Override
    public void initialize() {
        graphList = new ArrayList<>();
        graphList.add(new Pair<>(0, bestFitness));
        solutionList = new ArrayList<>();
        //_mainController.solutionArea.appendText( ("Initial Solution: with fitness: " + this.bestFitness + " tempature is " + this.initTemp + "\n"));
        Data data = new Data("bitString", 0, bestFitness, false, Optional.of(currentTemp));
        finalList.add(data);
    }
    @Override
    public void performSingleUpdate(int generation) {
        if(generation == 0){
            listener.firstSolution(_sl);
        }
        // Save the current solution
        // randomly at uniform
        boolean threeOpt = false;
        double tempChance = Math.random();
        _sl.clearData();

        if (tempChance < chance) {
            //_sl.twoOptMutate2();
            //_sl.ls3Opt();
            _sl.twoOptMutate();
            threeOpt = false;

        } else {
            //_sl.ls3Opt();
            _sl.random3Opt();
            threeOpt = true;
        }
        int offspringFitness = _sl.computeFitness();

        if (offspringFitness < bestFitness) {
            bestFitness = offspringFitness;
            TSPDATA tspdata = new TSPDATA(_sl,_sl.getSolution(),generation,offspringFitness,_sl.getImprovement,_sl.A1,_sl.A2,_sl.A3,_sl.A4,Optional.ofNullable(_sl.A5),Optional.ofNullable(_sl.A6),Optional.ofNullable(_sl.optCase), threeOpt);
            listener.receiveUpdate(tspdata);



            Data data = new Data("bitString", generation, bestFitness, false, Optional.empty());
            finalList.add(data);
            // Adaptively adjust mutation probability
            /*
            if (Math.random() < chance) {
                chance = Math.min(chance + 0.1, 1.0); // Increase chance of 2-opt
            } else {
                chance = Math.max(chance - 0.1, 0.0); // Decrease chance of 2-opt
            }

             */
        } else {
            //noImprovementCounter++;
            _sl.revert();
        }



        if (noImprovementCounter > RESTART_THRESHOLD) {
            //System.out.println("Restarting the algorithm... in generation"+ generation + " with fitness: " + bestFitness );
            _sl.restart(); // Reinitialize the solution
            bestFitness = _sl.computeFitness();
            noImprovementCounter = 0; // Reset counter
        }

        }

    }

