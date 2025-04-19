package restaurant_simulation;

public class ChefWorkstation {
    private int ingredients;

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
