package restaurant_simulation;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Main class for the restaurant simulation that extends JPanel to provide graphical representation.
 * This class handles the setup, update logic, and rendering of all restaurant components including
 * chefs, waiters, tables, guests, and workstations.
 *
 * The simulation displays a restaurant layout with a kitchen area on the left and dining area on the right,
 * separated by doors. Various staff members move around performing their duties while guests are seated
 * and served at tables.
 */
public class RestaurantMain extends JPanel {
    /** Width of the simulation window in pixels */
    static final int WIDTH = 1200;
    /** Height of the simulation window in pixels */
    static final int HEIGHT = 640;

    /** X coordinate for sous chef workstation spawn location */
    private static int SOUSSTATIONSPAWNX = 0;
    /** Y coordinate for sous chef workstation spawn location */
    private static int SOUSSTATIONSPAWNY = 250;

    /** X coordinate for garde manger workstation spawn location */
    private static int GARDEMANGERSTATIONSPAWNX = 300;
    /** Y coordinate for garde manger workstation spawn location */
    private static int GARDEMANGERSTATIONSPAWNY = 0;

    /** X coordinate for pastry workstation spawn location */
    private static int PASTRYSTATIONSPAWNX = 0;
    /** Y coordinate for pastry workstation spawn location */
    private static int PASTRYSTATIONSPAWNY = 450;

    /** X coordinate for prep workstation spawn location */
    private static int PREPSTATIONSPAWNX = 0;
    /** Y coordinate for prep workstation spawn location */
    private static int PREPSTATIONSPAWNY = 50;

    /** Collection of all waiters in the restaurant */
    static ArrayList<Waiter> waiters = new ArrayList<Waiter>();
    /** Collection of all tables in the restaurant */
    static ArrayList<Table> tables = new ArrayList<Table>();
    /** The head chef who coordinates all kitchen activities */
    static HeadChef headChef;

    /** The prep chef responsible for ingredient preparation */
    static PrepChef prepChef;
    /** The pastry chef specializing in desserts and baked goods */
    static DishChef pastryChef;
    /** The garde manger chef handling cold dishes and appetizers */
    static DishChef gardemangerChef;
    /** The sous chef assisting with main dishes */
    static DishChef sousChef;
    /** Collection of all dish chefs for easy iteration */
    static ArrayList<DishChef> dishChefList = new ArrayList<DishChef>();

    /** The head waiter who manages table assignments and guest seating */
    static HeadWaiter headWaiter;

    static ChefWorkstation sousStation;
    static ChefWorkstation pastryStation;
    static ChefWorkstation gardemangerStation;
    static ChefWorkstation prepStation;

    /** Collection of all workstations for easy iteration */
    static ArrayList<ChefWorkstation> workstationList = new ArrayList<ChefWorkstation>();

    /** Controller for managing guest instances and their lifecycle */
    static GuestInstanceController guestInstanceController;
    /** Queue system for managing waiting guests */
    static RestaurantQueue restaurantQueue;

    /** Collection of all objects that need to be updated each frame */
    static ArrayList<Updatable> updatables = new ArrayList<>();
    /** Collection of all objects that display progress bars */
    static ArrayList<Progressbarable> progressbarables = new ArrayList<>();

