public class Main {

    public static void main(String[] args) {

        String inputFileLocation = null, outputFileLocation = null;

        if (args.length == 2){
            inputFileLocation = args[0];
            outputFileLocation = args[1];
        }
        else {
            System.out.println("Add args -> <input file> <output file>");
            System.exit(0);
        }

        // Run the scheduler
        Scheduler scheduler = new Scheduler(inputFileLocation);
        scheduler.runScheduler();
        scheduler.writeOutput(outputFileLocation);
    }
}
