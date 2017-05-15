import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.*;

/**
 * Created by blenz on 3/7/17.
 */
public class VirtualMemorySystem {

    private Frame ST;
    private ArrayList<VirtualAddress> VAs;
    private StringBuilder result;
    private PhysicalMemory PM;
    private LinkedList<Integer> TLB;
    private boolean useTLB;

    public VirtualMemorySystem(String initFile, String vaFile, boolean useTLB) {
        this.VAs = new ArrayList<>();
        this.PM = new PhysicalMemory();
        this.ST = PM.getST();
        this.useTLB = useTLB;

        readInput(initFile, true);
        readInput(vaFile, false);
    }

    public void run() {

        result = new StringBuilder();
        int PA;

        if (useTLB)
            TLB = new LinkedList<>();

        for (VirtualAddress va : VAs) {
            PA = processVA(va);
            if (PA == -1)
                continue;
            appendResult(Integer.toString(PA));
        }

        //System.out.println(PM);
        System.out.println(result);
    }

    private int processVA(VirtualAddress va) {

        // Get the ST entry
        int stIndex = va.getSegment();
        int stEntry = ST.getEntry(va.getSegment());

        // Get the PT entry
        int ptIndex = ST.getEntry(stIndex) / ST.getSize();
        int ptEntry = PM.getFrame(ptIndex).getEntry(va.getPage());

        // Use TLB
        if (useTLB)
            return useTLB(va, stEntry, ptIndex, ptEntry);
        else
            return noTLB(va, stEntry, ptIndex, ptEntry);
    }

    private int noTLB(VirtualAddress va, int stEntry, int ptIndex, int ptEntry) {

        // Return the correct result
        if (stEntry == 0 || ptEntry == 0) {
            if (va.getOperation() == VirtualAddress.Operation.Read) {
                appendResult("err");
                return -1;
            }
            else
                return PM.createPageFromVA(va, stEntry, ptIndex) + va.getOffset();
        }
        else if (stEntry == -1 || ptEntry == -1) {
            appendResult("pf");
            return -1;
        }

        return ptEntry + va.getOffset();
    }

    private int useTLB(VirtualAddress va, int stEntry, int ptIndex, int ptEntry) {

        if (stEntry == 0 || ptEntry == 0) {
            if (va.getOperation() == VirtualAddress.Operation.Read) {
                appendResult("m err");
                return -1;
            }
            else
                ptEntry = PM.createPageFromVA(va, stEntry, ptIndex);
        }
        else if (ptEntry == -1) {
            appendResult("m pf");
            return -1;
        }

        // Hit
        if (TLB.contains(ptEntry)) {
            TLB.remove(new Integer(ptEntry));
            TLB.addFirst(ptEntry);
            appendResult("h");
        }
        // Doesn't have 4 values
        else if (TLB.size() < 4) {
            TLB.addFirst(ptEntry);
            appendResult("m");
        }
        // Miss
        else if (TLB.size() == 4) {
            TLB.addFirst(ptEntry);
            TLB.removeLast();
            appendResult("m");
        }

        return ptEntry + va.getOffset();
    }


    public void readInput(String inputFileLocation, boolean initFile) {

        File inputFile = new File(inputFileLocation);
        Scanner input;

        try {
            input = new Scanner(inputFile);

            if (initFile) {
                String[] segmentLine = input.nextLine().split(" ");
                String[] pageLine = input.nextLine().split(" ");
                int s, f, p;

                for (int i = 0; i < segmentLine.length; i += 2) {
                    s = Integer.parseInt(segmentLine[i]);
                    f = Integer.parseInt(segmentLine[i + 1]);

                    PM.addPageTableFromFile(s, f);
                }
                for (int i = 0; i < pageLine.length; i += 3) {
                    p = Integer.parseInt(pageLine[i]);
                    s = Integer.parseInt(pageLine[i + 1]);
                    f = Integer.parseInt(pageLine[i + 2]);

                    PM.addPageFromFile(p, s, f);
                }
            }
            else {
                String[] vaLine = input.nextLine().split(" ");
                for (int i = 0; i < vaLine.length; i += 2)
                    VAs.add(new VirtualAddress(vaLine[i], vaLine[i + 1]));
            }


        } catch (FileNotFoundException e) {
            System.out.println("Error getting processes from file");
        }
    }

    private void appendResult(String toAdd) {
        result.append(toAdd + " ");
    }

    public void writeOutput(String outputFile) {

        outputFile = outputFile.substring(0,outputFile.lastIndexOf('.'));
        outputFile += useTLB ? "-tlb.txt" : "-notlb.txt";

        PrintWriter writer;
        try {
            if (result.length() > 0)
                result.deleteCharAt(result.length() - 1);
            writer = new PrintWriter(new FileOutputStream(outputFile));
            writer.write(result.toString());
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing output to file");
        }
    }
}
