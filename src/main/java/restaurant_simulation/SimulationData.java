package restaurant_simulation;

public final class SimulationData {

    private static SimulationData INSTANCE;
    private int crowns = 0;

    private SimulationData() {
    }

    public static SimulationData getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new SimulationData();
        }

        return INSTANCE;
    }

    public int getCrowns() {
        return crowns;
    }

    public void addCrowns(int crowns) {
        this.crowns += crowns;
    }


}
