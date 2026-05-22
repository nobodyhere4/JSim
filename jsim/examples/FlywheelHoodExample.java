package examples;

import api.GamepieceZone;
import api.Rotation3d;
import api.SimRobot;
import api.Translation3d;

/**
 * Example that models a flywheel plus an adjustable hood for aiming.
 */
public class FlywheelHoodExample {
  private final FlywheelSubsystemExample flywheel;
  private final GamepieceZone hoodZone;
  private Rotation3d hoodAngle = new Rotation3d(0, 0, 0);

  public FlywheelHoodExample(SimRobot robot) {
    this.flywheel = new FlywheelSubsystemExample(robot);
    this.hoodZone = robot.createGamepieceZone(
        "hood",
        new Translation3d[] {
          new Translation3d(0.0, 0.0, 0.0),
          new Translation3d(0.18, 0.0, 0.0),
          new Translation3d(0.18, 0.14, 0.0),
          new Translation3d(0.0, 0.14, 0.0)
        },
        new Translation3d(0.12, 0.02, 0.0),
        new Rotation3d(0.0, 0.0, 0.0));
    this.hoodZone.disable();
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
