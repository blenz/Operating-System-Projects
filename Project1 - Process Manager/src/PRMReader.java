import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Created by blenz on 2/1/17.
 */
public class PRMReader extends CommandExecution {

    private static File inputFile;

    public PRMReader(String fileName) {
        inputFile = new File(fileName);
        readFile();
    }

    public static String getFileLocation(){
        return inputFile.getParent();
    }

    private void readFile() {

        try {
            input = new Scanner(inputFile);

            String command;

            while(input.hasNext())
            {
                command = input.nextLine();
                executeCommand(command);
            }
        }
        catch (FileNotFoundException e){
            System.out.print(e.getMessage());
        }
    }
}
