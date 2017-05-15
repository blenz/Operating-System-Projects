/**
 * Created by blenz on 3/8/17.
 */
public class VirtualAddress {

    private final Operation operation;
    private final String address, binary;
    private final int s, p, w;

    enum Operation {
        Read, Write
    }

    public VirtualAddress(String operation, String address) {
        this.operation = "0".equals(operation) ? Operation.Read : Operation.Write;
        this.address = address;
        StringBuilder binaryAddress = new StringBuilder(Integer.toBinaryString(Integer.parseInt(address)));

        while (binaryAddress.length() < 28)
            binaryAddress.insert(0, "0");
        this.binary = binaryAddress.toString();

        s = Integer.parseInt(binary.substring(0, 9), 2);
        p = Integer.parseInt(binary.substring(9, 19), 2);
        w = Integer.parseInt(binary.substring(19, 28), 2);
    }

    public Operation getOperation() {
        return operation;
    }

    public int getSegment() {
        return s;
    }

    public int getPage() {
        return p;
    }

    public int getOffset() {
        return w;
    }

    public String getInfo() {
        return String.format("(%d,%d,%d)", getSegment(), getPage(), getOffset());
    }

    @Override
    public String toString() {
        return "VA{" + operation + "," + address + '}';
    }
}
