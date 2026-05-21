package jsim.api;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
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
    private PhysicsWorld physicsWorld;
    private PhysicsBody robotBody;
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
     * Sets the active physics world managed by the simulation layer.
     *
     * @param physicsWorld the physics world to store
     */
    public static void setPhysicsWorld(PhysicsWorld physicsWorld) {
        INSTANCE.setPhysicsWorldInternal(physicsWorld);
    }

    /**
     * Returns the active physics world managed by the simulation layer.
     *
     * @return the current physics world, or {@code null} if none is registered
     */
    public static PhysicsWorld getPhysicsWorld() {
        return INSTANCE.getPhysicsWorldInternal();
    }

    /**
     * Sets the robot body tracked by the simulation layer.
     *
     * @param robotBody the robot body to store
     */
    public static void setRobotBody(PhysicsBody robotBody) {
        INSTANCE.setRobotBodyInternal(robotBody);
    }

    /**
     * Returns the tracked robot body.
     *
     * @return the current robot body, or {@code null} if none is registered
     */
    public static PhysicsBody getRobotBody() {
        return INSTANCE.getRobotBodyInternal();
    }

    /**
     * Sets the telemetry publisher used to mirror simulation state.
     *
     * @param telemetryPublisher the telemetry publisher to store
     */
    public static void setTelemetryPublisher(FieldTelemetryPublisher telemetryPublisher) {
        INSTANCE.setTelemetryPublisherInternal(telemetryPublisher);
    }

    /**
     * Returns the tracked telemetry publisher.
     *
     * @return the current telemetry publisher, or {@code null} if none is registered
     */
    public static FieldTelemetryPublisher getTelemetryPublisher() {
        return INSTANCE.getTelemetryPublisherInternal();
    }

    /**
     * Applies a chassis-speed command to the tracked robot body.
     *
     * @param speeds the commanded chassis speeds
     */
    public static void setPhysicsVelocity(ChassisSpeeds speeds) {
        INSTANCE.setPhysicsVelocityInternal(speeds);
    }

    /**
     * Advances the tracked physics world and refreshes telemetry.
     */
    public static void stepPhysics() {
        INSTANCE.stepPhysicsInternal();
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

        return new SimRobot(id, stateRef);
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

    private synchronized void setPhysicsWorldInternal(PhysicsWorld physicsWorld) {
        this.physicsWorld = physicsWorld;
    }

    private synchronized PhysicsWorld getPhysicsWorldInternal() {
        return physicsWorld;
    }

    private synchronized void setRobotBodyInternal(PhysicsBody robotBody) {
        this.robotBody = robotBody;
    }

    private synchronized PhysicsBody getRobotBodyInternal() {
        return robotBody;
    }

    private synchronized void setTelemetryPublisherInternal(FieldTelemetryPublisher telemetryPublisher) {
        this.telemetryPublisher = telemetryPublisher;
    }

    private synchronized FieldTelemetryPublisher getTelemetryPublisherInternal() {
        return telemetryPublisher;
    }

    private synchronized void setPhysicsVelocityInternal(ChassisSpeeds speeds) {
        if (robotBody != null) {
            robotBody.setLinearVelocity(speeds.vxMetersPerSecond, speeds.vyMetersPerSecond, 0.0);
        }
    }

    private synchronized void stepPhysicsInternal() {
        if (physicsWorld != null) {
            physicsWorld.step();
            if (telemetryPublisher != null) {
                telemetryPublisher.publishFrame();
            }
        }
    }
}
