package jsim.api;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jsim.PhysicsBody;
import jsim.PhysicsWorld;
import jsim.field.FieldConfig;
import jsim.nt.FieldTelemetryPublisher;

/**
 * System Rules:
 * - Single source of truth for all simulation state.
 * - Only system allowed to mutate FieldState&lt;T&gt;.
 * - External APIs are read/write proxies ONLY through defined methods.
 */
public class StateManager {
    private static final StateManager INSTANCE = new StateManager();

    private final Map<RobotID, FieldState<SimRobot.RobotState>> robotStates = new HashMap<>();
    private final Map<RobotID, SimRobot> robots = new HashMap<>();
    private final Map<RobotID, PhysicsBody> robotBodies = new HashMap<>();
    private final List<GamepieceZone> gamepieceZones = new ArrayList<>();
    private PhysicsWorld physicsWorld;
    private FieldTelemetryPublisher telemetryPublisher;

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
     * @param config The field configuration to initialize.
     */
    public void initializeField(FieldConfig config) {
        // Build collision zones based on parsed config schemas
    }

    /**
     * Creates a robot instance, assigns starting pose, and registers it in the simulation.
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
        Map<RobotID, Pose2d> poses = new HashMap<>();
        for (Map.Entry<RobotID, FieldState<SimRobot.RobotState>> entry : robotStates.entrySet()) {
            poses.put(entry.getKey(), entry.getValue().get().pose);
        }
        return Collections.unmodifiableMap(poses);
    }

    public synchronized void setPhysicsWorld(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    public synchronized PhysicsWorld getPhysicsWorld() {
        return physicsWorld;
    }

    public synchronized void setRobotBody(RobotID id, PhysicsBody robotBody) {
        if (robotBody == null) {
            robotBodies.remove(id);
        } else {
            robotBodies.put(id, robotBody);
        }
    }

    public synchronized PhysicsBody getRobotBody(RobotID id) {
        return robotBodies.get(id);
    }

    public synchronized void setTelemetryPublisher(FieldTelemetryPublisher telemetryPublisher) {
        this.telemetryPublisher = telemetryPublisher;
    }

    public synchronized FieldTelemetryPublisher getTelemetryPublisher() {
        return telemetryPublisher;
    }

    public synchronized void setPhysicsVelocity(RobotID id, ChassisSpeeds speeds) {
        PhysicsBody robotBody = robotBodies.get(id);
        if (robotBody != null) {
            robotBody.setLinearVelocity(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, 0.0);
        }
    }

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

    public synchronized void stepPhysics() {
        if (physicsWorld != null) {
            physicsWorld.step();
            refreshGamepieceZones();
            if (telemetryPublisher != null) {
                telemetryPublisher.publishFrame();
            }
        }
    }
}
