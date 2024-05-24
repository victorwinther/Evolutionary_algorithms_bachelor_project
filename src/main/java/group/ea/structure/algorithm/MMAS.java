package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;

public class MMAS extends ACO {

    private double minPheremone;
    private double maxPheremone;

    public MMAS(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);

        maxPheremone = 1.0 / ((evaporation * getValue()));
        minPheremone = maxPheremone / (2.0 * dimension);
        Q = maxPheremone;

    }

    public double getValue(){
        Ants();
        return bestAnt.getCost();
    }


    @Override
    public void updatePheromone(Ant ant) {
        double dTau = Q / ant.getCost();
        for (int i = 0; i < dimension; i++) {
            int j = ant.getTrailOfAnt()[i];
            int k = (i + 1 < dimension) ? ant.getTrailOfAnt()[i + 1] : ant.getTrailOfAnt()[0];
            pheromone[j][k] += dTau;
            pheromone[k][j] = pheromone[j][k]; // Ensure symmetry

            if (pheromone[j][k] > maxPheremone) {
                pheromone[j][k] = maxPheremone;
                pheromone[k][j] = maxPheremone;
            } else if (pheromone[j][k] < minPheremone) {
                pheromone[j][k] = minPheremone;
                pheromone[k][j] = minPheremone;
            }
        }
    }
}
