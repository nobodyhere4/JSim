package jsim.api;

/**
 * Simple chassis velocity container.
 */
public class ChassisSpeeds {
    /** Linear velocity in the robot X direction. */
    public double vx, vy, omega;

    /** Linear velocity in the robot Y direction. */
    /** Angular velocity about the vertical axis. */
    public ChassisSpeeds(double vx, double vy, double omega) {
        this.vx = vx;
        this.vy = vy;
        this.omega = omega;
    }
}
