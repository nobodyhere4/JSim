package jsim.api;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * A 3D rigid transform composed of a translation and a rotation.
 */
public class Transform3d {
    /** Translation component of the transform. */
    public final Translation3d translation;
    /** Rotation component of the transform. */
    public final Rotation3d rotation;

    /**
     * Creates a rigid transform from a translation and rotation.
     *
     * @param translation translation component
     * @param rotation rotation component
     */
    public Transform3d(Translation3d translation, Rotation3d rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    /**
     * Returns the rotation component.
     *
     * @return the stored rotation
     */
    public Rotation3d getRotation() {
        return rotation;
    }
}
