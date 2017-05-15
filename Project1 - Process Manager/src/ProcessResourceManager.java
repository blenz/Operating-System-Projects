import javafx.util.Pair;
import java.util.*;

/**
 * Created by blenz on 1/29/17.
 */

public class ProcessResourceManager{

    private LinkedList<Process>[] readyList;
    private HashMap<String, Process> blockList;

    private HashSet<String> processes;
    private HashMap<String,Resource> resources;

    // Current Running Process
    private Process runningProcess;

    private final int NUM_OF_PRIORITIES = 3;

    public ProcessResourceManager() {
        readyList = new LinkedList[NUM_OF_PRIORITIES];
        blockList = new HashMap<>();
        processes = new HashSet<>();
        resources = new HashMap<>();

        for (int i = 0; i < 3; i++) {
            readyList[i] = new LinkedList<>();
        }

        for (int i = 1; i < 5; i++) {
            createResource("R" + Integer.toString(i),i);
        }

        // Initialize with init process
        runningProcess = new Process("init", 0);
        readyList[runningProcess.getPriority()].add(runningProcess);
        PRMOutput.addOutput(runningProcess.getPid());
    }


    /*================================== PROCESSES ====================================*/

    public void createProcess(String pid, int priority) {
        if (ifProcessExists(pid) || priority < 1 || priority > NUM_OF_PRIORITIES-1) {
            printError();
            return;
        }

        Process newProcess = new Process(pid, priority);
        newProcess.setParent(runningProcess);
        insert("RL", newProcess);
        runScheduler();
    }

    public void destroyProcess(String pid) {

        Process p = getProcess(pid);
        if (p == null || p.getPid().equals("init"))
        {
            printError();
            return;
        }

        // Remove children and resources
        killTree(p);

        // Remove from parent's child list
        p.getParent().getChildren().remove(p);

        runScheduler();
    }

    private void killTree(Process process) {
        if (process.getChildren() == null )
            return;
        // Destroy Children
        for (Process p : process.getChildren())
            killTree(p);
        remove(process);
    }


    /*================================== RESOURCES ====================================*/

    public void requestResource(String rid, int units){

        Resource resource = resources.get(rid);

        if (resource == null || runningProcess.getPid().equals("init") || units == 0){
            printError();
            return;
        }

        Resource.RequestStatus status = resource.requestUnits(units,runningProcess);

        if (status == Resource.RequestStatus.SUCCESS)
            runningProcess.addResource(resource);
        else if (status == Resource.RequestStatus.BLOCKED)
        {
            runningProcess.getList().remove(runningProcess);
            resource.addWaitingList(runningProcess,units);
            blockList.put(runningProcess.getPid(),runningProcess);
        }
        else if (status == Resource.RequestStatus.FAIL){
            printError();
            return;
        }

        runScheduler();
    }

    public void releaseResource(String rid, int units){

        Resource resource = resources.get(rid);
        if (resource == null || units == 0 || !resource.releaseUnits(units,runningProcess)){
            printError();
            return;
        }
        runScheduler();
    }

    /*================================== PROCESS / RESOURCE MANAGER ====================================*/
    private void insert(String listType, Process process) {

        int priority = process.getPriority();

        if (listType.equals("RL")) {
            readyList[priority].addLast(process);
            process.setList(readyList[priority]);
        }

        processes.add(process.getPid());
        runningProcess.addChild(process);
    }

    private void remove(Process process) {
        int priority = process.getPriority();

        // Remove from ready list
        readyList[priority].remove(process);
        processes.remove(process.getPid());

        // Free Resources
        for (Resource resource : process.getResources())
            resource.releaseEntireProcess(process);

        // remove from waiting lists
        for (Resource r : resources.values())
            for(Pair<Process,Integer> p : r.getWaitingList())
                if (p.getKey().equals(process))
                    r.getWaitingList().remove(p);

    }

    private void runScheduler() {

        Process currentProcess = runningProcess;
        if (currentProcess.getStatus() != Process.Status.BLOCKED)
            currentProcess.setStatus(Process.Status.READY);

        if (!readyList[2].isEmpty())
            runningProcess = readyList[2].getFirst();
        else if (!readyList[1].isEmpty())
            runningProcess = readyList[1].getFirst();
        else if (!readyList[0].isEmpty())
            runningProcess = readyList[0].getFirst();

        PRMOutput.addOutput(runningProcess.getPid());
        runningProcess.setStatus(Process.Status.RUNNING);
    }

    public void timeOut() {

        Process currentProcess = runningProcess;

        if (currentProcess.getPid().equals("init")) {
            runScheduler();
            return;
        }

        currentProcess.setStatus(Process.Status.READY);
        LinkedList<Process> priorityList = currentProcess.getList();
        priorityList.pop();
        priorityList.addLast(currentProcess);
        runScheduler();
    }

    /*================================== HELPERS ====================================*/

    private void createResource(String rid, int units){
        Resource resource = new Resource(rid, units);
        resources.put(rid,resource);
    }

    private Boolean ifProcessExists(String pid) {
        return processes.contains(pid);
    }

    private Process getProcess(String pid) {
        // Check RL
        for (int i = 0; i < 3; i++)
            for (Process p : readyList[i])
                if (p.getPid().equals(pid))
                    return p;

        // Check BL
        if (blockList.containsKey(pid))
            return blockList.get(pid);

        return null;
    }

    public void printError(){
        PRMOutput.addOutput("error");
    }

    public void printPRMInfo() {
        System.out.println("\n\nReady List\n------");

        int priority = 2;

        for (int i = 2; i >= 0; i--) {
            System.out.printf(String.format("%s %s\n", Integer.toString(priority--), readyList[i]));
        }

        System.out.println("\nResources Usage\n------");
        for (int i = 2; i >= 0; i--) {
            for (Process p : readyList[i]) {
                for (Resource r : p.getResources()){
                    String result = p.getResources().getLast().equals(r) ? p.getPid() + " " + r.toString(p) : p.getPid() + " " + r.toString(p) + ", ";
                    System.out.print(result);
                }
            }
            System.out.print("\n");
        }

        System.out.println("Waiting List\n------");
        for (Map.Entry<String,Resource> r: resources.entrySet())
            System.out.println(r.getKey() + " " + r.getValue().getWaitingList());
    }

}
