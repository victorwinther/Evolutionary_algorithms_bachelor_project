package group.ea.structure.helperClasses;
import java.util.Optional;

public class Data {
    private String bitString;
    private int generation;
    private int fitness;
    private boolean yesNo;
    private Optional<Double> temp;

    private boolean stop;

    public Data(String bitString, int generation, int fitness, boolean yesNo, Optional<Double> temp,boolean stop) {
        this.bitString = bitString;
        this.generation = generation;
        this.fitness = fitness;
        this.yesNo = yesNo;
        this.temp = temp;
        this.stop = stop;
    }

    public void setYesNo(boolean b) {
        yesNo = b;
    }
    @Override
    public String toString() {
        return "Data{" +
                "generation=" + generation +
                ", bitString='" + bitString + '\'' +
                ", fitness=" + fitness +
                ", improved=" + yesNo +
                ", temp=" + temp +
                '}';
    }

    public void setTemp(Optional<Double> currentTemp) {
        temp = currentTemp;
    }

    public void setBitString(String newBitString) {
        bitString = newBitString;
    }

    public void setFitness(int newFitness) {
        fitness = newFitness;
    }

    public boolean getImproved(){
        return yesNo;
    }

    public String getBitString() {
        return bitString;
    }

    public int getGeneration() {
        return generation;
    }

    public int getFitness() {
        return fitness;
    }

    public Optional<Double> getTemp() {
        return temp;
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }

    // getters and setters for each field
    // ...
}