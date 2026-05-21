// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import edu.wpi.first.math.geometry.Translation2d;
import jsim.api.RobotID;
import jsim.api.SimRobot;
import jsim.api.StateManager;

/**
 * Public entry point for common JSim accessors.
 */
public final class JSim {
  private JSim() {}

  /**
   * Returns the shared simulation state manager.
   *
   * @return the singleton state manager
   */
  public static StateManager getStateManager() {
    return StateManager.getInstance();
  }

  /**
   * Creates a simulated robot using the shared state manager.
   *
   * @param id the robot identifier
   * @param frameDimensions the robot frame perimeter vertices
   * @return the created simulated robot
   */
  public static SimRobot createRobot(RobotID id, Translation2d[] frameDimensions) {
    return SimRobot.createRobot(id, frameDimensions);
  }

  /**
   * Creates a new physics world.
   *
   * @param fixedDtSeconds fixed simulation timestep in seconds
   * @param enableGravity whether gravity is enabled for the world
   * @return the created physics world
   */
  public static PhysicsWorld createPhysicsWorld(double fixedDtSeconds, boolean enableGravity) {
    return new PhysicsWorld(fixedDtSeconds, enableGravity);
  }
}