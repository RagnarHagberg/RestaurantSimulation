package restaurant_simulation;

public class ChefWorkstation extends CanvasObject {
    private int ingredients;
    private int width;
    private int height;

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

    public void addIngredients(int addAmount){
        this.ingredients += addAmount;
    }

    public void setIngredients(int ingredients) {
        this.ingredients = ingredients;
    }
}
