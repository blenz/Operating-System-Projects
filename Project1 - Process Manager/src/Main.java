// Brett Lenz
// 76382638

public class Main {
    public static void main(String[] args) {

        final String ABSOLUTE_INPUT_FILE;

        if (args.length == 1 || args.length == 2)
            ABSOLUTE_INPUT_FILE = args[0];
        else
            ABSOLUTE_INPUT_FILE = "ABSOLUTE FILE PATH GOES HERE";


        new PRMReader(ABSOLUTE_INPUT_FILE);


        if (args.length == 2)
            PRMOutput.writeToFile(args[1]);
        else
            PRMOutput.writeToFile();

        System.out.println();
    }
}
