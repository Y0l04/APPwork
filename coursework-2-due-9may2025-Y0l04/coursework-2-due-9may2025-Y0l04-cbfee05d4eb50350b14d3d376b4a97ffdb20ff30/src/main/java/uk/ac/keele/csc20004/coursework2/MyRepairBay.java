package uk.ac.keele.csc20004.coursework2;

import uk.ac.keele.csc20004.RepairBay;
import java.util.Random;

public class MyRepairBay extends RepairBay {

    private final Random random = new Random();

    /**
     * Constructs a specialized repair bay with a given label.
     *
     * @param label the identifier for the bay (e.g., "Frame")
     */
    public MyRepairBay(String label) {
        super(label);
    }

    /**
     * Performs the repair process based on how depleted the energy is.
     * Includes a randomized delay before the actual repair starts.
     *
     * @param depletedEnergy the energy to replenish, determines repair duration
     */
    public void operate(double depletedEnergy) {
        try {
            int randomDelay = random.nextInt(100); // Random delay up to 99ms
            Thread.sleep(randomDelay);
            long duration = Math.round(depletedEnergy * SimulationParameters.REPAIR_DELAY);
            Thread.sleep(duration);
        } catch (InterruptedException ex) {
            System.err.format("Repair operation interrupted: %s%n", ex);
        }
    }

    @Override
    public String toString() {
        return String.format("%15s", super.toString() + " bay");
    }
}
