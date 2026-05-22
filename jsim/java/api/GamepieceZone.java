package api;

public class GamepieceZone {
    public enum Mode {
        /** Intake a gamepiece into the robot. */
    public class GamepieceZone {
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
        private final Transform3d[] zoneDimensions;
        private final Translation3d robotCenterOffset;
        private final Rotation3d robotRotation;
        private final SimRobot robot;
        private final String name;
        private final Transform3d[] zoneDimensions;
        private final Translation3d robotCenterOffset;
        private final Rotation3d robotRotation;
        private double exitVelocity;
        private Rotation3d exitRotation;
        private Mode mode = Mode.DISABLED;

        public GamepieceZone(SimRobot robot, String name, Transform3d[] zoneDimensions, Translation3d robotCenterOffset, Rotation3d robotRotation) {
            this.robot = robot;
            this.name = name;
            this.zoneDimensions = zoneDimensions;
            this.robotCenterOffset = robotCenterOffset;
            this.robotRotation = robotRotation;
        }

        public SimRobot getRobot() {
            return robot;
        }

        public String getName() {
            return name;
        }

        public Transform3d[] getZoneDimensions() {
            return zoneDimensions;
        }

        public Translation3d getRobotCenterOffset() {
            return robotCenterOffset;
        }

        public Rotation3d getRobotRotation() {
            return robotRotation;
        }

        public String getName() {
            return name;
        }

        public Transform3d[] getZoneDimensions() {
            return zoneDimensions;
        }

        public Translation3d getRobotCenterOffset() {
            return robotCenterOffset;
        }

        public Rotation3d getRobotRotation() {
            return robotRotation;
        }

        public void setMode(Mode mode) {
            this.mode = mode;
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
