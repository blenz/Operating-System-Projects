import java.util.Scanner;

/**
 * Created by blenz on 1/31/17.
 */
public class PRMShell extends CommandExecution {

    public PRMShell() {
        //PRManager = new ProcessResourceManager();
        input = new Scanner(System.in);
        getInput();
    }

    private void getInput(){

        String command;

        while (true)
        {
            System.out.print("> ");
            command = input.nextLine();

            if (command.equals("exit"))
                System.exit(0);

            executeCommand(command);
        }
    }
}