    /**
     * Sets up the entire restaurant simulation by initializing all components including
     * workstations, chefs, tables, waiters, and guest management systems.
     *
     * This method:
     * - Creates the restaurant queue and menu
     * - Initializes all chef workstations at their designated positions
     * - Creates and configures all types of chefs with their specializations
     * - Sets up the head chef to coordinate kitchen activities
     * - Creates tables based on simulation data configuration
     * - Initializes the head waiter and guest controller
     * - Creates waiters and assigns them to specific tables
     * - Establishes listener relationships between components
     */
    static void setupRestaurant(){
        restaurantQueue = new RestaurantQueue();

        // Create menu
        Menu menu1 = getMenu();

        // create workstations
        gardemangerStation = new ChefWorkstation(GARDEMANGERSTATIONSPAWNX, GARDEMANGERSTATIONSPAWNY, 70, 50);
        pastryStation = new ChefWorkstation(PASTRYSTATIONSPAWNX, PASTRYSTATIONSPAWNY, 70, 50);
        sousStation = new ChefWorkstation(SOUSSTATIONSPAWNX, SOUSSTATIONSPAWNY, 70, 50);
        prepStation = new ChefWorkstation(PREPSTATIONSPAWNX, PREPSTATIONSPAWNY, 70, 50);

        workstationList.add(gardemangerStation);
        workstationList.add(pastryStation);
        workstationList.add(sousStation);
        workstationList.add(prepStation);

        // create chefs
        prepChef = new PrepChef(10,100,50, Color.orange, sousStation, pastryStation, gardemangerStation);
        updatables.add(prepChef);
        progressbarables.add(prepChef);

        pastryChef = new DishChef(PASTRYSTATIONSPAWNX + 50,PASTRYSTATIONSPAWNY + 50,70, Color.pink, Enums.ChefType.PASTRY, pastryStation, 400, 300);
        gardemangerChef = new DishChef(GARDEMANGERSTATIONSPAWNX + 50, GARDEMANGERSTATIONSPAWNY + 50, 50, Color.green, Enums.ChefType.GARDEMANGER, gardemangerStation, 400, 300);
        sousChef = new DishChef(SOUSSTATIONSPAWNX + 50, SOUSSTATIONSPAWNY + 50, 50, Color.yellow, Enums.ChefType.SOUS, sousStation, 400, 300);

        dishChefList.add(pastryChef);
        dishChefList.add(sousChef);
        dishChefList.add(gardemangerChef);

        // create headchef with references to all chefs
        headChef = new HeadChef(400,300, sousChef, pastryChef, gardemangerChef);
        updatables.add(headChef);

        // make the headchef a listener to all the chefs
        for (DishChef dishChef : dishChefList){
            updatables.add(dishChef);
            progressbarables.add(dishChef);
            dishChef.addListener(headChef);
        }

        // create tables
        for (int j = 0; j<SimulationData.getInstance().getAMOUNT_OF_TABLES(); j++){

            int lines = SimulationData.getInstance().getROWS_OF_TABLES();
            int distanceBetweenTablesX = (int) ((WIDTH - 540)/( Math.ceil((float) SimulationData.getInstance().getAMOUNT_OF_TABLES()/ (float)lines)));
            int distanceBetweenTablesY;

            if (lines == 2){
                distanceBetweenTablesY = (int) (Math.ceil((float) (HEIGHT-100)/(float)Math.min(lines-1, 1)));

            }
            else{
                distanceBetweenTablesY = (int) (Math.ceil((float) (HEIGHT-100)/(float)lines));
            }
            int tableSpawnX = 540 + Math.floorDiv(j,lines) * distanceBetweenTablesX;
            int tableSpawnY = (j % lines) * distanceBetweenTablesY;

            Table table = new Table(tableSpawnX,tableSpawnY, j);
            tables.add(table);
            updatables.add(table);
        }

        // give headwaiter reference to all tables
        headWaiter = new HeadWaiter(1120, 150, restaurantQueue, tables);
        updatables.add(headWaiter);

        guestInstanceController = new GuestInstanceController(restaurantQueue, headWaiter);
        updatables.add(guestInstanceController);

        // create waiters
        for (int i = 0; i<SimulationData.getInstance().getAMOUNT_OF_WAITERS(); i++){
            Waiter waiter = new Waiter(i);
            waiter.setTables(tables);
            waiter.setChef(headChef);
            waiter.setCurrentMenu(menu1);
            waiters.add(waiter);
            headChef.addListener(waiter);
            updatables.add(waiter);
        }

        // Assign waiter to tables
        int waiterIndex = 0;
        int tablesPerWaiter = (int) Math.ceil((float) SimulationData.getInstance().getAMOUNT_OF_TABLES()/ (float)SimulationData.getInstance().getAMOUNT_OF_WAITERS());
        int tableAssignmentCounter  = 0;

        for (int i = 0; i < tables.size(); i++) {
            tables.get(i).addListener(waiters.get(waiterIndex));
            waiters.get(waiterIndex).addTableIndexToAssignedTableIndexes(i);

            // Each waiter is assigned to tablesPerWaiter of tables before moving to the next waiter
            tableAssignmentCounter  += 1;
            if(tableAssignmentCounter == tablesPerWaiter){
                tableAssignmentCounter  = 0;
                waiterIndex += 1;
            }
        }
    }

