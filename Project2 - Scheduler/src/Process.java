/**
 * Created by blenz on 2/19/17.
 */
public class Process {

    private int totalRunningTime, remainingTime, priority, priorityCount;
    private final int pid, arrivalTime, runTime;

    public Process(int pid, int arrivalTime, int runningTime) {
        this.pid = pid;
        this.arrivalTime = arrivalTime;
        this.runTime = runningTime;
        remainingTime = runTime;
        totalRunningTime = 0;
        priority = 5;
        priorityCount = 0;
    }

    public int getPid() {
        return pid;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getRunTime() {
        return runTime;
    }

    public void addTime(int time){
        totalRunningTime += time;
        remainingTime = remainingTime - time < 0 ? 0 : remainingTime - time;
        priorityCount++;
    }

    public int getTotalRunningTime() {
        return totalRunningTime-arrivalTime;
    }

    public int getRemainingTime(){
        return remainingTime;
    }

    public void reset(){
        totalRunningTime = 0;
        remainingTime = runTime;
        priority = 5;
        priorityCount = 0;
    }

    public int getPriorityUnits(){
        int result = 0;

        switch (priority){
            case 5:
                result = 1;
                break;
            case 4:
                result = 2;
                break;
            case 3:
                result = 4;
                break;
            case 2:
                result = 8;
                break;
            case 1:
                result = 16;
                break;
        }

        return result;
    }

    public int getPriority() {
        return priority;
    }

    public boolean decreasePriorityLevel() {
        if (priorityCount == getPriorityUnits() && priority > 1){
            priority--;
            priorityCount = 0;
            return true;
        }
        return false;
    }

    public void setEndTime(int time) {
        this.totalRunningTime = time;
    }

    @Override
    public String toString() {
        return "P(Pid=" +pid+", Arr="+ arrivalTime+ ", Run=" +runTime +", Rem="+remainingTime + ", Tot=" + totalRunningTime + ", Prty=" + priority +')';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Process process = (Process) o;

        if (pid != process.pid) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return pid;
    }
}
