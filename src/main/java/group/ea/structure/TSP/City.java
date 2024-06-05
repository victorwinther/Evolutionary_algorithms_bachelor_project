package group.ea.structure.TSP;


public class City {
    private double[] coords;
    private int ID;

    public City(int ID, double[] coords){
        this.coords = coords;
        this.ID = ID;
    }
    public double getX(){
        return coords[0];
    }
    public double getY(){
        return coords[1];
    }
    public int getId(){ return ID;}

    public double distanceToCity(City city) {

        double x = Math.abs(getX() - city.getX());
        double y = Math.abs(getY() - city.getY());
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public void setX(int xSolution) {
        coords[0] = xSolution;
    }

    public void setY(int ySolution) {
        coords[1] = ySolution;
    }
}
