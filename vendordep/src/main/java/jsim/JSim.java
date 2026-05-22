// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import edu.wpi.first.math.geometry.Translation2d;
import jsim.api.RobotID;
import jsim.api.SimRobot;
import jsim.api.StateManager;
import jsim.nt.WorldPosePublisher;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jsim.field.FieldDefinitionCatalog;
import jsim.field.FieldConfig;

/**
 * Public entry point for common JSim accessors.
 */
public final class JSim {
  private static PhysicsWorld physicsWorld;
  private static WorldPosePublisher defaultWorldPublisher;

  private JSim() {}

  private static final ObjectMapper MAPPER = new ObjectMapper();

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
   * Returns the simulated robot registered for the given driver-station id.
   *
   * @param id the robot identifier
   * @return the registered simulated robot, or {@code null} if none exists
   */
  public static SimRobot getRobot(RobotID id) {
    return StateManager.getInstance().getRobot(id);
  }

  /**
   * Creates a new physics world.
   *
   * @param fixedDtSeconds fixed simulation timestep in seconds
   * @param enableGravity whether gravity is enabled for the world
   * @return the created physics world
   */
  public static PhysicsWorld createPhysicsWorld(double fixedDtSeconds, boolean enableGravity) {
    if (physicsWorld == null) {
      physicsWorld = new PhysicsWorld(fixedDtSeconds, enableGravity);
      StateManager.getInstance().setPhysicsWorld(physicsWorld);
    }

    if (defaultWorldPublisher == null) {
      try {
        // Install default telemetry publisher for world pose exports.
        defaultWorldPublisher = new WorldPosePublisher(
            physicsWorld.getNativeHandle(), physicsWorld.getMaxBodies());
        physicsWorld.addStepListener(() -> defaultWorldPublisher.publishFrame());
      } catch (Throwable t) {
        // Swallow errors to avoid breaking world creation when NT isn't available.
      }
    }
    return physicsWorld;
  }

  /**
   * Returns the shared physics world, if one has been created.
   *
   * @return the shared physics world, or {@code null} if none exists yet
   */
  public static PhysicsWorld getPhysicsWorld() {
    return physicsWorld;
  }

  /**
   * Loads a built-in field definition by year and initializes the simulation field state.
   * This is part of the JSim public API so examples and user code call into the vendordep.
   *
   * @param year season year (e.g. 2026)
   */
  public static void initializeField(int year) {
    JsonNode node = FieldDefinitionCatalog.loadFieldNode(year);
    try {
      FieldConfig cfg = MAPPER.treeToValue(node, FieldConfig.class);
      StateManager.getInstance().initializeField(cfg);
    } catch (Exception e) {
      throw new RuntimeException("Failed to initialize field for year " + year, e);
    }
  }

  /**
   * Silences WPILib joystick connection warnings if WPILib is present on the classpath.
   * This uses reflection so callers don't need a compile-time dependency on WPILib.
   */
  public static void silenceJoystickWarnings() {
    try {
      Class<?> ds = Class.forName("edu.wpi.first.wpilibj.DriverStation");
      java.lang.reflect.Method m = ds.getMethod("silenceJoystickConnectionWarning", boolean.class);
      m.invoke(null, true);
    } catch (ClassNotFoundException e) {
      // WPILib not available; nothing to do.
    } catch (Exception e) {
      throw new RuntimeException("Failed to silence joystick warnings", e);
    }
  }

  /**
   * Returns a WPILib zero rotation instance. Helpers and examples should prefer this method
   * when a canonical zero rotation is needed rather than constructing new instances.
   */
  public static edu.wpi.first.math.geometry.Rotation3d rotationZero() {
    return new edu.wpi.first.math.geometry.Rotation3d();
  }
}