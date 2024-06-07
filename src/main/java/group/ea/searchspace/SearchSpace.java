package group.ea.searchspace;

public abstract class SearchSpace {
    public String name;
    public int length;

    public SearchSpace(int length) {
        this.length = length;
    }

    public SearchSpace() {

    }
    public int returnLength(){return length;}

    public abstract String init();

}

