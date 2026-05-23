package jsim.api;

/**
 * Thin wrapper around WPILib's Rotation3d for API compatibility.
 */
public class Rotation3d extends edu.wpi.first.math.geometry.Rotation3d {

    /**
     * Creates a zero rotation.
     */
    public Rotation3d() {
        super();
    }

    /**
     * Creates a rotation.
     *
     * @param roll roll angle
     * @param pitch pitch angle
     * @param yaw yaw angle
     */
    public Rotation3d(double roll, double pitch, double yaw) {
        super(roll, pitch, yaw);
    }

    /**
     * Creates a rotation from a quaternion.
     *
     * @param quaternion source quaternion
     */
    public Rotation3d(edu.wpi.first.math.geometry.Quaternion quaternion) {
        super(quaternion);
    }
}
