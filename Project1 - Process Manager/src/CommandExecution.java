import java.util.Scanner;

/**
 * Created by blenz on 2/1/17.
 */
public abstract class CommandExecution {

    protected ProcessResourceManager PRManager;
    protected Scanner input;
    boolean firstInit = true;

    protected void executeCommand(String commandInput){

        if (commandInput.length() == 0)
            return;

        String[] commands = commandInput.trim().split(" ");
        String command = commands[0];

        String pid, rid;
        int priority, units;


        if (PRManager == null && !command.equals("init")){
            PRManager = new ProcessResourceManager();
            firstInit = false;
        }


        try {
            switch (command) {
                case "init":
                    if (!firstInit)
                        PRMOutput.addOutput("\n");
                    PRManager = new ProcessResourceManager();
                    firstInit = false;
                    break;

                case "cr":
                    pid = commands[1];
                    priority = Integer.parseInt(commands[2]);
                    PRManager.createProcess(pid, priority);
                    break;

                case "req":
                    rid = commands[1].toUpperCase();
                    units = Integer.parseInt(commands[2]);
                    PRManager.requestResource(rid, units);
                    break;

                case "rel":
                    rid = commands[1].toUpperCase();
                    units = Integer.parseInt(commands[2]);
                    PRManager.releaseResource(rid, units);
                    break;

                case "to":
                    PRManager.timeOut();
                    break;

                case "de":
                    pid = commands[1];
                    PRManager.destroyProcess(pid);
                    break;

                case "p":
                    PRManager.printPRMInfo();
                    break;

                default:
                    PRMOutput.addOutput("error");
            }
        }
        catch (Exception e){
            PRMOutput.addOutput("error");
        }
    }
}
