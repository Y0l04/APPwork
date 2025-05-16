package uk.ac.keele.csc20004.coursework2;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import uk.ac.keele.csc20004.robots.Robot;
import uk.ac.keele.csc20004.RepairLine;


public class MyRepairLine implements RepairLine {
    private final Queue<Robot> repairQueue;

    public MyRepairLine(int maxCapacity) {
        this.repairQueue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void addRobot(Robot incoming) {
        if (incoming != null) {
            repairQueue.offer(incoming);
        }
    }

    @Override
    public Robot getNextRobot() {
        return repairQueue.poll();
    }

    @Override
    public int size() {
        return repairQueue.size();
    }

    @Override
    public void printOut() {
        for (Robot unit : repairQueue) {
            System.out.println(unit);
        }
    }
}
