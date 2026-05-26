// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim;

/**
 * Backward-compatible wrapper for a spherical {@link Gamepiece}.
 */
@Deprecated(forRemoval = false)
public final class Ball extends Gamepiece {
  private final int ballIndex;

  Ball(PhysicsWorld world, int ballIndex) {
    super(world, ballIndex);
    this.ballIndex = ballIndex;
  }

  /**
   * Gets the native ball index for this ball.
   *
   * @return the native ball index
   */
  public int ballIndex() {
    return ballIndex;
  }

  /**
   * Backward-compatible alias for {@link #gamepieceIndex()}.
   *
   * @return native index of the wrapped gamepiece
   */
  public int gamepieceIndex() {
    return super.gamepieceIndex();
  }
}
