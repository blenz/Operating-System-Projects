import java.util.HashSet;
import java.util.LinkedList;

/**
 * Created by blenz on 1/28/17.
 */
public class Process {

    // Name
    private final String pid;

    // Used Resources
    private LinkedList<Resource> resources;

    // Status
    private Status status;
    private LinkedList list;

    // Creation Tree
    private Process parent;
    private LinkedList<Process> children;

    // Priority
    private int priority;

    // States
    enum Status {
        RUNNING, READY, BLOCKED
    }

    public Process(String pid, int priority) {
        this.pid = pid;
        setStatus(Status.READY);
        setPriority(priority);
        children = new LinkedList<>();
        resources = new LinkedList<>();
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Process getParent() {
        return parent;
    }

    public void addResource(Resource resource) {
        if (!resources.contains(resource))
            resources.add(resource);
    }

    public void setParent(Process parent) {
        this.parent = parent;
    }

    public void addChild(Process child) {
        this.children.addLast(child);
    }

    public LinkedList getList() {
        return list;
    }

    public LinkedList<Process> getChildren() {
        return children;
    }

    public void setList(LinkedList list) {
        this.list = list;
    }

    private void setPriority(int priority) {
        this.priority = priority;
    }

    public String getPid() {
        return pid;
    }

    public LinkedList<Resource> getResources() {
        return resources;
    }

    public Status getStatus() {
        return status;
    }

    @Override
    public String toString() {

        String debug = "resources";

        if (pid.toLowerCase().equals("init"))
            return "Process{" + pid + ", " + status + '}';


        if (debug.equals("children"))
            return "Process{" + pid + ", " + status + ", " + children + "}";
        else if (debug.equals("basic"))
            return "Process{" + pid + ", " + status + ", " + priority + "}";
        else if (debug.equals("resources"))
            return "Process{" + pid + ", " + status + ", " + resources + "}";
        else if (debug.equals("parent"))
            return "Process{" + pid + ", " + status + ", " + parent + "}";

        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Process process = (Process) o;

        return pid.equals(process.pid);
    }

    @Override
    public int hashCode() {
        return pid.hashCode();
    }

    public int getPriority() {
        return priority;
    }

}
