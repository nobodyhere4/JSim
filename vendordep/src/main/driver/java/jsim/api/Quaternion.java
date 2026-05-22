package jsim.api;

/**
 * Lightweight quaternion value.
 */
public class Quaternion {
    public final double w;
    public final double x;
    public final double y;
    public final double z;

    public Quaternion(double w, double x, double y, double z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static Quaternion fromAxisAngle(Vector3 axis, double angleRad) {
        double halfAngle = angleRad * 0.5;
        double sinHalf = Math.sin(halfAngle);
        double magnitude = axis == null ? 0.0 : axis.norm();
        if (magnitude == 0.0) {
            return new Quaternion(Math.cos(halfAngle), 0.0, 0.0, 0.0);
        }

        return new Quaternion(
                Math.cos(halfAngle),
                axis.x / magnitude * sinHalf,
                axis.y / magnitude * sinHalf,
                axis.z / magnitude * sinHalf);
    }

    public Quaternion multiply(Quaternion o) {
        return new Quaternion(
                w * o.w - x * o.x - y * o.y - z * o.z,
                w * o.x + x * o.w + y * o.z - z * o.y,
                w * o.y - x * o.z + y * o.w + z * o.x,
                w * o.z + x * o.y - y * o.x + z * o.w);
    }

    public Vector3 rotate(Vector3 v) {
        Quaternion vectorQuaternion = new Quaternion(0.0, v.x, v.y, v.z);
        Quaternion rotated = this.multiply(vectorQuaternion).multiply(conjugate());
        return new Vector3(rotated.x, rotated.y, rotated.z);
    }

    private Quaternion conjugate() {
        return new Quaternion(w, -x, -y, -z);
    }
}
