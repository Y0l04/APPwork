package uk.ac.keele.csc20004.coursework2;

import uk.ac.keele.csc20004.CombatPair;

public class BattleManager implements Runnable {
    private final MyConcurrentArena battleArena;

    public BattleManager(MyConcurrentArena arenaInstance) {
        this.battleArena = arenaInstance;
    }

    @Override
    public void run() {
        System.out.println(">> BattleManager active on thread: " + Thread.currentThread().getName());

        CombatPair fighters = battleArena.getOpponents();
        if (fighters == null) {
            return;
        }

        fighters.fight();

        battleArena.processResult(fighters.getRobot1());
        battleArena.processResult(fighters.getRobot2());
    }
}
