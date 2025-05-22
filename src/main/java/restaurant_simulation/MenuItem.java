package restaurant_simulation;


/**
 * Represents an item in the menu.
 * The item contains data about what chef makes it, the price and the name of the dish.
 */
public class MenuItem {

    private String courseName;
    private int price;
    Enums.ChefType targetChef;

    /**
     * Constructs a new MenuItem with the specified name, price and targetChef.
     *
     * @param courseName the name of the dish.
     * @param price the price of the dish
     * @param targetChef the chef that makes the type of dish.
     */
    MenuItem(String courseName, int price, Enums.ChefType targetChef){
        this.courseName = courseName;
        this.price = price;
        this.targetChef = targetChef;
    }

    Enums.ChefType getTargetChef() {
        return this.targetChef;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getPrice() {
        return price;
    }
}
