import java.util.HashMap;

/**
 * Created by blenz on 3/9/17.
 */
public class Frame {
    private HashMap<Integer,Integer> frame;
    private int address;
    private Type type;
    private int size;

    enum Type{
        ST,PT,PT2,Page
    }

    public Frame(int index, Type type) {
        size = 512;
        frame = new HashMap<>();
        address = index*size;
        setType(type);
    }

    public void addIntoFrame(int index, int value) {
        if (index < size)
            frame.put(index,value);
    }

    public int getEntry(int index){
        if (frame.containsKey(index) && index < size)
            return frame.get(index);
        return 0;
    }

    public int getAddress() {
        return address;
    }

    public void setType(Type type) {
        this.type = type;

        if (type == Type.PT)
            size = 1024;
        else if (type == Type.PT2)
            frame = null;
    }

    public Type getType() {
        return type;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return address + " " + type +
                " frame=" + frame;
    }
}
