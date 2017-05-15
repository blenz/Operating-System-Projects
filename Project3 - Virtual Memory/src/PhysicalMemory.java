import java.util.TreeMap;

/**
 * Created by blenz on 3/9/17.
 */
public class PhysicalMemory {

    private TreeMap<Integer,Frame> pm;
    private Frame st;
    private int frameSize;

    public PhysicalMemory() {

        pm = new TreeMap<>();
        st = new Frame(0, Frame.Type.ST);
        pm.put(0,st);
        frameSize = st.getSize();
    }

    public Frame getST(){
        return st;
    }

    public void addPageFromFile(int p, int s, int f) {
        int ptIndex = getFrameIndex(st.getEntry(s));

        pm.putIfAbsent(ptIndex,new Frame(ptIndex, Frame.Type.PT));
        pm.putIfAbsent(ptIndex+1,new Frame(ptIndex+1, Frame.Type.PT2));
        pm.get(ptIndex).addIntoFrame(p,f);

        pm.putIfAbsent(getFrameIndex(f),new Frame(getFrameIndex(f), Frame.Type.Page));
    }

    public Frame getFrame(int i){
        return pm.get(i);
    }

    public int getFrameIndex(int address) {
        return address/frameSize;
    }

    public int createPageFromVA(VirtualAddress va, int stEntry, int ptIndex) {

        boolean needToAddNewPT = stEntry == 0 ? true : false;

        if (needToAddNewPT) {
            ptIndex = insertNewPageTableFromVA(va);
            if (ptIndex == -1)
                return -1;
        }

        int newPageAddr = insertNewPageFromVA(va, ptIndex);

        if (newPageAddr == -1)
            return -1;

        return newPageAddr;
    }

    public void addPageTableFromFile(int s, int f){
        st.addIntoFrame(s, f);
        int ptIndex = getFrameIndex(f);
        pm.putIfAbsent(ptIndex,new Frame(ptIndex, Frame.Type.PT));
        pm.putIfAbsent(ptIndex+1,new Frame(ptIndex+1, Frame.Type.PT2));
    }

    private int insertNewPageTableFromVA(VirtualAddress va){
        for (int i = 0; i < 1024; i++)
            if (pm.get(i) == null && pm.get(i+1) == null){
                pm.putIfAbsent(i,new Frame(i, Frame.Type.PT));
                pm.putIfAbsent(i+1,new Frame(i+1, Frame.Type.PT2));
                st.addIntoFrame(va.getSegment(),pm.get(i).getAddress());
                return getFrameIndex(pm.get(i).getAddress());
            }
        return -1;
    }

    private int insertNewPageFromVA(VirtualAddress va, int ptIndex){
        for (int i = 0; i < 1024; i++)
            if (pm.get(i) == null){
                pm.putIfAbsent(i,new Frame(i, Frame.Type.Page));
                pm.get(ptIndex).addIntoFrame(va.getPage(),pm.get(i).getAddress());
                return pm.get(i).getAddress();
            }
        return -1;
    }

    @Override
    public String toString() {
        String result = "PM: ";

        for (int i = 0; i < pm.size(); i++)
            if (pm.get(i) == null)
                result += "--- ";
            else if (pm.get(i).getType() == Frame.Type.ST)
                result += "ST(" + pm.get(i).getAddress() + ") ";
            else if (pm.get(i).getType() == Frame.Type.PT)
                result += "PT(" + pm.get(i).getAddress() + ") ";
            else if (pm.get(i).getType() == Frame.Type.PT2)
                    result += "PT2(" + pm.get(i).getAddress() + ") ";
            else if (pm.get(i).getType() == Frame.Type.Page)
                result += "Page(" + pm.get(i).getAddress() + ") ";
        return result;
    }
}
