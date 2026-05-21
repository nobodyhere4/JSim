// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.api;

import edu.wpi.first.math.geometry.Rotation3d;

/**
 * Simulation-side gamepiece interaction zone.
 */
public class GamepieceZone {
  /**
   * Interaction mode for the zone.
   */
  public enum Mode {
    INTAKE,
    OUTTAKE,
    SHOOT,
    DISABLED
  }

  private final SimRobot robot;
  private double exitVelocity;
  private Rotation3d exitRotation = new Rotation3d();
  private Mode mode = Mode.DISABLED;

  /**
   * Creates a zone attached to a robot.
   *
   * @param robot the simulated robot that owns this zone
   */
  public GamepieceZone(SimRobot robot) {
    this.robot = robot;
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
   * Sets the gamepiece exit parameters for this zone.
   *
   * @param velocity the launch velocity in meters per second
   * @param rotation the launch rotation
   */
  public void setExitParameters(double velocity, Rotation3d rotation) {
    this.exitVelocity = velocity;
    this.exitRotation = rotation;
  }

  /**
   * Returns the configured exit velocity.
   *
   * @return the exit velocity in meters per second
   */
  public double getExitVelocity() {
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