    /**
     * Creates and returns the restaurant's menu with various dishes and their prices.
     * Prices are multiplied by the simulation's dish price multiplier for scaling.
     * @return Menu object containing all available menu items with their prices and chef assignments
     */
    private static Menu getMenu() {
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>(Arrays.asList(
                new MenuItem("Fish fingers",
                        70*SimulationData.getInstance().getDISH_PRICE_MULTIPLIER(), Enums.ChefType.SOUS),
                new MenuItem("Meatballs",
                        55*SimulationData.getInstance().getDISH_PRICE_MULTIPLIER(), Enums.ChefType.SOUS),
                new MenuItem("Bouef Bourgoignon",
                        65*SimulationData.getInstance().getDISH_PRICE_MULTIPLIER(), Enums.ChefType.SOUS),
                new MenuItem("Pink Cake",
                        45*SimulationData.getInstance().getDISH_PRICE_MULTIPLIER(), Enums.ChefType.PASTRY), new MenuItem("Garlic bread", 20, Enums.ChefType.GARDEMANGER)));
        return new Menu(menuItems);
    }

    /**
     * Updates all simulation components by calling their update methods.
     * This method is called each frame to advance the simulation state.
     *
     * Updates include:
     * - All registered updatable objects (chefs, waiters, tables, etc.)
     * - Dynamically created guests through the guest instance controller
     *
     * @param delta Time elapsed since last update in milliseconds
     */
    static void update(int delta) {
        for (Updatable obj : updatables) {
            obj.update(delta);
        }

        // Special case for dynamically added guests
        for (Guest guest : guestInstanceController.getGuests()){
            guest.update(delta);
        }
    }

    /**
     * Custom paint method that renders the entire restaurant simulation.
     * @param g Graphics context for rendering
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        setBackground(Color.decode("#286CD4")); // Set the background color to blue

        g.setColor(Color.LIGHT_GRAY); // Set the color for the borderlines
        // draw wall between serving room and kitchen
        g.fillRect(490, 0, 10, getHeight()-5);
        // draw wall between the serving room and the foyer
        g.fillRect(1090, 0, 10, getHeight()-5);
        g.setColor(Color.decode("#964B00")); // Set the color for the outer wall
        g.fillRect(1100, 0, 5, getHeight()-5);


        // Draw kitchen door
        g.setColor(Color.RED);
        g.fillRect(490, 270, 20, 100);
        g.fillRect(1090, 270, 20, 100);

        // Draw tables
        drawTables(g);

        // Draw the waiters
        drawWaiters(g);

        // Draw the Head chef
        if (headChef != null){
            drawHeadChef(g);
        }

        // draw the dishchefs and prepchef
        drawChefs(g);

        drawWorkstations(g);

        if (guestInstanceController != null){
            drawGuests(g);
        }

        if (headWaiter != null){
            drawHeadWaiter(g);
        }

        drawSimulationData(g);
        drawProgressbarables(g);

    }

    /**
     * Draws progress bars for all objects that implement the Progressbarable interface.
     * Progress bars appear above the object showing completion status of current tasks.
     * Only draws progress bars for objects with progress greater than 0.
     *
     * @param g Graphics context for rendering
     */
    static void drawProgressbarables(Graphics g){
        for (Progressbarable progressbarable : progressbarables){
            if (progressbarable.getProgressProportion() == 0){
                continue;
            }

            int startX = progressbarable.getX();
            int width = 50;
            g.setColor(Color.black);
            g.fillRect(startX, progressbarable.getY() - 50, width, 30);
            g.setColor(Color.green);
            g.fillRect(startX + 5, progressbarable.getY() - 45, (int) ((width - 10) * progressbarable.getProgressProportion()), 20);
        }
    }

    /**
     * Draws the simulation data including the current money earned by the restaurant.
     * Displays in the top-left corner of the screen.
     *
     * @param g Graphics context for rendering
     */
    static void drawSimulationData(Graphics g){
        int fontSize = 32;
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        g.drawString(SimulationData.getInstance().getCrowns() + "kr", 10, 30);
    }

    /**
     * Draws all tables in the dining area as circular shapes with table numbers.
     * @param g Graphics context for rendering
     */
    static void drawTables(Graphics g) {
        for (Table table : tables) {
            g.setColor(Color.decode("#6D6B5E"));
            g.fillOval(table.getX(), table.getY(), table.getDiameter(), table.getDiameter()); // Draw circle with diameter of 50 pixels
            g.setColor(Color.decode("#663D22"));
            g.fillOval(table.getX()+3, table.getY()+3, table.getDiameter()-6, table.getDiameter()-6);
            g.setColor(Color.BLACK);
            int fontSize = 16;
            g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
            g.drawString(String.valueOf(table.getTableNumber()), table.getX() + table.getDiameter()/2,  table.getY() + table.getDiameter()/2);
        }
    }

