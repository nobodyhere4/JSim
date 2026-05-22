package examples;

import api.GamepieceZone;
import api.Rotation3d;
import api.SimRobot;
import api.Transform3d;
import api.Translation3d;

/**
 * Example that models a flywheel plus an adjustable hood for aiming.
 */
public class FlywheelHoodExample {
  private static final Rotation3d ZERO_ROTATION = new Rotation3d();

  private final FlywheelSubsystemExample flywheel;
  private final GamepieceZone hoodZone;
  private Rotation3d hoodAngle = ZERO_ROTATION;

  public FlywheelHoodExample(SimRobot robot) {
    this.flywheel = new FlywheelSubsystemExample(robot);
    this.hoodZone = robot.createGamepieceZone(
        "hood",
        createZoneDimensions(
            new Translation3d(0.0, 0.0, 0.0),
            new Translation3d(0.18, 0.0, 0.0),
            new Translation3d(0.18, 0.14, 0.0),
            new Translation3d(0.0, 0.14, 0.0)),
        new Translation3d(0.12, 0.02, 0.0),
        ZERO_ROTATION);
    this.hoodZone.disable();
  }

  private static Transform3d[] createZoneDimensions(Translation3d... translations) {
    Transform3d[] transforms = new Transform3d[translations.length];
    for (int i = 0; i < translations.length; i++) {
      transforms[i] = new Transform3d(translations[i], ZERO_ROTATION);
    }
    return transforms;
  }

  public void setShot(double left, double right, Rotation3d angle) {
    flywheel.setFlywheel(left, right, angle);
    this.hoodAngle = angle;
  }

  public void fire() {
    flywheel.shoot();
    hoodZone.shoot(0.0, hoodAngle);
  }

  public void stop() {
    flywheel.stop();
    hoodZone.disable();
  }

  public FlywheelSubsystemExample getFlywheel() {
    return flywheel;
  }

  public GamepieceZone getHoodZone() {
    return hoodZone;
  }
}
