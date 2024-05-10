package group.ea.structure.algorithm;

import group.ea.controllers.mainController;
import group.ea.structure.problem.Problem;
import group.ea.structure.searchspace.SearchSpace;

public class MMAS extends ACO {

    private double minPheremone;
    private double maxPheremone;

    public MMAS(SearchSpace searchSpace, Problem problem, mainController controller) {
        super(searchSpace, problem, controller);

        maxPheremone = 1.0 / ((evaporation * getValue()));
        minPheremone = maxPheremone / (2.0 * dimension);
        Q = maxPheremone;

    }

    public double getValue(){
        Ants();
        return bestAnt.getCost();
    }


    @Override
    public void updatePheremone(Ant a) {
        super.updatePheremone(a);
        for(int i = 0; i < dimension; i ++){
            int h = 0;
            if (i + 1 < dimension) {
                h = a.getTrailOfAnt()[i + 1];
            } else {
                h = a.getTrailOfAnt()[0];
            }
            int j = a.getTrailOfAnt()[i];

            if(pheromone[j][h] > maxPheremone){
                pheromone[h][j] = maxPheremone;
                pheromone[j][h] = maxPheremone;
            }

            else if(pheromone[j][h] < minPheremone){
                pheromone[h][j] = minPheremone;
                pheromone[j][h] = minPheremone;
            }

        }
    }
}
