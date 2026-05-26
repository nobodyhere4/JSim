// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

import static edu.wpi.first.units.Units.Meters;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.Distance;

/**
 * Generic handle for a gamepiece managed by {@link PhysicsWorld}.
 */
public class Gamepiece {
  private final PhysicsWorld world;
  private final int gamepieceIndex;

  Gamepiece(PhysicsWorld world, int gamepieceIndex) {
    this.world = world;
    this.gamepieceIndex = gamepieceIndex;
  }

  /**
   * Gets the native gamepiece index for this handle.
   *
   * @return the native gamepiece index
   */
  public int gamepieceIndex() {
    return gamepieceIndex;
  }

  /**
   * Sets the gamepiece world-space position in meters.
   *
   * @param pose the new position (only translation component is used)
   */
  public void setPosition(Pose3d pose) {
    Translation3d translation = pose.getTranslation();
    world.setGamepiecePosition(
        gamepieceIndex, translation.getX(), translation.getY(), translation.getZ());
  }

  /**
   * Sets the gamepiece world-space position in meters.
   *
   * @param positionMeters the new position in meters
   */
  public void setPosition(Translation3d positionMeters) {
    world.setGamepiecePosition(
        gamepieceIndex,
        positionMeters.getX(),
        positionMeters.getY(),
        positionMeters.getZ());
  }

  /**
   * Sets the gamepiece world-space position in meters.
   *
   * @param positionMeters the new position in meters
   */
  public void setPosition(Vec3 positionMeters) {
    world.setGamepiecePosition(gamepieceIndex, positionMeters.x(), positionMeters.y(), positionMeters.z());
  }

  /**
   * Sets the gamepiece world-space position.
   *
   * @param x x position
   * @param y y position
   * @param z z position
   */
  public void setPosition(Distance x, Distance y, Distance z) {
    world.setGamepiecePosition(gamepieceIndex, x.in(Meters), y.in(Meters), z.in(Meters));
  }

  /**
   * Sets the gamepiece world-space linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
  public void setLinearVelocity(LinearVelocity3d velocityMps) {
    world.setGamepieceLinearVelocity(
        gamepieceIndex,
        velocityMps.getVxMetersPerSecond(),
        velocityMps.getVyMetersPerSecond(),
        velocityMps.getVzMetersPerSecond());
  }

  /**
   * Sets the gamepiece world-space linear velocity in meters per second.
   *
   * @param velocityMps the new linear velocity in meters per second
   */
  public void setLinearVelocity(Vec3 velocityMps) {
    world.setGamepieceLinearVelocity(gamepieceIndex, velocityMps.x(), velocityMps.y(), velocityMps.z());
  }

  /**
   * Sets the gamepiece world-space linear velocity in meters per second.
   *
   * @param vxMetersPerSecond x velocity in meters per second
   * @param vyMetersPerSecond y velocity in meters per second
   * @param vzMetersPerSecond z velocity in meters per second
   */
  public void setLinearVelocity(
      double vxMetersPerSecond, double vyMetersPerSecond, double vzMetersPerSecond) {
    world.setGamepieceLinearVelocity(
        gamepieceIndex, vxMetersPerSecond, vyMetersPerSecond, vzMetersPerSecond);
  }

  /**
   * Convenience method to set both position and launch velocity.
   *
   * @param pose launch position (only translation component is used)
   * @param velocityMps launch velocity in meters per second
   */
  public void shoot(Pose3d pose, LinearVelocity3d velocityMps) {
    setPosition(pose);
    setLinearVelocity(velocityMps);
  }

  /**
   * Convenience method to set both position and launch velocity.
   *
   * @param positionMeters launch position in meters
   * @param velocityMps launch velocity in meters per second
   */
  public void shoot(Translation3d positionMeters, LinearVelocity3d velocityMps) {
    setPosition(positionMeters);
    setLinearVelocity(velocityMps);
  }

  /**
   * Convenience method to set both position and launch velocity.
   *
   * @param positionMeters launch position in meters
   * @param velocityMps launch velocity in meters per second
   */
  public void shoot(Vec3 positionMeters, Vec3 velocityMps) {
    setPosition(positionMeters);
    setLinearVelocity(velocityMps);
  }

  /**
   * Gets the current world-space position in meters.
   *
   * @return the gamepiece position
   */
  public Pose3d position() {
    return world.getGamepiecePosition(gamepieceIndex);
  }

  /**
   * Gets the current world-space linear velocity in meters per second.
   *
   * @return the gamepiece linear velocity
   */
  public LinearVelocity3d linearVelocity() {
    return world.getGamepieceLinearVelocity(gamepieceIndex);
  }
}
