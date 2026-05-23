package jsim.api;

/**
 * Thin wrapper around WPILib's Transform3d for API compatibility.
 */
public class Transform3d extends edu.wpi.first.math.geometry.Transform3d {

    /**
     * Creates a zero transform.
     */
    public Transform3d() {
        super();
    }

    /**
     * Creates a rigid transform from a translation and rotation.
     *
     * @param translation translation component
     * @param rotation rotation component
     */
    public Transform3d(
            edu.wpi.first.math.geometry.Translation3d translation,
            edu.wpi.first.math.geometry.Rotation3d rotation) {
        super(translation, rotation);
    }
}
