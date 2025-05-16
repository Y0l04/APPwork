package uk.ac.keele.csc20004.coursework2;

import uk.ac.keele.csc20004.RepairLine;
import uk.ac.keele.csc20004.RobotArena;
import uk.ac.keele.csc20004.Bench;
import uk.ac.keele.csc20004.CombatPair;
import uk.ac.keele.csc20004.robots.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyConcurrentArena extends RobotArena {

    private final Object arenaSync = new Object();

    private enum RobotType {
        HUMANOID, WHEELED, CYCLOBOT
    }

    public MyConcurrentArena(int studentId, int capacity, RepairLine repairLine, Bench bench) {
        super(studentId, capacity, repairLine, bench);
        RobotType[] typeCycle = RobotType.values();
        for (int i = 0; i < capacity; i++) {
            robots.add(createRobot(i, typeCycle[i % typeCycle.length]));
        }
    }

    private Robot createRobot(int id, RobotType type) {
        switch (type) {
            case HUMANOID: return new HumanoidRobot("R-" + id);
            case WHEELED: return new WheeledRobot("R-" + id);
            case CYCLOBOT: return new CycloBot("R-" + id);
            default: throw new IllegalArgumentException("Unsupported robot type: " + type);
        }
    }

    @Override
    public void repair(Robot robot) {
        if (robot.getFrameEnergy() == 0.0) robot.refillFrameEnergy(frameBay);
        if (robot.getMotorEnergy() == 0.0) robot.refillMotorEnergy(motorBay);
        if (robot.getSensorsEnergy() == 0.0) robot.refillSensorsEnergy(sensorBay);
        if (robot.getActuatorsEnergy() == 0.0) robot.refillActuatorsEnergy(actuatorBay);
    }

    public void startRound() {
        System.out.println("--- Running simulation cycle ---");

        ExecutorService executor = Executors.newCachedThreadPool();

        // Combat
        int totalCombats;
        synchronized (arenaSync) {
            totalCombats = Math.min(robots.size() / 2, (int)(Math.random() * (robots.size() / 2 + 1)));
        }
        for (int i = 0; i < totalCombats; i++) {
            executor.execute(new BattleManager(this));
        }

        // Repairs
        int pendingRepairs = repairLine.size();
        int repairCount = (int)(Math.random() * (pendingRepairs + 1));
        for (int i = 0; i < repairCount; i++) {
            executor.execute(new Mechanic(repairLine, bench, this));
        }

        // Bench
        int restingCount = bench.size();
        int benchTasks = (int)(Math.random() * (restingCount + 1));
        for (int i = 0; i < benchTasks; i++) {
            executor.execute(() -> {
                Robot bot = bench.getNextRobot();
                if (bot != null) addRobot(bot);
            });
        }

        executor.shutdown();
    }

    public void processResult(Robot r) {
        double energySum = r.getFrameEnergy() + r.getMotorEnergy() +
                           r.getSensorsEnergy() + r.getActuatorsEnergy();
        if (energySum > 50.0) {
            addRobot(r);
        } else {
            repairLine.addRobot(r);
        }
    }

    @Override
    public Robot getWinner() {
        double peakEnergy = 0.0;
        Robot champ = null;

        synchronized (arenaSync) {
            for (Robot r : robots) {
                double e = r.getFrameEnergy() + r.getMotorEnergy() +
                           r.getSensorsEnergy() + r.getActuatorsEnergy();
                if (e > peakEnergy) {
                    peakEnergy = e;
                    champ = r;
                }
            }
        }
        return champ;
    }

    @Override
    public void addRobot(Robot r) {
        synchronized (arenaSync) {
            robots.add(r);
        }
    }

    @Override
    public int getNumRobots() {
        synchronized (arenaSync) {
            return robots.size();
        }
    }

    @Override
    public void printStats() {
        synchronized (arenaSync) {
            System.out.println("=== Arena Summary for Student ID: " + studentId + " ===");
            System.out.println("Participants:");
            for (Robot r : robots) {
                System.out.println(r);
            }
        }

        System.out.println("-- In Repair Queue --");
        repairLine.printOut();

        System.out.println("-- Resting on Bench --");
        bench.printOut();
    }

    public CombatPair getOpponents() {
        synchronized (arenaSync) {
            if (robots.size() < 2) return null;
            Robot one = robots.remove(0);
            Robot two = robots.remove(0);
            return new CombatPair(one, two);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        MyBench myBench = new MyBench();
        MyRepairLine myRepair = new MyRepairLine(SimulationParameters.MAX_REPAIRLINE_SIZE);
        MyConcurrentArena arena = new MyConcurrentArena(22016974, SimulationParameters.NUM_ROBOTS, myRepair, myBench);

        System.out.println("--- Robot Arena Simulation Begins ---");

        int cycles = 20;
        while (cycles-- > 0) {
            System.out.println("\n[Simulation Round: " + (20 - cycles) + "]");
            arena.startRound();

            // Allow time for threads to complete each round
            Thread.sleep(500);
            arena.printStats();
        }

        Robot w = arena.getWinner();
        System.out.println("\n*** Champion: " + (w != null ? w : "No valid winner") + " ***");
        System.out.println("--- Simulation Ends ---");
    }
}
