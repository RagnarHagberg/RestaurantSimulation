package restaurant_simulation;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class RestaurantMain extends JPanel {

    static ArrayList<Waiter> waiters = new ArrayList<Waiter>();
    static ArrayList<Table> tables = new ArrayList<Table>();
    static HeadChef headChef;

    static int delta = 33;
    // In here all objects that are needed for operating the restaurant should be created.
    // This is initialisation and determines the initial state of the program.

    static Chef prepChef;
    static Chef pastryChef;
    static Chef gardemangerChef;
    static Chef sousChef;

    static ArrayList<Chef> chefList = new ArrayList<Chef>();

    static void setupRestaurant(){

        // Create menu
        // Might be moved to another file
        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>(Arrays.asList(new MenuItem("Fish fingers", 10, Enums.ChefType.SOUS),
                new MenuItem("Meatballs", 5, Enums.ChefType.SOUS), new MenuItem("Bouef Bourgoignon", 12, Enums.ChefType.SOUS),
                new MenuItem("Pink Cake", 7, Enums.ChefType.PASTRY), new MenuItem("Garlic bread", 3, Enums.ChefType.GARDEMANGER)));
        Menu menu1 = new Menu(menuItems);


        // create chefs
        prepChef = new Chef(10,100,50,Color.orange, Enums.ChefType.PREP);
        pastryChef = new Chef(10,400,70, Color.pink, Enums.ChefType.PASTRY);
        gardemangerChef = new Chef(200, 100, 50, Color.green, Enums.ChefType.GARDEMANGER);
        sousChef = new Chef(200, 400, 50, Color.yellow, Enums.ChefType.SOUS);

        chefList.add(prepChef);
        chefList.add(pastryChef);
        chefList.add(sousChef);
        chefList.add(gardemangerChef);

        // create headchef with references to all chefs
        headChef = new HeadChef(400,300, prepChef, sousChef, pastryChef, gardemangerChef);

        // make the headchef a listener to all the chefs
        for (Chef chef : chefList){
            chef.addListener(headChef);
        }

        // create tables
        for (int j = 0; j<6; j++){
            Table table = new Table(j);
            tables.add(table);
        }

        // create waiters
        for (int i = 0; i<3; i++){
            Waiter waiter = new Waiter(i);
            waiter.setTables(tables);
            waiter.setChef(headChef);
            waiter.setCurrentMenu(menu1);
            waiters.add(waiter);
            headChef.addListener(waiter);
        }


        // Assign waiter to tables
        int waiterIndex = 0;
        for (int i = 0; i < tables.size(); i++) {
            tables.get(i).addListener(waiters.get(waiterIndex));

            waiters.get(waiterIndex).addTableIndexToAssignedTableIndexes(i);

            // Each waiter is assigned to two tables before moving to the next waiter
            if ((i + 1) % 2 == 0) {
                waiterIndex = (waiterIndex + 1) % waiters.size();
            }
        }
    }

    // Contains the simulation logic, should probably be broken into smaller pieces as the program expands
    static void update(int delta) {

        // Update all visible objects
        for (Waiter w : waiters) {
            w.update(delta);
        }

        for (Table t : tables) {
            t.update(delta);
        }

        for (Chef chef: chefList){
            chef.update(delta);
        }


        if (headChef != null){
            headChef.update(delta);
        }

        // ... similar updates for all other agents in the simulation.
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        setBackground(new Color(255, 245, 158, 184)); //  // Set the background color to light yellow

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
        drawHeadChef(g);

        drawChefs(g);
        // MORE CODE HERE
    }

    static void drawTables(Graphics g) {
        for (Table table : tables) {
            g.setColor(Color.RED);
            g.fillOval(table.getX(), table.getY(), table.getDiameter(), table.getDiameter()); // Draw circle with diameter of 50 pixels
            g.setColor(Color.WHITE);
            g.fillOval(table.getX()+3, table.getY()+3, table.getDiameter()-6, table.getDiameter()-6);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(table.getTableNumber()), table.getX()+30,  table.getY()+35);
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

        for(Chef chef : chefList){
            g.setColor(Color.black);
            g.fillOval(chef.getX(), chef.getY(), chef.getDiameter(), chef.getDiameter());
            g.setColor(chef.getBodyColor());
            g.fillOval(chef.getX()+7, chef.getY()+7, chef.getDiameter()-14, chef.getDiameter()-14);
            g.setColor(Color.BLACK);
            g.drawString(chef.getChefType().name(),chef.getX()+25,chef.getY()+60);

            // Draw progress bar

            if (chef.isCooking()) {
                int startX = chef.getX();
                int width = 50;
                g.setColor(Color.black);
                g.fillRect(startX, chef.getY() - 50, width, 30);
                g.setColor(Color.green);
                g.fillRect(startX + 5, chef.getY() - 45, (int) ((width - 10) * chef.getProgressProportion()), 20);
            }
        }


    }

    public static void main(String[] args) {
        // Create the frame
        JFrame frame = new JFrame("Restuarant Simulation");
        frame.setSize(1200, 640); // Set window size
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
            update(delta);
            panel.repaint();
        }
    }
}