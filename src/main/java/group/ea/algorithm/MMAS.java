package group.ea.algorithm;

import group.ea.problem.TSP.Solution;
import group.ea.problem.Problem;
import group.ea.searchspace.SearchSpace;

public class MMAS extends ACO {

    private double minPheremone;
    private double maxPheremone;

    public MMAS(SearchSpace searchSpace, Problem problem) {
        super(searchSpace, problem);
        maxPheremone = 1.0 / ((evaporation * getValue()));
        minPheremone = maxPheremone / (2000.0 * dimension);

        Q = maxPheremone;
    }

    public double getValue() {
        _cloneSl = new Solution(_sl.get_tsp());

        localSearch();
        return _cloneSl.computeFitness();

    }

    @Override
    public void updatePheromone() {
        super.updatePheromone();
        limitPheromones();
    }

    private void limitPheromones() {
        for (int i = 0; i < (dimension); i++) {
            for (int j = 0; j < (dimension); j++) {
                if (pheromone[i][j] > maxPheremone) {
                    pheromone[i][j] = maxPheremone;
                    pheromone[j][i] = maxPheremone;
                } else if (pheromone[i][j] < minPheremone) {
                    pheromone[i][j] = minPheremone;
                    pheromone[j][i] = minPheremone;
                }
            }
        }
    }
}
