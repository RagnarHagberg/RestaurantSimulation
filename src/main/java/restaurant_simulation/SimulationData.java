package restaurant_simulation;

/**
 * Singleton class that holds simulation configuration data and state.
 */
public final class SimulationData {

    private static SimulationData INSTANCE;
    private int crowns = 0;

    // Configuration parameters
    private int GUESTS_PER_TABLE = 2;
    private int AMOUNT_OF_TABLES = 2;
    private int ROWS_OF_TABLES = 1;
    private int AMOUNT_OF_WAITERS = 2;
    private int DISH_PRICE_MULTIPLIER = 3;
    private int CHEF_SPEED_MULTIPLIER = 3;


    /**
     * Private constructor to enforce singleton pattern.
     */
    private SimulationData() {
    }

    /**
     * Returns the singleton instance of {@code SimulationData}.
     * If it does not exist, it is created.
     *
     * @return the singleton instance
     */
    public static SimulationData getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SimulationData();
        }
        return INSTANCE;
    }

    public int getCrowns() {
        return crowns;
    }

    /**
     * Adds the specified amount to the current crown total.
     *
     * @param crowns the number of crowns to add
     */
    public void addCrowns(int crowns) {
        this.crowns += crowns;
    }

    public int getGUESTS_PER_TABLE() {
        return GUESTS_PER_TABLE;
    }

    public void setGUESTS_PER_TABLE(int GUESTS_PER_TABLE) {
        this.GUESTS_PER_TABLE = GUESTS_PER_TABLE;
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

    public int getROWS_OF_TABLES() {
        return ROWS_OF_TABLES;
    }

    public void setROWS_OF_TABLES(int ROWS_OF_TABLES) {
        this.ROWS_OF_TABLES = ROWS_OF_TABLES;
    }

    public int getDISH_PRICE_MULTIPLIER() {
        return DISH_PRICE_MULTIPLIER;
    }

    public void setDISH_PRICE_MULTIPLIER(int DISH_PRICE_MULTIPLIER) {
        this.DISH_PRICE_MULTIPLIER = DISH_PRICE_MULTIPLIER;
    }

    public int getCHEF_SPEED_MULTIPLIER() {
        return CHEF_SPEED_MULTIPLIER;
    }

    public void setCHEF_SPEED_MULTIPLIER(int CHEF_SPEED_MULTIPLIER) {
        this.CHEF_SPEED_MULTIPLIER = CHEF_SPEED_MULTIPLIER;
    }
}
