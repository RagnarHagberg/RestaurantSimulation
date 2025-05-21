package restaurant_simulation;

/**
 * Represents a base object with a position on the canvas.
 * <p>
 * Stores both the current position and the original spawn position.
 * Serves as a superclass for visual or movable entities in the simulation.
 */
public class CanvasObject {
    private int x;
    private int y;
    private int spawnX;
    private int spawnY;

    /**
     * Constructs a CanvasObject at the given spawn location.
     * The current position is initialized to the spawn location.
     *
     * @param spawnX initial and spawn x-coordinate
     * @param spawnY initial and spawn y-coordinate
     */
    CanvasObject(int spawnX, int spawnY) {
        this.spawnX = spawnX;
        this.spawnY = spawnY;
        this.x = spawnX;
        this.y = spawnY;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the x-coordinate at which the object was originally spawned
     */
    public int getSpawnX() {
        return spawnX;
    }

    /**
     * @return the y-coordinate at which the object was originally spawned
     */
    public int getSpawnY() {
        return spawnY;
    }
}
