package restaurant_simulation;

public class MenuItem {

    String courseName;
    double price;
    Enums.ChefType targetChef;

    MenuItem(String courseName, double price, Enums.ChefType targetChef){
        this.courseName = courseName;
        this.price = price;
        this.targetChef = targetChef;
    }

    Enums.ChefType getTargetChef() {
        return this.targetChef;
    }
}
