package group.ea.algorithm;

import group.ea.problem.TSP.Solution;
import group.ea.helperClasses.Data;

import java.util.ArrayList;

public interface AlgorithmUpdateListener {

    void tspGraphics(ArrayList<TSPDATA> solution);

    void firstSolution(Solution solution);

    void receiveUpdate(TSPDATA tspdata);

    void receiveBitstringUpdate(Data data);

    void recievePheromone(double[][] pheromone);
}
