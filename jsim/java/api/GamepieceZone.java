package api;

public class GamepieceZone {
    public enum Mode {
        /** Intake a gamepiece into the robot. */
        INTAKE, // Intake gamepiece, motor count irrelevant
        /** Outtake a gamepiece using a single roller. */
        OUTTAKE, // Outtake gamepiece with only one roller (intake, flywheel wihout backrollers, etc.)
        /** Outtake a gamepiece using a shooter-style mechanism. */
        SHOOT, // Outtake gamepiece with two rollers (flywheel with backrollers, etc.)
        /** Disable gamepiece interaction. */
        DISABLED // No gamepiece interaction, motor count irrelevant
    }

    private double exitVelocity;
    private Rotation3d exitRotation;
    private Mode mode = Mode.DISABLED;

    public GamepieceZone(SimRobot robot) {
        // Optionally link to robot or state manager if needed
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        // Integrate with StateManager if needed
    }

    public void setExitParameters(double velocity, Rotation3d rotation) {
        this.exitVelocity = velocity;
        this.exitRotation = rotation;
    }

    public double getExitVelocity() {
        return exitVelocity;
    }

    public Rotation3d getExitRotation() {
        return exitRotation;
    }

    public Mode getMode() {
        return mode;
    }
}
