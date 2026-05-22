package jsim.api;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;

public class RobotState {

    public Pose2d pose;
    public final Translation2d[] frameVertices;
    public ChassisSpeeds chassisSpeeds;
    public final RobotID id;

    public RobotState(RobotID id, Pose2d pose, Translation2d[] frameVertices) {
        this.id = id;
        this.pose = pose;
        this.frameVertices = frameVertices;
        this.chassisSpeeds = new ChassisSpeeds(0, 0, 0);
    }
}
