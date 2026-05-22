package jsim.api;

/**
 * Lightweight 3D translation value.
 */
public class Translation3d {
    /** X position. */
    public final double x, y, z;

    /**
     * Creates a translation.
     *
     * @param x x position
     * @param y y position
     * @param z z position
     */
    public Translation3d(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
