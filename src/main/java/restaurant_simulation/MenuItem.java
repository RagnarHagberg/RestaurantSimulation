package restaurant_simulation;

public class MenuItem {

    private String courseName;
    private int price;
    Enums.ChefType targetChef;

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
