package group.ea.algorithm;

import group.ea.problem.TSP.City;
import group.ea.problem.TSP.Solution;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.ArrayList;
import java.util.Optional;

public class PermutationOnePlusOneEA extends Algorithm {
    double chance = 0.5;
    int noImprovementCounter = 0; // Counter to track iterations without improvement
    final int RESTART_THRESHOLD = 1000000; // Threshold for restarting the algorithm


    Solution _slClone;
    
    public PermutationOnePlusOneEA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();

    }

    @Override
    public void initialize() {

    }
    @Override
    public void performSingleUpdate(int generation) {
        if(generation == 0){
           // listener.firstSolution(_sl);
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

        } else {
            //_sl.ls3Opt();
            _sl.random3Opt();
            threeOpt = true;
        }
        int offspringFitness = _sl.computeFitness();
        functionEvaluations++;
        _slClone = new Solution(_sl.get_tsp());
        _slClone.deepCopy(_sl);
        TSPDATA tspdata = new TSPDATA(_slClone,new ArrayList<>(_slClone.getSolution()),generation,offspringFitness,_slClone.getImprovement,_slClone.A1,_slClone.A2,_slClone.A3,_slClone.A4,Optional.ofNullable(_slClone.A5),Optional.ofNullable(_slClone.A6),Optional.ofNullable(_slClone.optCase), threeOpt,"1+1EA");
        tspdata.setTimeElapsed(timer.getCurrentTimer());
        tspdata.setFunctionEvaluations(functionEvaluations-1);
        if (offspringFitness < bestFitness) {
            bestFitness = offspringFitness;
            tspdata.improved();
        } else {
            //noImprovementCounter++;
            _sl.revert();
        }

        listener.receiveUpdate(tspdata);
        _sl.clearData();
        if (noImprovementCounter > RESTART_THRESHOLD) {
            //System.out.println("Restarting the algorithm... in generation"+ generation + " with fitness: " + bestFitness );
            _sl.restart(); // Reinitialize the solution
            bestFitness = _sl.computeFitness();
            noImprovementCounter = 0; // Reset counter
        }

    }

    public void copyCreateCopy(Solution from){
        _slClone = new Solution();
        for(City c : from.getSolution()){
            _slClone.getSolution().add(c);
        }
        _slClone.set_tsp(from.get_tsp());

    }




}

