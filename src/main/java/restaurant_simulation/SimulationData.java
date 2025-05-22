package restaurant_simulation;

public final class SimulationData {

    private static SimulationData INSTANCE;
    private int crowns = 0;

    private int GUESTS_PER_TABLE = 6;

    private int AMOUNT_OF_TABLES = 8;

    private int AMOUNT_OF_WAITERS = 3;

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


    public int getGUESTS_PER_TABLE() {
        return GUESTS_PER_TABLE;
    }


    public int getAMOUNT_OF_WAITERS() {
        return AMOUNT_OF_WAITERS;
    }

    public void setAMOUNT_OF_WAITERS(int AMOUNT_OF_WAITERS) {
        this.AMOUNT_OF_WAITERS = AMOUNT_OF_WAITERS;
    }

    public int getAMOUNT_OF_TABLES() {
        return AMOUNT_OF_TABLES;
    }

    public void setAMOUNT_OF_TABLES(int AMOUNT_OF_TABLES) {
        this.AMOUNT_OF_TABLES = AMOUNT_OF_TABLES;
    }
}
