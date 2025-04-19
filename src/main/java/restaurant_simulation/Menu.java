package restaurant_simulation;

import java.util.ArrayList;
import java.util.Random;

public class Menu {
    ArrayList<MenuItem> items;
    Menu(ArrayList<MenuItem> items){
        this.items = items;
    }

    MenuItem selectRandomItem(){
        Random random = new Random();
        return items.get(random.nextInt(items.size()));
    }
}
