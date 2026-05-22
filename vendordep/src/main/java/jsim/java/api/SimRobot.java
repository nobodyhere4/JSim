package api;


import api.Pose2d;
import api.ChassisSpeeds;
import api.Transform3d;
import api.Translation2d;
import api.Translation3d;
import api.Rotation3d;
import api.RobotID;
import api.RobotState;
import api.FieldState;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import frc.robot.sim.core.RobotStateManager;
import frc.robot.sim.core.GamepieceStateManager;

/**
 * Minimal simulation-side robot representation used by example code.
 */

@SuppressWarnings("unused")
public final class SimRobot {

    private final RobotID robotID;
    private final RobotStateManager stateManager;
    private final GamepieceStateManager gamepieceStateManager;
    private final FieldState<RobotState> robotStateRef;
    private final Map<String, GamepieceZone> gamepieceZones = new HashMap<>();

    private SimRobot(
            RobotID robotID,
            RobotStateManager stateManager,
            GamepieceStateManager gamepieceStateManager,
            FieldState<RobotState> robotStateRef
    ) {
        this.robotID = robotID;
        this.stateManager = stateManager;
        this.gamepieceStateManager = gamepieceStateManager;
        this.robotStateRef = robotStateRef;
    }

    public static SimRobot createRobot(
            Translation2d[] frameDimensions,
            RobotStateManager stateManager,
            GamepieceStateManager gamepieceStateManager,
            RobotID robotID
    ) {
        FieldState<RobotState> ref =
                stateManager.initializeRobot(
                        robotID,
                        new Pose2d(0, 0, 0),
                        frameDimensions
                );

        return new SimRobot(robotID, stateManager, gamepieceStateManager, ref);
    }

    public GamepieceZone createGamepieceZone(
            String name,
            Transform3d[] zoneDimensions,
            Translation3d robotCenterOffset,
            Rotation3d robotRotation) {
        GamepieceZone zone = new GamepieceZone(this, name, zoneDimensions, robotCenterOffset, robotRotation);
        if (name != null) {
            gamepieceZones.put(name, zone);
        }
        return zone;
    }

    public GamepieceZone getGamepieceZone(String name) {
        return gamepieceZones.get(name);
    }

    public Map<String, GamepieceZone> getGamepieceZones() {
        return Collections.unmodifiableMap(gamepieceZones);
    }

    // Robot control (state)
    public Pose2d getPose() {
        return stateManager.getRobotPose(robotID);
    }

    public void resetPose(Pose2d pose) {
        stateManager.resetRobotPose(robotID, pose);
    }

    public void setChassisSpeeds(ChassisSpeeds speeds) {
        stateManager.setChassisSpeeds(robotID, speeds);
    }

    public RobotState getRobotState() {
        return stateManager.getFieldState(robotID).getState();
    }

    public void setRobotState(RobotState state) {
        stateManager.getFieldState(robotID).setState(state);
    }

    // Game piece interaction
    public boolean intake(GamePieceState piece) {
        return gamepieceStateManager.intake(robotID, piece);
    }

    public boolean outtake(GamePieceType type, double velocity, Rotation3d rotation) {
        return gamepieceStateManager.outtake(robotID, type, velocity, rotation);
    }

    // Identity / refs
    public RobotID getRobotID() {
        return robotID;
    }

    public FieldState<RobotState> getRobotStateRef() {
        return robotStateRef;
    }
}