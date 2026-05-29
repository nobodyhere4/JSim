package jsim.api;

import static edu.wpi.first.units.Units.MetersPerSecond;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

class StateManagerTest {
  @Test
  void stepPhysicsUpdatesRobotAndZoneState() {
    StateManager stateManager = StateManager.getInstance();
    SimRobot robot = stateManager.initializeRobot(
        RobotID.BLUE_2,
        new Pose2d(),
        new Translation2d[] {
          new Translation2d(0.0, 0.0),
          new Translation2d(0.4, 0.0),
          new Translation2d(0.4, 0.4)
        });
    robot.setChassisSpeeds(new ChassisSpeeds(1.0, 0.0, 0.0));

    GamepieceZone zone = robot.createGamepieceZone(
        "state-manager-test-zone",
        new Transform3d[] {
          new Transform3d(new Translation3d(0.0, 0.0, 0.0), new Rotation3d()),
          new Transform3d(new Translation3d(0.2, 0.0, 0.0), new Rotation3d()),
          new Transform3d(new Translation3d(0.2, 0.2, 0.0), new Rotation3d()),
          new Transform3d(new Translation3d(0.0, 0.2, 0.0), new Rotation3d())
        },
        new Rotation3d());

    AtomicBoolean enter = new AtomicBoolean(true);
    AtomicBoolean exit = new AtomicBoolean(false);
    AtomicReference<Double> rate = new AtomicReference<>(3.0);
    AtomicReference<Rotation3d> rotation = new AtomicReference<>(new Rotation3d());
    AtomicReference<Translation3d> translation = new AtomicReference<>(new Translation3d());
    zone.configure(
      GamepieceZone.Mode.OUTTAKE,
      enter::get,
      exit::get,
      rate::get,
      rotation::get,
      translation::get);

    stateManager.stepPhysics();

    assertEquals(0.02, robot.getPose().getX(), 1e-9);
    assertEquals(GamepieceZone.Mode.OUTTAKE, zone.getMode());
    assertEquals(3.0, zone.getExitVelocity().in(MetersPerSecond), 1e-9);
  }
}