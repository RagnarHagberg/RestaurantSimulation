package restaurant_simulation;

import java.util.ArrayList;
import java.util.Random;

/**
 * Represents a menu that guests can order dishes from.
 *
 */
public class Menu {
    ArrayList<MenuItem> items;
    /**
     * Constructs a new Menu with the specified menuItems.
     *
     * @param items the MenuItems in the menu.
     */

    Menu(ArrayList<MenuItem> items){
        this.items = items;
    }

    /**
     * Returns a randomly selected item from the saved data in the menu.
     * @return a randomly selected menuItem from the menu.
     */
    MenuItem selectRandomItem(){
        Random random = new Random();
        return items.get(random.nextInt(items.size()));
    }
}
