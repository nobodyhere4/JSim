package jsim.api;

/**
 * Lightweight 3D pose value.
 */
public class Pose3d {
    /** X position. */
    public final double x, y, z, roll, pitch, yaw;

    /**
     * Creates a pose.
     *
     * @param x x position
     * @param y y position
     * @param z z position
     * @param roll roll angle
     * @param pitch pitch angle
     * @param yaw yaw angle
     */
    public Pose3d(double x, double y, double z, double roll, double pitch, double yaw) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
