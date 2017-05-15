public class Main {

    public static void main(String[] args) {

        String initFileLocation = "";
        String vaFileLocation = "";
        String outputFileLocation = "";

        if (args.length == 3){
            initFileLocation = args[0];
            vaFileLocation = args[1];
            outputFileLocation = args[2];
        }
        else {
            System.out.println("Add args -> <init file> <VA input> <output file>");
            System.exit(0);
        }


        // Without TLB
        VirtualMemorySystem notlb = new VirtualMemorySystem(initFileLocation,vaFileLocation,false);
        notlb.run();
        notlb.writeOutput(outputFileLocation);

        // With TLB
        VirtualMemorySystem tlb = new VirtualMemorySystem(initFileLocation,vaFileLocation,true);
        tlb.run();
        tlb.writeOutput(outputFileLocation);
    }
}
