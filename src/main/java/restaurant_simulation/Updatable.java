package restaurant_simulation;

/**
 * Class for all objects that has an update method that needs to be run every frame of the simulation.
 */
public interface Updatable {
    void update(int delta);
}
