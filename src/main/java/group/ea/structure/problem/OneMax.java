package group.ea.structure.problem;

import group.ea.structure.searchspace.BitString;
import group.ea.structure.searchspace.SearchSpace;

public class OneMax extends Problem {

    public OneMax(SearchSpace searchSpace) {
        name = "OnemMax";
        this.searchSpace = searchSpace;
    }

    @Override
    public double computeFitness(BitString bitString) {
        int count = 0;
        for (int i = 0; i < bitString.getLength(); i++) {
            if (bitString.getBitString().charAt(i) == '1') {
                count++;
            }
        }
        return count;
    }

}
