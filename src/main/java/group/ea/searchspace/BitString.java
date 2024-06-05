package group.ea.searchspace;

public class BitString extends SearchSpace {
    private String bitString;

    public BitString(int length){
        super(length);
        this.bitString = new String();
        name = "Bitstring";

    }

    @Override
    public String init() {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Math.random() > 0.5 ? '1' : '0');
        }
        bitString = sb.toString();
        return bitString;
    }

    public int getLength() {return length;}

}
