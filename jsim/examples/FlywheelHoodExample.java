package examples;

import jsim.api.GamepieceZone;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * Example that models a flywheel plus an adjustable hood for aiming.
 */
public class FlywheelHoodExample {
  private static final Translation3d[] HOOD_ZONE_POINTS = {
    new Translation3d(0.0, 0.0, 0.0),
    new Translation3d(0.18, 0.0, 0.0),
    new Translation3d(0.18, 0.14, 0.0),
    new Translation3d(0.0, 0.14, 0.0)
  };
  private static final Translation3d HOOD_ROBOT_CENTER_OFFSET = new Translation3d(0.12, 0.02, 0.0);

  private final FlywheelSubsystemExample flywheel;
  private final GamepieceZone hoodZone;
  private Rotation3d hoodAngle = Rotation3d.kZero;

  public FlywheelHoodExample(SimRobot robot) {
    this.flywheel = new FlywheelSubsystemExample(robot);
    this.hoodZone = robot.createGamepieceZone(
      "hood",
      GamepieceZone.createZoneDimensions(Rotation3d.kZero, HOOD_ZONE_POINTS),
      HOOD_ROBOT_CENTER_OFFSET,
      Rotation3d.kZero);
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
