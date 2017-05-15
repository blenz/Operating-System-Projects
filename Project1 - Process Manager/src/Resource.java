import javafx.util.Pair;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by blenz on 1/29/17.
 */
public class Resource {

    private final String rid;
    private Status status;
    private LinkedList<Pair<Process,Integer>> waitingList;
    private int units;
    private final int maxUnits;
    private HashMap<String,Integer> processes;

    private enum Status{
        FREE, ALLOCATED
    }

    enum RequestStatus {
        SUCCESS, FAIL, BLOCKED
    }

    public Resource(String rid, int units) {
        this.rid = rid;
        this.units = units;
        maxUnits = units;

        status = Status.FREE;

        processes = new HashMap<>();
        waitingList = new LinkedList<>();
    }

    public RequestStatus requestUnits(int requestedUnits, Process process){

        int availableUnits = units - requestedUnits;

        int totalRequestedByResource = processes.containsKey(process.getPid()) ? processes.get(process.getPid()) + requestedUnits : requestedUnits;

        if (availableUnits <= units && availableUnits >= 0){
            units = availableUnits;

            String processPid = process.getPid();

            if (!processes.containsKey(processPid))
                processes.put(processPid,requestedUnits);
            else
                processes.put(processPid,processes.get(processPid)+requestedUnits);

            return RequestStatus.SUCCESS;
        }
        else if (Math.abs(availableUnits) <= maxUnits  && requestedUnits <= maxUnits && totalRequestedByResource <= maxUnits)
        {
            return RequestStatus.BLOCKED;
        }
        return RequestStatus.FAIL;
    }

    public boolean releaseUnits(int releasedUnits, Process process){

        int availableUnits = units + releasedUnits;

        if (availableUnits <= maxUnits && availableUnits >= 0){
            units = availableUnits;

            String processPid = process.getPid();

            if (units == maxUnits) {
                processes.remove(processPid);
                process.getResources().remove(this);
            }
            else
                processes.put(processPid,processes.get(processPid)-releasedUnits);

            checkWaitingList();
            return true;
        }
        return false;
    }

    public void addWaitingList(Process process, int units) {
        process.setStatus(Process.Status.BLOCKED);
        waitingList.addLast(new Pair<>(process,units));
    }

    public void releaseEntireProcess(Process process) {
        int usedUnits = processes.get(process.getPid());
        processes.remove(process.getPid());
        units += usedUnits;
        checkWaitingList();
    }

    public void checkWaitingList(){
        if (waitingList.size() == 0)
            return;

        Pair<Process,Integer> pair = waitingList.getFirst();
        Process process = pair.getKey();
        int requestedUnits = pair.getValue();

        if (requestUnits(requestedUnits,process) == RequestStatus.SUCCESS){
            waitingList.removeFirst();
            process.setStatus(Process.Status.READY);
            process.getList().addLast(process);
            process.addResource(this);
        }
    }

    public int getUnits() {
        return units;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LinkedList<Pair<Process,Integer>> getWaitingList() {
        return waitingList;
    }

    @Override
    public String toString() {
        return rid;
    }

    public String toString(Process p) {
        return rid + "->" + Integer.toString(processes.get(p.getPid()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        return rid.equals(resource.rid);

    }

    @Override
    public int hashCode() {
        return rid.hashCode();
    }
}
