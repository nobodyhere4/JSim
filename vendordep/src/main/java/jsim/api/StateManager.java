package jsim.api;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import jsim.PhysicsBody;
import jsim.PhysicsWorld;
import jsim.field.FieldConfig;
import jsim.field.FieldElement;
import java.util.Objects;
import jsim.field.FieldConfig;

/**
 * System Rules:
 * - Single source of truth for all simulation state.
 * - Only system allowed to mutate FieldState&lt;T&gt;.
 * - External APIs are read/write proxies ONLY through defined methods.
 */
public class StateManager {
    private static final StateManager INSTANCE = new StateManager();

    private final Map<RobotID, FieldState<SimRobot.RobotState>> robotStates = new EnumMap<>(RobotID.class);
    private final Map<RobotID, SimRobot> robots = new EnumMap<>(RobotID.class);
    private final Map<RobotID, PhysicsBody> robotBodies = new EnumMap<>(RobotID.class);
    private final List<GamepieceZone> gamepieceZones = new ArrayList<>();
    private final List<FieldElement> fieldElements = new ArrayList<>();
    private PhysicsWorld physicsWorld;

    private StateManager() {}

    /**
     * Gets the singleton instance of the StateManager.
     * @return the StateManager instance.
     */
    public static StateManager getInstance() {
        return INSTANCE;
    }

    /**
     * Loads JSON field definition and builds all FieldElements and collision zones.
     *
     * @param config The field configuration to initialize.
     */
    public void initializeField(FieldConfig config) {
        Objects.requireNonNull(config, "FieldConfig must not be null");
        // Populate internal list of field elements from the parsed configuration.
        synchronized (this) {
            fieldElements.clear();
            if (config.fieldElements != null) {
                for (FieldElement e : config.fieldElements) {
                    if (e != null) {
                        fieldElements.add(e);
                    }
                }
            }
        }
    }

    /**
     * Returns an immutable view of the current field elements registered in the simulation.
     *
     * @return unmodifiable list of field elements
     */
    public synchronized List<FieldElement> getFieldElements() {
        return Collections.unmodifiableList(new ArrayList<>(fieldElements));
    }

    /**
     * Creates a robot instance, assigns starting pose, and registers it in the simulation.
     *
     * @param id The RobotID for the robot.
     * @param startingPose The initial pose of the robot.
     * @param frameVertices The vertices of the robot's frame.
     * @return the initialized SimRobot instance.
     */
    public SimRobot initializeRobot(RobotID id, Pose2d startingPose, Translation2d[] frameVertices) {
        SimRobot.RobotState internalState = new SimRobot.RobotState();
        internalState.pose = startingPose;
        
        FieldState<SimRobot.RobotState> stateRef = new FieldState<>(internalState);
        robotStates.put(id, stateRef);
        SimRobot robot = new SimRobot(id, stateRef);
        robots.put(id, robot);
        return robot;
    }

    /**
     * Returns the simulated robot registered for the given driver-station id.
     *
     * @param id the robot identifier
     * @return the registered robot, or {@code null} if none exists
     */
    public synchronized SimRobot getRobot(RobotID id) {
        return robots.get(id);
    }

    /**
     * Returns a snapshot of all registered robot poses keyed by robot id.
     *
     * @return immutable map of robot ids to poses
     */
    public Map<RobotID, Pose2d> getRobotPoses() {
        Map<RobotID, Pose2d> poses = new EnumMap<>(RobotID.class);
        for (Map.Entry<RobotID, FieldState<SimRobot.RobotState>> entry : robotStates.entrySet()) {
            poses.put(entry.getKey(), entry.getValue().get().pose);
        }
        return Collections.unmodifiableMap(poses);
    }

    /**
     * Stores the active physics world used by the simulation layer.
     *
     * @param physicsWorld the physics world to store
     */
    public synchronized void setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    /**
     * Returns the active physics world used by the simulation layer.
     *
     * @return the current physics world, or {@code null} if none is registered
     */
    public synchronized PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    /**
     * Associates a native physics body with a robot id.
     *
     * @param id the robot identifier
     * @param robotBody the physics body to store, or {@code null} to clear the mapping
     */
    public synchronized void setRobotBody(RobotID id, PhysicsBody robotBody) {
        if (robotBody == null) {
            robotBodies.remove(id);
        } else {
            robotBodies.put(id, robotBody);
        }
    }

    /**
     * Returns the physics body associated with the given robot id.
     *
     * @param id the robot identifier
     * @return the tracked physics body, or {@code null} if none exists
     */
    public synchronized PhysicsBody getRobotBody(RobotID id) {
        return robotBodies.get(id);
    }

    /**
     * Applies a chassis-speed command to the physics body registered for the given robot id.
     *
     * @param id the robot identifier
     * @param speeds the commanded chassis speeds
     */
    public synchronized void setPhysicsVelocity(RobotID id, ChassisSpeeds speeds) {
        PhysicsBody robotBody = robotBodies.get(id);
        if (robotBody != null) {
            robotBody.setLinearVelocity(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, 0.0);
        }
    }

    /**
     * Registers a gamepiece zone to be refreshed on each simulation step.
     *
     * @param gamepieceZone the zone to register
     */
    public synchronized void registerGamepieceZone(GamepieceZone gamepieceZone) {
        if (gamepieceZone != null && !gamepieceZones.contains(gamepieceZone)) {
            gamepieceZones.add(gamepieceZone);
        }
    }

    private synchronized void refreshGamepieceZones() {
        for (GamepieceZone gamepieceZone : gamepieceZones) {
            gamepieceZone.refresh();
        }
    }

    /**
     * Advances the tracked physics world and refreshes gamepiece zones.
     */
    public synchronized void stepPhysics() {
        if (physicsWorld != null) {
            physicsWorld.step();
            refreshGamepieceZones();
        }
    }
}
