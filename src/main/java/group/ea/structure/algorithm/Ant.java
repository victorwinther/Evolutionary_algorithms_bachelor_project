package group.ea.structure.algorithm;

import group.ea.structure.TSP.City;

public class Ant {

    private int[] trailOfAnt;
    private boolean[] visited;
    private int _length;
    private int counter;

    private double _cost;

    public Ant(int length){
        _length = length;
        trailOfAnt = new int[length];
        visited = new boolean[length];
        counter = 0;
    }

    public void visitCity(int cityID){
        if(cityID == -1){
            System.out.println(cityID + " CityID");
        }


        trailOfAnt[counter++] = cityID;
        visited[cityID] = true;

    }

    public boolean visitedCity(int cityID){
        return visited[cityID];
    }

    public void setCost(double cost){
        _cost = cost;
    }

    public double getCost(){
        return _cost;
    }
    public void clearData(){
        for (int i = 0; i < _length; i++)
            visited[i] = false;
        counter = 0;
    }

    public int getTrailOfAnt(int ID){
        return trailOfAnt[ID];
    }
    public int[] getTrailOfAnt(){
        return trailOfAnt;
    }
    public void setTrailOfAnt(int[] trail){trailOfAnt = trail;}

    public void localMutate(int i, int j, int k){


        int first = trailOfAnt[i];
        int second = trailOfAnt[j];
        int third = trailOfAnt[k];

        trailOfAnt[i] = second;
        trailOfAnt[j] = third;
        trailOfAnt[k] = first;
    }

}
