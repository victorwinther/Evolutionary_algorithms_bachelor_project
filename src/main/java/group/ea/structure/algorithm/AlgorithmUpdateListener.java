package group.ea.structure.algorithm;

import group.ea.structure.TSP.Solution;

import java.util.ArrayList;

public interface AlgorithmUpdateListener {
   /* void updateText(String text);
    void updateChart(int generation, int fitness);

    */
    //void tspGraphics(Solution solution);
    void tspGraphics(ArrayList<TSPDATA> solution);

    void firstSolution(Solution solution);

    void receiveUpdate(TSPDATA tspdata);
}
