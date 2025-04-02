package restaurant_simulation;

public class Dish {
    String courseName;
    private int tableOriginIndex;

    Dish(String courseName, int tableOriginIndex){
        this.courseName = courseName;
        this.tableOriginIndex = tableOriginIndex;
    }

    public int getTableOriginIndex() {
        return tableOriginIndex;
    }
}
