package group.ea.problem;

import group.ea.searchspace.SearchSpace;

public class OneMax extends Problem {

    public OneMax(SearchSpace searchSpace) {
        name = "OnemMax";
        this.searchSpace = searchSpace;
    }

    @Override
    public double computeFitness(String string) {
        int count = 0;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

}
