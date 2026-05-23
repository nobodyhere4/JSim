package jsim.api;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import jsim.api.StateManager;

/**
 * External interface for simulated FRC Robots interacting with JSim.
 */
public class SimRobot {
    private final RobotID robotID;
    private final FieldState<RobotState> stateManagerRef;
    private final Map<String, GamepieceZone> gamepieceZones = new HashMap<>();

    /**
     * Internal state for a simulated robot, including pose and speeds.
     */
    public static class RobotState {
        /**
         * The current field-relative pose of the robot.
         */
        public Pose2d pose = new Pose2d();
        /**
         * The current chassis speeds of the robot.
         */
        public ChassisSpeeds speeds = new ChassisSpeeds();
    }

    /**
     * Protected constructor. Use SimRobot.createRobot().
     * @param id The RobotID for this robot.
     * @param stateRef The field state reference for this robot.
     */
    protected SimRobot(RobotID id, FieldState<RobotState> stateRef) {
        this.robotID = id;
        this.stateManagerRef = stateRef;
    }

    /**
     * Initializes a new robot via the unified StateManager tracking system.
     * 
     * @param id The logical driver station id.
     * @param frameDimensions Translation2d[] for each vertex of the frame perimeter
     * @return the instantiated SimRobot handle.
     */
    public static SimRobot createRobot(RobotID id, Translation2d[] frameDimensions) {
        return StateManager.getInstance().initializeRobot(id, new Pose2d(), frameDimensions);
    }

    /**
     * Retrieves a previously created robot by driver-station id.
     *
     * @param id the robot identifier
     * @return the registered robot, or {@code null} if none exists
     */
    public static SimRobot getRobot(RobotID id) {
        return StateManager.getInstance().getRobot(id);
    }

    /**
     * Creates a gamepiece zone tied to this robot.
     *
     * @param name the zone name used for retrieval
     * @param zoneDimensions the zone polygon dimensions relative to the robot center
     * @param robotCenterOffset the zone offset from the robot center
     * @param robotRotation the zone rotation relative to the robot
     * @return a new zone registered for simulation refreshes
         * @throws IllegalArgumentException if a zone with the same name already exists
     */
    public GamepieceZone createGamepieceZone(
            String name,
            Transform3d[] zoneDimensions,
            Rotation3d robotRotation) {
        if (name != null && gamepieceZones.containsKey(name)) {
            throw new IllegalArgumentException("Gamepiece zone already exists: " + name);
        }
        GamepieceZone zone = new GamepieceZone(this, name, zoneDimensions, robotRotation);
        if (name != null) {
            gamepieceZones.put(name, zone);
        }
        return zone;
    }

    /**
     * Retrieves a previously created gamepiece zone by name.
     *
     * @param name the zone name
     * @return the registered zone, or {@code null} if none exists
     */
    public GamepieceZone getGamepieceZone(String name) {
        return gamepieceZones.get(name);
    }

    /**
     * Returns all gamepiece zones registered on this robot.
     *
     * @return an immutable view of the named zones
     */
    public Map<String, GamepieceZone> getGamepieceZones() {
        return Collections.unmodifiableMap(gamepieceZones);
    }

    /**
     * Retrieves the current field-relative odometry pose. 
     * Pulled strictly from the StateManager snapshot.
     * @return the current pose of the robot.
     */
    public Pose2d getPose() {
        return stateManagerRef.get().pose;
    }

    /**
     * Hard overrides the state manager's simulation pose for this robot.
     * @param pose the new pose to set for the robot.
     */
    public void resetPose(Pose2d pose) {
        stateManagerRef.get().pose = pose;
    }

    /**
     * Applies commanded speeds to the physics solver.
     * @param speeds the chassis speeds to apply.
     */
    public void setChassisSpeeds(ChassisSpeeds speeds) {
        stateManagerRef.get().speeds = speeds;
    }

    /**
     * Gets the RobotID for this robot.
     * @return the RobotID assigned to this robot.
     */
    public RobotID getRobotID() {
        return robotID;
    }
}
