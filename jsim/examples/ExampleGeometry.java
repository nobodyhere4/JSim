package examples;

import jsim.api.GamepieceZone;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * Small geometry helpers shared by the example subsystems. Delegates to the vendordep API.
 */
final class ExampleGeometry {
  private ExampleGeometry() {}

  static Transform3d[] createZoneDimensions(Rotation3d rotation, Translation3d... translations) {
    return GamepieceZone.createZoneDimensions(rotation, translations);
  }
}
