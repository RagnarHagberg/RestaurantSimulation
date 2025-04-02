package restaurant_simulation;

public class MenuItem {

    String courseName;
    double price;
    Enums.TargetChef targetChef;

    MenuItem(String courseName, double price, Enums.TargetChef targetChef){
        this.courseName = courseName;
        this.price = price;
        this.targetChef = targetChef;
    }
}
