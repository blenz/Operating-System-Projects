import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Created by blenz on 2/19/17.
 */
public class Scheduler {

    private LinkedList<Process> waitingProcesses, activeProcesses, doneProcesses;
    private int timePerProcessTotal, time, numProcesses;
    private StringBuilder result;

    private enum SortBy {
        FIFO, SJF, SRT, MLF
    }

    public Scheduler(String inputFileLocation) {
        waitingProcesses = new LinkedList<>();
        getProcessesFromInputFile(inputFileLocation);
    }

    public Scheduler() {
        waitingProcesses = new LinkedList<>();
    }

    public void runScheduler() {

        activeProcesses = new LinkedList<>();
        doneProcesses = new LinkedList<>();
        result = new StringBuilder();
        numProcesses = waitingProcesses.size();
        timePerProcessTotal = 0;
        time = 0;
        addWaitingProcesses();

        try {
            runFIFO();
            runSJF();
            runSRT();
            runMLF();
            System.out.print(result.toString());
        } catch (Exception e) {
            System.out.println("Error running scheduler - make sure scheduler has processes");
            System.exit(0);
        }
    }

    private void runFIFO() {
        Process process;
        sortProcesses(SortBy.FIFO);

        while (!activeProcesses.isEmpty()) {
            process = activeProcesses.getFirst();
            time += process.getRunTime();
            process.addTime(time);
            timePerProcessTotal += process.getTotalRunningTime();

            if (process.getRemainingTime() == 0)
                doneProcesses.add(activeProcesses.pop());
            if (addWaitingProcesses())
                sortProcesses(SortBy.FIFO);
        }

        addToResult();
        reset();
    }

    private void runSJF() {
        Process process;
        sortProcesses(SortBy.SJF);

        while (!activeProcesses.isEmpty()) {
            process = activeProcesses.getFirst();
            time += process.getRunTime();
            process.addTime(time);
            timePerProcessTotal += process.getTotalRunningTime();

            if (process.getRemainingTime() == 0)
                doneProcesses.add(activeProcesses.pop());
            if (addWaitingProcesses())
                sortProcesses(SortBy.SJF);
        }

        addToResult();
        reset();
    }

    private void runSRT() {
        Process process;
        sortProcesses(SortBy.SRT);

        while (!activeProcesses.isEmpty()) {

            process = activeProcesses.getFirst();
            process.addTime(1);
            time++;

            if (process.getRemainingTime() == 0) {
                process.setEndTime(time);
                timePerProcessTotal += process.getTotalRunningTime();
                doneProcesses.add(activeProcesses.pop());
            }

            if (addWaitingProcesses())
                sortProcesses(SortBy.SRT);
        }

        addToResult();
        reset();
    }

    private void runMLF() {
        Process process;
        sortProcesses(SortBy.MLF);

        while (!activeProcesses.isEmpty()) {

            process = activeProcesses.getFirst();
            process.addTime(1);
            time++;

            if (process.getRemainingTime() == 0) {
                process.setEndTime(time);
                timePerProcessTotal += process.getTotalRunningTime();
                doneProcesses.add(activeProcesses.pop());
            } else {
                if (!process.decreasePriorityLevel() && !isProcessWaiting()) {
                    sortProcesses(SortBy.MLF);
                    continue;
                }
                activeProcesses.addLast(activeProcesses.pop());
            }

            addWaitingProcesses();
            sortProcesses(SortBy.MLF);
        }

        addToResult();
        reset();
    }

    private void sortProcesses(SortBy sortBy) {

        if (sortBy == SortBy.FIFO) {
            activeProcesses.sort((Process a, Process b) -> {
                if (Integer.compare(a.getArrivalTime(), b.getArrivalTime()) == 0)
                    return Integer.compare(a.getPid(), b.getPid());
                else
                    return Integer.compare(a.getArrivalTime(), b.getArrivalTime());
            });
        } else if (sortBy == SortBy.SJF) {
            activeProcesses.sort((Process a, Process b) -> {
                if (Integer.compare(a.getRunTime(), b.getRunTime()) == 0)
                    return Integer.compare(a.getPid(), b.getPid());
                else
                    return Integer.compare(a.getRunTime(), b.getRunTime());
            });
        } else if (sortBy == SortBy.SRT) {
            activeProcesses.sort((Process a, Process b) -> {
                if (Integer.compare(a.getRemainingTime(), b.getRemainingTime()) == 0)
                    return Integer.compare(a.getPid(), b.getPid());
                else
                    return Integer.compare(a.getRemainingTime(), b.getRemainingTime());
            });
        } else if (sortBy == SortBy.MLF) {
            activeProcesses.sort((Process a, Process b) -> {
                if (Integer.compare(a.getPriority(), b.getPriority()) == 0)
                    return Integer.compare(a.getPid(), b.getPid());
                else
                    return Integer.compare(b.getPriority(), a.getPriority());
            });
        }
    }

    private boolean addWaitingProcesses() {

        boolean didAddProcess = false;

        for (Process process : waitingProcesses) {
            if (process.getArrivalTime() <= time) {
                activeProcesses.addFirst(process);
                didAddProcess = true;
            }
        }

        if (!didAddProcess)
            if (activeProcesses.isEmpty() && doneProcesses.size() != numProcesses) {
                time++;
                return addWaitingProcesses();
            } else
                return didAddProcess;

        // Remove process from the waiting list
        waitingProcesses.removeAll(activeProcesses);

        return didAddProcess;
    }

    private boolean isProcessWaiting() {
        for (Process process : waitingProcesses)
            if (process.getArrivalTime() <= time)
                return true;
        return false;
    }

    private void reset() {
        timePerProcessTotal = 0;
        time = 0;

        for (Process process : doneProcesses)
            process.reset();

        waitingProcesses.addAll(doneProcesses);
        doneProcesses.clear();
        activeProcesses.clear();

        addWaitingProcesses();
    }

    private void addToResult() {
        doneProcesses.sort((Process a, Process b) -> {
            return Integer.compare(a.getPid(), b.getPid());
        });

        String line = "";
        for (Process process : doneProcesses) {
            line += doneProcesses.getLast().equals(process) ? process.getTotalRunningTime() + "\n" : process.getTotalRunningTime() + " ";
        }

        result.append(computeAverage(timePerProcessTotal) + " ");
        result.append(line);
    }

    private String computeAverage(int totalTime) {
        return String.format("%.3f", (float) totalTime / numProcesses).replaceFirst(".$", "");
    }

    public void writeOutput(String outputFile) {
        PrintWriter writer;
        try {
            result.deleteCharAt(result.length() - 1);
            writer = new PrintWriter(new FileOutputStream(outputFile));
            writer.write(result.toString());
            writer.close();
        } catch (Exception e) {
            System.out.println("Error writing output to file - make sure the scheduler was ran");
        }
    }

    public void getProcessesFromInputFile(String inputFileLocation) {

        File inputFile = new File(inputFileLocation);
        Scanner input;

        try {
            input = new Scanner(inputFile);
            String[] processLine;
            while (input.hasNext()) {
                processLine = input.nextLine().split(" ");

                for (int i = 0, pid = 1; i < processLine.length; i += 2, pid++) {

                    int arrivalTime = Integer.parseInt(processLine[i]);
                    int runningTime = Integer.parseInt(processLine[i + 1]);

                    waitingProcesses.addLast(new Process(pid, arrivalTime, runningTime));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error getting processes from file");
        }
    }
}
