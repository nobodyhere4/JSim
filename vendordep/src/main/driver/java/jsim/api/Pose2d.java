package jsim.api;

/**
 * Lightweight 2D pose value.
 */
public class Pose2d {
    /** X position. */
    public final double x, y, theta;

    /** Y position. */

    /** Heading angle. */
    /**
     * Creates a pose.
     *
     * @param x x position
     * @param y y position
     * @param theta heading angle
     */
    public Pose2d(double x, double y, double theta) {
        this.x = x;
        this.y = y;
        this.theta = theta;
    }
}
