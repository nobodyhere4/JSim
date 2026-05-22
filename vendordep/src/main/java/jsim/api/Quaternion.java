package jsim.api;

import edu.wpi.first.math.geometry.Translation3d;

/**
 * Quaternion helper used by the Fuel2026 example path.
 */
public class Quaternion {
    /** Scalar component. */
    public final double w;
    /** X component. */
    public final double x;
    /** Y component. */
    public final double y;
    /** Z component. */
    public final double z;

    /**
     * Creates a quaternion from its four components.
     *
     * @param w scalar component
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Creates a quaternion from an axis and angle in radians.
     *
     * @param axis rotation axis
     * @param angleRad rotation angle in radians
     * @return quaternion representing the rotation
     */
    public static Quaternion fromAxisAngle(Translation3d axis, double angleRad) {
        double halfAngle = angleRad * 0.5;
        double sinHalf = Math.sin(halfAngle);
        double magnitude = axis == null
                ? 0.0
                : Math.sqrt(axis.getX() * axis.getX() + axis.getY() * axis.getY() + axis.getZ() * axis.getZ());
        if (magnitude == 0.0) {
            return new Quaternion(Math.cos(halfAngle), 0.0, 0.0, 0.0);
        }

        return new Quaternion(
                Math.cos(halfAngle),
                axis.getX() / magnitude * sinHalf,
                axis.getY() / magnitude * sinHalf,
                axis.getZ() / magnitude * sinHalf);
    }

    /**
     * Multiplies this quaternion by another quaternion.
     *
     * @param o the right-hand operand
     * @return the product quaternion
     */
    public Quaternion multiply(Quaternion o) {
        return new Quaternion(
                w * o.w - x * o.x - y * o.y - z * o.z,
                w * o.x + x * o.w + y * o.z - z * o.y,
                w * o.y - x * o.z + y * o.w + z * o.x,
                w * o.z + x * o.y - y * o.x + z * o.w);
    }

    /**
     * Rotates a vector by this quaternion.
     *
     * @param v vector to rotate
     * @return rotated vector
     */
    public Translation3d rotate(Translation3d v) {
        Quaternion vectorQuaternion = new Quaternion(0.0, v.getX(), v.getY(), v.getZ());
        Quaternion rotated = this.multiply(vectorQuaternion).multiply(conjugate());
        return new Translation3d(rotated.x, rotated.y, rotated.z);
    }

    private Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }
}
