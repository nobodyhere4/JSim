package jsim;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import edu.wpi.first.math.geometry.Translation2d;
import jsim.api.RobotID;
import jsim.api.SimRobot;
import jsim.api.StateManager;
import org.junit.jupiter.api.Test;

class JSimFacadeTest {
  @Test
  void getStateManagerReturnsSingleton() {
    assertSame(StateManager.getInstance(), JSim.getStateManager());
  }

  @Test
  void createRobotDelegatesToSharedStateManager() {
    Translation2d[] frameDimensions = new Translation2d[] {
      new Translation2d(0.0, 0.0),
      new Translation2d(0.5, 0.0),
      new Translation2d(0.5, 0.5)
    };

    SimRobot robot = JSim.createRobot(RobotID.BLUE_1, frameDimensions);

    assertNotNull(robot);
    assertEquals(RobotID.BLUE_1, robot.getRobotID());
  }
}