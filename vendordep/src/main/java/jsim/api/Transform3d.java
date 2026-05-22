package jsim.api;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * A 3D rigid transform composed of a translation and a rotation.
 */
public class Transform3d {
    public final Translation3d translation;
    public final Rotation3d rotation;

    public Transform3d(Translation3d translation, Rotation3d rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }

    public Rotation3d getRotation() {
        return rotation;
    }
}
