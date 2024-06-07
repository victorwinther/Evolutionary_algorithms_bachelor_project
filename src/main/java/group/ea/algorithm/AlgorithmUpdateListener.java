package group.ea.algorithm;

import group.ea.problem.TSP.Solution;
import group.ea.helperClasses.Data;

import java.util.ArrayList;

public interface AlgorithmUpdateListener {
    /* void updateText(String text);
     void updateChart(int generation, int fitness);

     */
    //void tspGraphics(Solution solution);
    void tspGraphics(ArrayList<TSPDATA> solution);

    void firstSolution(Solution solution);

    void receiveUpdate(TSPDATA tspdata);

    void receiveBitstringUpdate(Data data);

    void recievePheromone(double[][] pheromone);
}
