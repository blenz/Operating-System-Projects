import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

/**
 * Created by blenz on 2/2/17.
 */
public class PRMOutput {

    private String inputFileLocation;
    private static PrintWriter writer;
    private static StringBuilder output = new StringBuilder();
    private static boolean showConsole = true;

    public static void addOutput(String toAdd) {

        if (toAdd.equals("\n"))
            removeLastChar();
        else
            toAdd += ' ';

        if (showConsole)
            System.out.print(toAdd);

        output.append(toAdd);
    }

    public static void writeToFile() {
        String outputFile = PRMReader.getFileLocation() + "/output.txt";
        write(outputFile);
    }

    private static void removeLastChar() {
        if (output.length() > 0)
            output.deleteCharAt(output.length() - 1);
    }

    public static void hideConsoleOutput(){
        showConsole = false;
    }

    public static void writeToFile(String absoluteFilePath) {
        String outputFile = absoluteFilePath;
        write(outputFile);
    }

    private static void write(String outputFile){
        removeLastChar();
        try {
            writer = new PrintWriter(new FileOutputStream(outputFile));
            writer.write(output.toString());
            writer.close();
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }


}
