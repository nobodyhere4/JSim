package jsim.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.units.measure.LinearVelocity;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;

/**
 * Regression tests for {@link GamepieceZone} helper behavior.
 */
class GamepieceZoneTest {
  /**
   * Verifies the convenience helpers update the zone mode and exit parameters together.
   */
  @Test
  void helperMethodsSetModeAndExitParametersTogether() {
    SimRobot robot = SimRobot.createRobot(
        RobotID.BLUE_1,
        new Translation2d[] {
          new Translation2d(0.0, 0.0),
          new Translation2d(0.5, 0.0),
          new Translation2d(0.5, 0.5)
        });
    GamepieceZone zone = robot.createGamepieceZone();

    zone.intake(MetersPerSecond.of(2.5), new Rotation3d(0.1, 0.2, 0.3));

    assertEquals(GamepieceZone.Mode.INTAKE, zone.getMode());
    assertEquals(2.5, zone.getExitVelocity().in(MetersPerSecond), 1e-9);
    assertEquals(0.1, zone.getExitRotation().getX(), 1e-9);
    assertEquals(0.2, zone.getExitRotation().getY(), 1e-9);
    assertEquals(0.3, zone.getExitRotation().getZ(), 1e-9);

    zone.disable();

    assertEquals(GamepieceZone.Mode.DISABLED, zone.getMode());
  }

  /**
   * Verifies supplier-backed configuration is refreshed into the zone on demand.
   */
  @Test
  void refreshAppliesConfiguredSuppliers() {
    SimRobot robot = SimRobot.createRobot(
        RobotID.BLUE_1,
        new Translation2d[] {
          new Translation2d(0.0, 0.0),
          new Translation2d(0.5, 0.0),
          new Translation2d(0.5, 0.5)
        });
    GamepieceZone zone = robot.createGamepieceZone();
    AtomicReference<GamepieceZone.Mode> mode = new AtomicReference<>(GamepieceZone.Mode.OUTTAKE);
        AtomicReference<LinearVelocity> velocity = new AtomicReference<>(MetersPerSecond.of(4.25));
    AtomicReference<Rotation3d> rotation = new AtomicReference<>(new Rotation3d(1.0, 2.0, 3.0));

        zone.configure(mode::get, velocity::get, rotation::get);
    zone.refresh();

    assertEquals(GamepieceZone.Mode.OUTTAKE, zone.getMode());
        assertEquals(4.25, zone.getExitVelocity().in(MetersPerSecond), 1e-9);
    assertEquals(1.0, zone.getExitRotation().getX(), 1e-9);
    assertEquals(2.0, zone.getExitRotation().getY(), 1e-9);
    assertEquals(3.0, zone.getExitRotation().getZ(), 1e-9);

    mode.set(GamepieceZone.Mode.SHOOT);
        velocity.set(MetersPerSecond.of(6.0));
    rotation.set(new Rotation3d(0.0, 0.0, 1.0));
    zone.refresh();

    assertEquals(GamepieceZone.Mode.SHOOT, zone.getMode());
        assertEquals(6.0, zone.getExitVelocity().in(MetersPerSecond), 1e-9);
    assertEquals(0.0, zone.getExitRotation().getX(), 1e-9);
    assertEquals(0.0, zone.getExitRotation().getY(), 1e-9);
    assertEquals(1.0, zone.getExitRotation().getZ(), 1e-9);
  }
}