package restaurant_simulation;

/**
 * Represents a single dish in the restaurant simulation.
 * <p>
 * A dish is associated with a course name and the table that ordered it.
 */
public class Dish {
    String courseName;
    private int tableOriginIndex;

    /**
     * Constructs a Dish with the specified course name and originating table index.
     *
     * @param courseName       the name of the dish/course
     * @param tableOriginIndex the index of the table that ordered the dish
     */
    Dish(String courseName, int tableOriginIndex){
        this.courseName = courseName;
        this.tableOriginIndex = tableOriginIndex;
    }

    public int getTableOriginIndex() {
        return tableOriginIndex;
    }
}
