package jsim.api;

/**
 * Lightweight 3D vector value.
 */
public class Vector3 {
    public final double x;
    public final double y;
    public final double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double norm() {
        return Math.sqrt(x * x + y * y + z * z);
    }
}
