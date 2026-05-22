package jsim.api;

import edu.wpi.first.math.geometry.Translation3d;

/**
 * Small 3x3 matrix helper used by the Fuel2026 example path.
 */
public class Matrix3 {
    public final double m00;
    public final double m01;
    public final double m02;
    public final double m10;
    public final double m11;
    public final double m12;
    public final double m20;
    public final double m21;
    public final double m22;

    public Matrix3() {
        this(1.0, 0.0, 0.0,
             0.0, 1.0, 0.0,
             0.0, 0.0, 1.0);
    }

    public Matrix3(
            double m00, double m01, double m02,
            double m10, double m11, double m12,
            double m20, double m21, double m22) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
    }

    public static Matrix3 identity() {
        return new Matrix3();
    }

    public Translation3d multiply(Translation3d v) {
        return new Translation3d(
                m00 * v.x + m01 * v.y + m02 * v.z,
                m10 * v.x + m11 * v.y + m12 * v.z,
                m20 * v.x + m21 * v.y + m22 * v.z);
    }
}
