package restaurant_simulation;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RestaurantMain extends JPanel {

    static final int WIDTH = 1200;
    static final int HEIGHT = 640;

    private static int SOUSSTATIONSPAWNX = 0;
    private static int SOUSSTATIONSPAWNY = 250;

    private static int GARDEMANGERSTATIONSPAWNX = 300;
    private static int GARDEMANGERSTATIONSPAWNY = 0;

    private static int PASTRYSTATIONSPAWNX = 0;
    private static int PASTRYSTATIONSPAWNY = 450;

    private static int PREPSTATIONSPAWNX = 0;
    private static int PREPSTATIONSPAWNY = 50;


    static ArrayList<Waiter> waiters = new ArrayList<Waiter>();
    static ArrayList<Table> tables = new ArrayList<Table>();
    static HeadChef headChef;

    static int delta = 33;
    // In here all objects that are needed for operating the restaurant should be created.
    // This is initialisation and determines the initial state of the program.

    static PrepChef prepChef;
    static DishChef pastryChef;
    static DishChef gardemangerChef;
    static DishChef sousChef;
    static ArrayList<DishChef> dishChefList = new ArrayList<DishChef>();

    static HeadWaiter headWaiter;

    static ChefWorkstation sousStation;
    static ChefWorkstation pastryStation;
    static ChefWorkstation gardemangerStation;
    static ChefWorkstation prepStation;
    static ArrayList<ChefWorkstation> workstationList = new ArrayList<ChefWorkstation>();

    static GuestInstanceController guestInstanceController;
    static RestaurantQueue restaurantQueue;

    static ArrayList<Updatable> updatables = new ArrayList<>();
    static ArrayList<Progressbarable> progressbarables = new ArrayList<>();

    static void setupRestaurant(){
        restaurantQueue = new RestaurantQueue();

        // Create menu
        // Might be moved to another file
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>(Arrays.asList(new MenuItem("Fish fingers", 210, Enums.ChefType.SOUS),
                new MenuItem("Meatballs", 190, Enums.ChefType.SOUS), new MenuItem("Bouef Bourgoignon", 200, Enums.ChefType.SOUS),
                new MenuItem("Pink Cake", 125, Enums.ChefType.PASTRY), new MenuItem("Garlic bread", 90, Enums.ChefType.GARDEMANGER)));
        Menu menu1 = new Menu(menuItems);

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

            int lines = 4;
            int distanceBetweenTablesX = (int) ((WIDTH - 540)/( Math.ceil((float) SimulationData.getInstance().getAMOUNT_OF_TABLES()/ (float)lines)));
            int distanceBetweenTablesY = (int) (Math.ceil((float) HEIGHT/(float)lines));
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

    // Contains the simulation logic, should probably be broken into smaller pieces as the program expands
    static void update(int delta) {
        for (Updatable obj : updatables) {
            obj.update(delta);
        }

        // Special case for dynamically added guests
        for (Guest guest : guestInstanceController.getGuests()){
            guest.update(delta);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


        setBackground(Color.decode("#286CD4")); //  // Set the background color to light yellow

        g.setColor(Color.DARK_GRAY); // Set the color for the border lines
        g.drawRect(500, 0, 600, getHeight() - 5);
        //g.drawRect(800, 0, getWidth() - 5, getHeight() - 5);
        g.setColor(Color.BLACK);
        g.drawRect(500, 0, 695, getHeight() - 5);

        // Draw kitchen door
        g.setColor(Color.DARK_GRAY);
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
        drawProgressbarables(g);

        drawWorkstations(g);

        if (guestInstanceController != null){
            drawGuests(g);
        }


        if (headWaiter != null){
            drawHeadWaiter(g);
        }

        drawSimulationData(g);
        // MORE CODE HERE
    }

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

    static void drawSimulationData(Graphics g){
        int fontSize = 32;
        g.setFont(new Font("TimesRoman", Font.PLAIN, fontSize));
        g.drawString(SimulationData.getInstance().getCrowns() + "kr", 10, 50);
    }
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

    static void drawHeadWaiter(Graphics g){
        g.setColor(Color.BLACK);
        g.fillOval(headWaiter.getX(), headWaiter.getY(), headWaiter.getDiameter(), headWaiter.getDiameter());
        g.setColor(Color.decode("#A020F0"));
        g.fillOval(headWaiter.getX()+7, headWaiter.getY()+7, headWaiter.getDiameter()-14, headWaiter.getDiameter()-14); // Draw circle with diameter of 50 pixels
        g.setColor(Color.BLACK);
    }

    static void drawGuests(Graphics g){
        for (Guest guest: guestInstanceController.getGuests()){
            g.setColor(Color.BLACK);
            g.fillOval(guest.getX(), guest.getY(), guest.getDiameter(), guest.getDiameter());
            g.setColor(guest.getColor());
            g.fillOval(guest.getX()+2, guest.getY()+2, guest.getDiameter()-4, guest.getDiameter()-4); // Draw circle with diameter of 50 pixels
            g.setColor(Color.BLACK);
        }
    }

    static void drawWorkstations(Graphics g) {
        for (ChefWorkstation workstation : workstationList) {
            g.setColor(Color.DARK_GRAY);
            g.fillRect(workstation.getX(), workstation.getY(), workstation.getWidth(), workstation.getHeight());
        }
    }

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

    static void drawHeadChef(Graphics g) {
        g.setColor(Color.BLACK);
        g.fillOval(headChef.getX(), headChef.getY(), headChef.getDiameter(), headChef.getDiameter()); // Draw circle with diameter of 50 pixels
        g.setColor(Color.BLUE);
        g.fillOval(headChef.getX()+7, headChef.getY()+7, headChef.getDiameter()-14, headChef.getDiameter()-14);
        g.setColor(Color.BLACK);
        g.drawString("Head Chef", headChef.getX()+30,  headChef.getY()+35);

    }

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

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Restaurant Simulation");
        frame.setSize(WIDTH, HEIGHT); // Set window size
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        // Add the custom panel (with circles) to the frame
        RestaurantMain panel = new RestaurantMain();
        frame.add(panel);

        // Display the window
        frame.setVisible(true);

        // Setup for the restaurant
        setupRestaurant();

        while (true) {
            try {
                Thread.sleep(delta); // With the goal of having about 30 fps.
            } catch (Exception threadException) {
                System.out.println("Sleep exception: " + threadException.getMessage());
            }
            update(delta*5);
            panel.repaint();
        }
    }
}