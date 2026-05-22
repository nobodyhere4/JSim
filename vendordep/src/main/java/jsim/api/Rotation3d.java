package jsim.api;

/**
 * Lightweight 3D rotation value.
 */
public class Rotation3d {
    /** Roll angle. */
    public final double roll, pitch, yaw;

    /**
     * Creates a rotation.
     *
     * @param roll roll angle
     * @param pitch pitch angle
     * @param yaw yaw angle
     */
    public Rotation3d(double roll, double pitch, double yaw) {
        this.roll = roll;
        this.pitch = pitch;
        this.yaw = yaw;
    }
}
