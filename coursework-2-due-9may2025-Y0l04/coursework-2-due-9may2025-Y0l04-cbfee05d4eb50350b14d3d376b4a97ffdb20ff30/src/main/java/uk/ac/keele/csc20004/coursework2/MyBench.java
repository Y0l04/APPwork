package uk.ac.keele.csc20004.coursework2;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import uk.ac.keele.csc20004.Bench;
import uk.ac.keele.csc20004.robots.Robot;

public class MyBench implements Bench {
    private final Queue<Robot> robotStorage;

    public MyBench() {
        this.robotStorage = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void addRobot(Robot incomingRobot) {
        if (incomingRobot != null) {
            robotStorage.offer(incomingRobot);
        }
    }

    @Override
    public Robot getNextRobot() {
        return robotStorage.poll();
    }

    @Override
    public void printOut() {
        for (Robot bot : robotStorage) {
            System.out.println(bot);
        }
    }

    @Override
    public int size() {
        return robotStorage.size();
    }
}
