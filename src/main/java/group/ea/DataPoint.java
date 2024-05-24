package group.ea;

class DataPoint {
    private final int bitStringLength;
    private final int iterations;

    public DataPoint(int bitStringLength, int iterations) {
        this.bitStringLength = bitStringLength;
        this.iterations = iterations;
    }

    public String getBitStringLength() {
        // int to char sequence

        return String.valueOf(bitStringLength);
    }

    public String getIterations() {
        return String.valueOf(iterations);
    }
}