package examples;

import jsim.api.Rotation3d;
import jsim.api.Transform3d;
import jsim.api.Translation3d;

/**
 * Small geometry helpers shared by the example subsystems.
 */
final class ExampleGeometry {
  private ExampleGeometry() {}

  /**
   * Builds a transform array from vertex translations and a shared rotation.
   *
   * @param rotation the rotation to apply to every vertex
   * @param translations the vertex translations defining the zone polygon
   * @return transforms with the provided rotation applied to each translation
   */
  static Transform3d[] createZoneDimensions(Rotation3d rotation, Translation3d... translations) {
    Transform3d[] transforms = new Transform3d[translations.length];
    for (int i = 0; i < translations.length; i++) {
      transforms[i] = new Transform3d(translations[i], rotation);
    }
    return transforms;
  }
}
