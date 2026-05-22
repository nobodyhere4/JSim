package jsim.api;

import edu.wpi.first.math.geometry.Translation3d;

/**
 * Thin wrapper around WPILib's Quaternion with a few helper utilities.
 */
public class Quaternion extends edu.wpi.first.math.geometry.Quaternion {

    /**
     * Creates an identity quaternion.
     */
    public Quaternion() {
        super();
    }

    /**
     * Creates a quaternion from its four components.
     *
     * @param w scalar component
     * @param x x component
     * @param y y component
     * @param z z component
     */
    public Quaternion(double w, double x, double y, double z) {
        super(w, x, y, z);
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
                getW() * o.getW() - getX() * o.getX() - getY() * o.getY() - getZ() * o.getZ(),
                getW() * o.getX() + getX() * o.getW() + getY() * o.getZ() - getZ() * o.getY(),
                getW() * o.getY() - getX() * o.getZ() + getY() * o.getW() + getZ() * o.getX(),
                getW() * o.getZ() + getX() * o.getY() - getY() * o.getX() + getZ() * o.getW());
    }

    /**
     * Rotates a vector by this quaternion.
     *
     * @param v vector to rotate
     * @return rotated vector
     */
    public Translation3d rotate(Translation3d v) {
        Quaternion vectorQuaternion = new Quaternion(0.0, v.getX(), v.getY(), v.getZ());
        Quaternion rotated = this.multiply(vectorQuaternion).multiply(computeConjugate());
        return new Translation3d(rotated.getX(), rotated.getY(), rotated.getZ());
    }

    private Quaternion computeConjugate() {
        return new Quaternion(getW(), -getX(), -getY(), -getZ());
    }
}
