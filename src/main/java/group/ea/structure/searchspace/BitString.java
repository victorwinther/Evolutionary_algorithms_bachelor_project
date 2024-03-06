package group.ea.structure.searchspace;

public class BitString extends SearchSpace {
    private int length;
    private String bitString;

    public BitString(int length){
        super(length);
        name = "Bitstring";
    }

    @Override
    void init() {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(Math.random() > 0.5 ? '1' : '0');
        }
        bitString = sb.toString();
    }

    public int getLength() {return length;}

    public String getBitString() {return bitString;}
}
