package restaurant_simulation;

/**
 * An interface for character that process a function for a specific time,
 * that can be displayed with a progressbar.
 */
public interface Progressbarable {
    int getX();
    int getY();
    float getProgressProportion();
}
