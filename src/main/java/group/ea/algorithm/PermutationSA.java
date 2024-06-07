package group.ea.algorithm;



import group.ea.problem.TSP.City;
import group.ea.problem.TSP.Solution;
import group.ea.helperClasses.Data;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

import java.util.ArrayList;
import java.util.Optional;


//TEMP CLASS WILL BE FIXED ASAP

//TODO
//MERGE THIS AND NORMAL SA
public class PermutationSA extends  Algorithm {

    Solution _slClone;
    double initTemp;
    double tempReduction;


    public PermutationSA(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        _sl = (Solution) problem;
        bestFitness = _sl.computeFitness();
        initTemp = Math.pow(searchSpace.returnLength(),3);
        tempReduction = 1-(1/Math.pow(searchSpace.returnLength(),2));
        currentTemp = initTemp;
        System.out.print("Construct done");
    }

    @Override
    public void initialize() {

    }

    @Override
    public void performSingleUpdate(int generation) {
        _sl.clearData();
        if (currentTemp < 1) {
            System.out.println("too cool");
            stoppingMet = true;
            return;
        }
        _sl.twoOptMutate();
        int offspringFitness = _sl.computeFitness();

        System.out.println(offspringFitness + " " + bestFitness);



        if (offspringFitness < bestFitness) {
            functionEvaluations++;
            _slClone = new Solution(_sl.get_tsp());
            _slClone.deepCopy(_sl);
            TSPDATA tspdata = new TSPDATA(_slClone,_slClone.getSolution(),generation-1,offspringFitness,currentTemp,"SA");
            tspdata.setTimeElapsed(timer.getCurrentTimer());
            tspdata.setFunctionEvaluations(functionEvaluations);
            listener.receiveUpdate(tspdata);
            bestFitness = offspringFitness;

        } else if (Math.exp((offspringFitness - bestFitness) / currentTemp) > Math.random()) {
            _sl.revert();
        }
        currentTemp *= tempReduction;

    }
    public void copyCreateCopy(Solution from){
        _slClone = new Solution();
        for(City c : from.getSolution()){
            _slClone.getSolution().add(c);
        }
        _slClone.set_tsp(from.get_tsp());

    }
}