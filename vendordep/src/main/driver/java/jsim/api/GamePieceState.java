package jsim.api;

/**
 * Represents the state of a game piece in the simulation.
 * Includes type, velocity, rotation, and other physical properties.
 */
public class GamePieceState {
    public GamePieceType type;
    public double velocity;
    public Rotation3d rotation;
    public Translation2d robotOffsetStart;
    public Translation3d exitVelocity;
    public Rotation3d exitAngle;
    public Translation3d[] intakeArea;

    public GamePieceState(GamePieceType type) {
        this.type = type;
    }

    public GamePieceType getType() { return type; }
    public void setVelocity(double velocity) { this.velocity = velocity; }
    public void setRotation(Rotation3d rotation) { this.rotation = rotation; }
    public double getVelocity() { return velocity; }
    public Rotation3d getRotation() { return rotation; }
    public void setRobotOffsetStart(Translation2d offset) { this.robotOffsetStart = offset; }
    public Translation2d getRobotOffsetStart() { return robotOffsetStart; }
    public void setExitVelocity(Translation3d velocity) { this.exitVelocity = velocity; }
    public Translation3d getExitVelocity() { return exitVelocity; }
    public void setExitAngle(Rotation3d angle) { this.exitAngle = angle; }
    public Rotation3d getExitAngle() { return exitAngle; }
    public void intakeZone(Translation3d[] intakeArea) { this.intakeArea = intakeArea; }
    public Translation3d[] getIntakeArea() { return intakeArea; }
}
