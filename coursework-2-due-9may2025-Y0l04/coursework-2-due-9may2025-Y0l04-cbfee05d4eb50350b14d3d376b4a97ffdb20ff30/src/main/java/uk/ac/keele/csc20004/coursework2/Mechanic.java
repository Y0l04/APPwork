package uk.ac.keele.csc20004.coursework2;

import uk.ac.keele.csc20004.Bench;
import uk.ac.keele.csc20004.RepairLine;
import uk.ac.keele.csc20004.robots.Robot;

public class Mechanic implements Runnable {

    private final RepairLine repairQueue;
    private final Bench workBench;
    private final MyConcurrentArena arenaInstance;

    public Mechanic(RepairLine repairQueue, Bench workBench, MyConcurrentArena arenaInstance) {
        this.repairQueue = repairQueue;
        this.workBench = workBench;
        this.arenaInstance = arenaInstance;
    }

    @Override
    public void run() {
        String threadId = Thread.currentThread().getName();
        System.out.println("[Mechanic] Running in thread: " + threadId);

        Robot unitToFix = repairQueue.getNextRobot();

        if (unitToFix != null) {
            arenaInstance.repair(unitToFix);
            workBench.addRobot(unitToFix);
        } else {
            System.out.println("[Mechanic] No robots available in queue for thread: " + threadId);
            try {
                Thread.sleep(1000); // Brief pause before retrying
            } catch (InterruptedException ex) {
                System.err.println("[Mechanic] Thread interrupted: " + threadId);
            }
        }
    }
}
