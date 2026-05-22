package api;

/**
 * Represents a polygonal interaction zone on a simulated robot used for intake/outtake
 * interactions with gamepieces.
 *
 * <p>All spatial units in this API use meters for translation components and radians for
 * rotations where applicable. When constructing {@link Transform3d} instances for
 * {@code zoneDimensions} or exit transforms, provide translations in meters.
 */
public class GamepieceZone {
    /** Mode describing how the zone interacts with gamepieces. */
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
    private final String name;
    private final Transform3d[] zoneDimensions; // per-vertex transforms (meters + rotation)
    private final Translation3d robotCenterOffset; // meters
    private final Rotation3d robotRotation; // radians

    private double exitVelocity; // meters per second
    private Rotation3d exitRotation; // fallback rotation-only exit
    private Transform3d exitTransform; // optional full exit transform
    private Mode mode = Mode.DISABLED;

    /**
     * Creates a new gamepiece zone.
     *
     * @param robot owner robot for the zone
     * @param name optional human-readable name for the zone
     * @param zoneDimensions per-vertex transforms describing the zone polygon relative to the robot
     *                       (translations in meters)
     * @param robotCenterOffset offset of the zone's origin from the robot center (meters)
     * @param robotRotation rotation of the zone relative to the robot (radians)
     */
    public GamepieceZone(
            SimRobot robot,
            String name,
            Transform3d[] zoneDimensions,
            Translation3d robotCenterOffset,
            Rotation3d robotRotation) {
        this.robot = robot;
        this.name = name;
        this.zoneDimensions = zoneDimensions;
        this.robotCenterOffset = robotCenterOffset;
        this.robotRotation = robotRotation;
    }

    /** Returns the owning robot. */
    public SimRobot getRobot() {
        return robot;
    }

    /** Returns the zone name, or {@code null} if unnamed. */
    public String getName() {
        return name;
    }

    /** Returns the per-vertex transforms for the zone. Translations are in meters. */
    public Transform3d[] getZoneDimensions() {
        return zoneDimensions;
    }

    /** Returns the offset of the zone from the robot center (meters). */
    public Translation3d getRobotCenterOffset() {
        return robotCenterOffset;
    }

    /** Returns the rotation of the zone relative to the robot (radians). */
    public Rotation3d getRobotRotation() {
        return robotRotation;
    }

    /** Sets the active interaction mode for this zone. */
    public void setMode(Mode mode) {
        this.mode = mode;
    }

    /** Sets the exit parameters using a rotation-only exit orientation. Velocity is meters/second. */
    public void setExitParameters(double velocity, Rotation3d rotation) {
        this.exitVelocity = velocity;
        this.exitRotation = rotation;
        this.exitTransform = null;
    }

    /** Sets the exit parameters using a full {@link Transform3d} for exit pose. Velocity is meters/second. */
    public void setExitParameters(double velocity, Transform3d transform) {
        this.exitVelocity = velocity;
        this.exitTransform = transform;
        this.exitRotation = transform == null ? null : transform.getRotation();
    }

    /** Returns the configured exit velocity in meters per second. */
    public double getExitVelocity() {
        return exitVelocity;
    }

    /** Returns the exit orientation as a {@link Rotation3d}, if configured (may be {@code null}). */
    public Rotation3d getExitRotation() {
        return exitRotation;
    }

    /**
     * Returns the full exit {@link Transform3d} if set, otherwise {@code null}.
     *
     * <p>Prefer this over {@link #getExitRotation()} when callers need both translation
     * and rotation for exit calculations.
     */
    public Transform3d getExitTransform() {
        return exitTransform;
    }

    /** Returns the current mode. */
    public Mode getMode() {
        return mode;
    }
}
