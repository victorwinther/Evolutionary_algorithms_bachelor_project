package group.ea.structure;

interface Blueprint {
    String searchspace = "", problem = "", algorithm = "", stoppingCriteria = "";
    int batches = 0;

    default void mutate(){

    }




}
