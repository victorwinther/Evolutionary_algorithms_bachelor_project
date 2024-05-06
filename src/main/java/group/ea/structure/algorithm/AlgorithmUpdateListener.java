package group.ea.structure.algorithm;

import group.ea.structure.TSP.Solution;

public interface AlgorithmUpdateListener {
   /* void updateText(String text);
    void updateChart(int generation, int fitness);

    */
    void tspGraphics(Solution solution);
}