    /**
     * Draws the head waiter as a purple circle with black border..
     *
     * @param g Graphics context for rendering
     */
    static void drawHeadWaiter(Graphics g){
        g.setColor(Color.BLACK);
        g.fillOval(headWaiter.getX(), headWaiter.getY(), headWaiter.getDiameter(), headWaiter.getDiameter());
        g.setColor(Color.decode("#A020F0"));
        g.fillOval(headWaiter.getX()+7, headWaiter.getY()+7, headWaiter.getDiameter()-14, headWaiter.getDiameter()-14); // Draw circle with diameter of 50 pixels
        g.setColor(Color.BLACK);
    }

    /**
     * Draws all guests currently in the restaurant.
     * Each guest is rendered as a colored circle with black border.*
     * @param g Graphics context for rendering
     */
    static void drawGuests(Graphics g){
        for (Guest guest: guestInstanceController.getGuests()){
            g.setColor(Color.BLACK);
            g.fillOval(guest.getX(), guest.getY(), guest.getDiameter(), guest.getDiameter());
            g.setColor(guest.getColor());
            g.fillOval(guest.getX()+2, guest.getY()+2, guest.getDiameter()-4, guest.getDiameter()-4); // Draw circle with diameter of 50 pixels
            g.setColor(Color.BLACK);
        }
    }

    /**
     * Draws all chef workstations as dark gray rectangles.
     * Workstations represent the cooking areas where chefs perform their tasks.
     *
     * @param g Graphics context for rendering
     */
    static void drawWorkstations(Graphics g) {
        for (ChefWorkstation workstation : workstationList) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(workstation.getX(), workstation.getY(), workstation.getWidth(), workstation.getHeight());
        }
    }

    /**
     * Draws all waiters as white circles with black borders.
     * Also displays the current action text above each waiter to show their current activity.
     *
     * @param g Graphics context for rendering
     */
    static  void drawWaiters(Graphics g){
        for (Waiter waiter : waiters) {
            g.setColor(Color.BLACK);
            g.fillOval(waiter.getX(), waiter.getY(), waiter.getDiameter(), waiter.getDiameter()); // Draw circle with diameter of 50 pixels
            g.setColor(Color.WHITE);
            g.fillOval(waiter.getX()+7, waiter.getY()+7, waiter.getDiameter()-14, waiter.getDiameter()-14); // Draw circle with diameter of 50 pixels
            g.setColor(Color.BLACK);
            g.drawString(waiter.getActionText(), waiter.getX()+20,  waiter.getY()-10);
        }
    }

    /**
     * Draws the head chef as a blue circle with black border and "Head Chef" label.
     * @param g Graphics context for rendering
     */
    static void drawHeadChef(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(headChef.getX(), headChef.getY(), headChef.getDiameter(), headChef.getDiameter()); // Draw circle with diameter of 50 pixels
        g.setColor(Color.BLUE);
        g.fillOval(headChef.getX()+7, headChef.getY()+7, headChef.getDiameter()-14, headChef.getDiameter()-14);
        g.setColor(Color.BLACK);
        g.drawString("Head Chef", headChef.getX()+30,  headChef.getY()+35);
    }

    /**
     * Draws all chefs including dish chefs and the prep chef.
     * Chef type labels are displayed above each chef
     * The prep chef is rendered separately with an orange color and "Prep" label.
     *
     * @param g Graphics context for rendering
     */
    static void drawChefs(Graphics g){
        for(DishChef dishChef : dishChefList){
            g.setColor(Color.black);
            g.fillOval(dishChef.getX(), dishChef.getY(), dishChef.getDiameter(), dishChef.getDiameter());
            g.setColor(dishChef.getBodyColor());
            g.fillOval(dishChef.getX()+7, dishChef.getY()+7, dishChef.getDiameter()-14, dishChef.getDiameter()-14);
            g.setColor(Color.BLACK);
            g.drawString(dishChef.getChefType().name(), dishChef.getX(), dishChef.getY());
        }

        if (prepChef != null){
            g.setColor(Color.BLACK);
            g.setColor(Color.black);
            g.fillOval(prepChef.getX(), prepChef.getY(), prepChef.getDiameter(), prepChef.getDiameter());
            g.setColor(prepChef.getBodyColor());
            g.fillOval(prepChef.getX()+7, prepChef.getY()+7, prepChef.getDiameter()-14, prepChef.getDiameter()-14);
            g.setColor(Color.BLACK);
            g.drawString("Prep", prepChef.getX()+25, prepChef.getY()+60);
        }
    }


}