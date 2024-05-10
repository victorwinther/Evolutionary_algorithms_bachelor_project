package group.ea.structure.TSP;

import group.ea.structure.problem.Problem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


public class Solution extends Problem {

    TSPParser _tsp;
    private ArrayList<City> solution = new ArrayList<>();
    private ArrayList<City> prevSolution = new ArrayList<>();

    @Override
    public double computeFitness(String string) {
        return 0;
    }

    private void init(){
        for (Map.Entry<String, double[]> entry : _tsp.getTspCitiesDict().entrySet()) {
            int _id = Integer.parseInt(entry.getKey());
            City temp = new City(_id, entry.getValue());
            solution.add(temp);
        }
    }
    public Solution(TSPParser tsp){
        _tsp = tsp;
        System.out.println("Solution init");
        init();
    }

    public int getXSolution(int i){
        return (int) solution.get(i).getX();
    }
    public int getYSolution(int i){
        return (int) solution.get(i).getY();
    }

    public int getListLength(){
        return solution.size();
    }
    private int randomIndex(){
        return (int) (Math.random() * solution.size());
    }
    public void twoOptMutate(){
        prevSolution = new ArrayList<>(solution);
        int firstIndex = randomIndex();
        int secondIndex = randomIndex();

        while(firstIndex == secondIndex){
            secondIndex = randomIndex();
        }

        City firstCity = solution.get(firstIndex);
        City secondCity = solution.get(secondIndex);
        //System.out.println("swapping " + firstIndex + "which is " + firstCity.getId() + " with " + secondIndex + "which " + secondCity.getId());
        solution.set(firstIndex, secondCity);
        solution.set(secondIndex, firstCity);
        System.out.println(solution.size());
    }

    public int computeFitness(){
        double fitness = 0;
        for (int i = 0; i < solution.size(); i++){
            City start = solution.get(i);
            City end;
            if (i + 1 < solution.size()) {
                end = solution.get(i + 1);
            } else {
                end = solution.get(0);
            }
            //System.out.println("from start " + start.getId() + " to end: " + end.getId());
            fitness += start.distanceToCity(end);
        }

        return (int)fitness;
    }
    public void revert(){
        solution = prevSolution;
    }

    public void printSolution(){

        for( City c : solution){
            System.out.print(c.getId());
            System.out.println("x " + c.getX() + " y "+  c.getY());

        }
    }

    public double distanceBetweenIndex(int start, int end){
        return solution.get(start).distanceToCity(solution.get(end));
    }

    public int getDimension(){
        return _tsp.getDimension();
    }






}
