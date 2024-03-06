package group.ea.structure.searchspace;

public abstract class SearchSpace {
    public String name;
    public int length;

    public SearchSpace(int length) {
        this.length = length;
    }

    void init(){ }
}

