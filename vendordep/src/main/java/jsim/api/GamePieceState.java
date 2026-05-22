package jsim.api;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;

/**
 * Represents the state of a game piece in the simulation.
 * Includes type, velocity, rotation, and other physical properties.
 */
public class GamePieceState {
    /** Type of this game piece. */
    public GamePieceType type;
    /** Scalar exit velocity used by older state managers. */
    public double velocity;
    /** Orientation of the piece. */
    public Rotation3d rotation;
    /** Start offset in the robot frame. */
    public Translation2d robotOffsetStart;
    /** Exit velocity vector. */
    public Translation3d exitVelocity;
    /** Exit orientation. */
    public Rotation3d exitAngle;
    /** Intake polygon in robot space. */
    public Translation3d[] intakeArea;

    /**
     * Creates a game piece state for the given type.
     *
     * @param type game piece type
     */
    public GamePieceState(GamePieceType type) {
        this.type = type;
    }

    /** Returns the piece type. */
    public GamePieceType getType() { return type; }
    /** Sets the scalar velocity. */
    public void setVelocity(double velocity) { this.velocity = velocity; }
    /** Sets the rotation. */
    public void setRotation(Rotation3d rotation) { this.rotation = rotation; }
    /** Returns the scalar velocity. */
    public double getVelocity() { return velocity; }
    /** Returns the rotation. */
    public Rotation3d getRotation() { return rotation; }
    /** Sets the robot offset start. */
    public void setRobotOffsetStart(Translation2d offset) { this.robotOffsetStart = offset; }
    /** Returns the robot offset start. */
    public Translation2d getRobotOffsetStart() { return robotOffsetStart; }
    /** Sets the exit velocity vector. */
    public void setExitVelocity(Translation3d velocity) { this.exitVelocity = velocity; }
    /** Returns the exit velocity vector. */
    public Translation3d getExitVelocity() { return exitVelocity; }
    /** Sets the exit angle. */
    public void setExitAngle(Rotation3d angle) { this.exitAngle = angle; }
    /** Returns the exit angle. */
    public Rotation3d getExitAngle() { return exitAngle; }
    /** Sets the intake polygon area. */
    public void intakeZone(Translation3d[] intakeArea) { this.intakeArea = intakeArea; }
    /** Returns the intake polygon area. */
    public Translation3d[] getIntakeArea() { return intakeArea; }
}
