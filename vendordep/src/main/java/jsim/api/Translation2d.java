package jsim.api;

/**
 * Lightweight 2D translation value.
 */
public class Translation2d {
    /** X position. */
    public final double x, y;

    /**
     * Creates a translation.
     *
     * @param x x position
     * @param y y position
     */
    public Translation2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
