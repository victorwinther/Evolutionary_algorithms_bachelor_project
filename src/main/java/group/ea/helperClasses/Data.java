package group.ea.helperClasses;

import java.util.Optional;

public class Data {
    private String bitString;
    private int generation;
    private int fitness;
    private boolean yesNo;
    private Optional<Double> temp;
    private long timeElapsed = 0;

    private boolean stop;


    private int functionEvaluations = 0;

    public Data(String bitString, int generation, int fitness, boolean yesNo, Optional<Double> temp, boolean stop) {
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

    public boolean getImproved() {
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

    public long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public int getFunctionEvaluations() {
        return functionEvaluations;
    }

    public void setFunctionEvaluations(int functionEvaluations) {
        this.functionEvaluations = functionEvaluations;
    }

    public void setGeneration(int generation) {
        this.generation = generation;
    }

    // getters and setters for each field
    // ...
}