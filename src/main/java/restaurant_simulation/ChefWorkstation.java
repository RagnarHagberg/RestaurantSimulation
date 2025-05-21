package restaurant_simulation;

/**
 * Represents a chef's workstation in the restaurant simulation.
 * <p>
 * Each workstation has a defined width and height, and tracks the amount of ingredients available.
 */
public class ChefWorkstation extends CanvasObject {
    private int ingredients;
    private int width;
    private int height;

    /**
     * Constructs a new ChefWorkstation at the specified location with given dimensions.
     *
     * @param spawnX the x-coordinate of the workstation
     * @param spawnY the y-coordinate of the workstation
     * @param width  the width of the workstation
     * @param height the height of the workstation
     */
    ChefWorkstation(int spawnX, int spawnY, int width, int height) {
        super(spawnX, spawnY);
        this.width = width;
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getIngredients() {
        return this.ingredients;
    }

    /**
     * Adds the specified amount of ingredients to the workstation.
     *
     * @param addAmount the number of ingredients to add
     */
    public void addIngredients(int addAmount){
        this.ingredients += addAmount;
    }

    public void setIngredients(int ingredients) {
        this.ingredients = ingredients;
    }
}
