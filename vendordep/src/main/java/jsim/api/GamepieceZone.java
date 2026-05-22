// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.api;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.units.measure.LinearVelocity;
import java.util.function.Supplier;

/**
 * Simulation-side gamepiece interaction zone.
 */
public class GamepieceZone {
  /**
   * Interaction mode for the zone.
   */
  public enum Mode {
    /** Intake a gamepiece into the robot. */
    INTAKE,
    /** Outtake a gamepiece using a single roller. */
    OUTTAKE,
    /** Outtake a gamepiece using a shooter-style mechanism. */
    SHOOT,
    /** Disable gamepiece interaction. */
    DISABLED
  }

  private final SimRobot robot;
  private LinearVelocity exitVelocity = MetersPerSecond.of(0.0);
  private Rotation3d exitRotation = new Rotation3d();
  private Mode mode = Mode.DISABLED;
  private Supplier<Mode> modeSupplier;
  private Supplier<LinearVelocity> exitVelocitySupplier;
  private Supplier<Rotation3d> exitRotationSupplier;

  /**
   * Creates a zone attached to a robot.
   *
   * @param robot the simulated robot that owns this zone
   */
  GamepieceZone(SimRobot robot) {
    this.robot = robot;
    StateManager.getInstance().registerGamepieceZone(this);
  }

  /**
   * Returns the robot associated with this zone.
   *
   * @return the owning simulated robot
   */
  public SimRobot getRobot() {
    return robot;
  }

  /**
   * Sets the zone interaction mode.
   *
   * @param mode the new zone mode
   */
  public void setMode(Mode mode) {
    this.mode = mode;
  }

  /**
   * Disables gamepiece interaction for this zone.
   */
  public void disable() {
    setMode(Mode.DISABLED);
  }

  /**
   * Sets the gamepiece exit parameters for this zone.
   *
   * @param velocity the launch velocity
   * @param rotation the launch rotation
   */
  public void setExitParameters(LinearVelocity velocity, Rotation3d rotation) {
    this.exitVelocity = velocity;
    this.exitRotation = rotation;
  }

  /**
   * Sets the gamepiece exit velocity with a default zero rotation.
   *
   * @param velocity the launch velocity
   */
  public void setExitParameters(LinearVelocity velocity) {
    setExitParameters(velocity, new Rotation3d());
  }

  /**
   * Configures the zone to intake gamepieces.
   *
   * @param velocity the launch velocity
   * @param rotation the launch rotation
   */
  public void intake(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.INTAKE);
  }

  /**
   * Configures the zone to outtake gamepieces.
   *
   * @param velocity the launch velocity
   * @param rotation the launch rotation
   */
  public void outtake(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.OUTTAKE);
  }

  /**
   * Configures the zone to shoot gamepieces.
   *
   * @param velocity the launch velocity
   * @param rotation the launch rotation
   */
  public void shoot(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.SHOOT);
  }

  /**
   * Configures this zone from suppliers that can be refreshed each simulation step.
   *
   * @param modeSupplier supplies the current interaction mode
   * @param exitVelocitySupplier supplies the current exit velocity
   * @param exitRotationSupplier supplies the current exit rotation
   */
  public void configure(
      Supplier<Mode> modeSupplier,
      Supplier<LinearVelocity> exitVelocitySupplier,
      Supplier<Rotation3d> exitRotationSupplier) {
    this.modeSupplier = modeSupplier;
    this.exitVelocitySupplier = exitVelocitySupplier;
    this.exitRotationSupplier = exitRotationSupplier;
  }

  /**
   * Refreshes this zone from its configured suppliers, if present.
   */
  public void refresh() {
    if (modeSupplier != null) {
      setMode(modeSupplier.get());
    }
    if (exitVelocitySupplier != null) {
      exitVelocity = exitVelocitySupplier.get();
    }
    if (exitRotationSupplier != null) {
      exitRotation = exitRotationSupplier.get();
    }
  }

  /**
   * Returns the configured exit velocity.
   *
   * @return the exit velocity in meters per second
   */
  public LinearVelocity getExitVelocity() {
    return exitVelocity;
  }

  /**
   * Returns the configured exit rotation.
   *
   * @return the exit rotation
   */
  public Rotation3d getExitRotation() {
    return exitRotation;
  }

  /**
   * Returns the active interaction mode.
   *
   * @return the current mode
   */
  public Mode getMode() {
    return mode;
  }
}