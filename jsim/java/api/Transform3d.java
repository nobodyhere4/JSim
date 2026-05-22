package api;

/**
 * A 3D rigid transform composed of a translation and a rotation.
 */
public class Transform3d {
    /** The translation component of the transform. */
    public final Translation3d translation;
    /** The rotation component of the transform. */
    public final Rotation3d rotation;

    /**
     * Creates a transform from the supplied translation and rotation.
     *
     * @param translation the translation component
     * @param rotation the rotation component
     */
    public Transform3d(Translation3d translation, Rotation3d rotation) {
        this.translation = translation;
        this.rotation = rotation;
    }
}