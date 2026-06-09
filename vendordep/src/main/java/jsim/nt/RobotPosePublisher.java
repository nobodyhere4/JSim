// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.nt;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.DoubleArrayPublisher;
import edu.wpi.first.networktables.StringArrayPublisher;
import edu.wpi.first.networktables.StructArrayPublisher;
import edu.wpi.first.networktables.StructPublisher;
import jsim.api.RobotID;
import jsim.api.SimRobot;
import jsim.api.StateManager;

/**
 * Publishes JSim robot poses to NetworkTables in an AdvantageScope-friendly format.
 *
 * <p>Publishes:
 * <ul>
 *   <li>Robot id labels: {@code /JSim/RobotPose/robotIds}</li>
 *   <li>Robot poses as Pose3d array: {@code /JSim/RobotPose/robotPoses}</li>
 *   <li>Robot poses as flat XYtheta arrays: {@code /JSim/RobotPose/robotPose3Flat}</li>
 *   <li>Robot poses to field2d (AdvantageScope): {@code /field2d/JSim}</li>
 * </ul>
 */
public class RobotPosePublisher implements AutoCloseable {
  private static final String DEFAULT_BASE_TOPIC = "JSim/RobotPose";
  private static final String FIELD2D_TOPIC = "field2d/JSim";

  private final StructArrayPublisher<Pose3d> robotPosesPublisher;
  private final StringArrayPublisher robotIdsPublisher;
  private final DoubleArrayPublisher robotPoseFlatPublisher;
  private final StructPublisher<Pose2d> field2dPublisher;

  /**
   * Creates a new robot pose publisher under {@code /JSim/RobotPose}.
   */
  public RobotPosePublisher() {
    this(NetworkTableInstance.getDefault(), DEFAULT_BASE_TOPIC);
  }

  /**
   * Creates a new robot pose publisher.
   *
   * @param ntInstance network table instance
   * @param baseTopic base topic path (e.g. {@code /JSim/RobotPose})
   */
  public RobotPosePublisher(NetworkTableInstance ntInstance, String baseTopic) {
    NetworkTable table = ntInstance.getTable(baseTopic);
    this.robotPosesPublisher = table.getStructArrayTopic("robotPoses", Pose3d.struct).publish();
    this.robotIdsPublisher = table.getStringArrayTopic("robotIds").publish();
    this.robotPoseFlatPublisher = table.getDoubleArrayTopic("robotPose3Flat").publish();
    
    // Initialize field2d publisher for AdvantageScope visualization
    // Use a no-arg publish and cast to the expected publisher type to match
    // the NetworkTables Java API surface across versions.
    this.field2dPublisher = (StructPublisher<Pose2d>) ntInstance.getTopic(FIELD2D_TOPIC).publish();
  }

  /**
   * Publishes the current robot poses and names for all registered JSim robots.
   *
   * @return the number of robots published
   */
  public int publishFrame() {
    var robots = StateManager.getInstance().getRobots();
    int count = robots.size();
    if (count == 0) {
      robotIdsPublisher.set(new String[0]);
      robotPosesPublisher.set(new Pose3d[0]);
      robotPoseFlatPublisher.set(new double[0]);
      return 0;
    }

    Pose3d[] poses = new Pose3d[count];
    String[] ids = new String[count];
    double[] flat = new double[count * 3];

    int index = 0;
    Pose2d primaryPose = null;
    for (var entry : robots.entrySet()) {
      RobotID robotID = entry.getKey();
      SimRobot robot = entry.getValue();
      Pose2d pose2d = robot.getPose();
      ids[index] = robotID.name();

      poses[index] = new Pose3d(
          new Translation3d(pose2d.getX(), pose2d.getY(), 0.0),
          new Rotation3d(0.0, 0.0, pose2d.getRotation().getRadians()));

      int base = index * 3;
      flat[base] = pose2d.getX();
      flat[base + 1] = pose2d.getY();
      flat[base + 2] = pose2d.getRotation().getRadians();
      
      // Capture the first robot's pose for field2d publication
      if (index == 0) {
        primaryPose = pose2d;
      }
      
      index++;
    }

    robotIdsPublisher.set(ids);
    robotPosesPublisher.set(poses);
    robotPoseFlatPublisher.set(flat);
    
    // Publish primary robot pose to field2d for AdvantageScope
    if (primaryPose != null) {
      field2dPublisher.set(primaryPose);
    }
    
    return count;
  }

  @Override
  public void close() {
    robotIdsPublisher.close();
    robotPosesPublisher.close();
    robotPoseFlatPublisher.close();
    field2dPublisher.close();
  }
}
