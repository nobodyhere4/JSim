// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.api;

import static edu.wpi.first.units.Units.MetersPerSecond;

import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.LinearVelocity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Simulation-side gamepiece interaction zone.
 */
public class GamepieceZone {
  /**
   * Interaction mode for the zone.
   */
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

  /** Common zero rotation constant for examples and callers. */
  public static final Rotation3d ZERO_ROTATION = Rotation3d.kZero;

  /**
   * Helper to build an array of transform dimensions from a shared rotation and a list of
   * translations. This is a small convenience for example code and users constructing zones.
   *
   * @param rotation the rotation to apply to every translation
   * @param translations the vertex translations defining the zone polygon
   * @return transforms with the provided rotation applied to each translation
   */
  public static edu.wpi.first.math.geometry.Transform3d[] createZoneDimensions(
      edu.wpi.first.math.geometry.Rotation3d rotation,
      edu.wpi.first.math.geometry.Translation3d... translations) {
    edu.wpi.first.math.geometry.Transform3d[] transforms =
        new edu.wpi.first.math.geometry.Transform3d[translations.length];
    for (int i = 0; i < translations.length; i++) {
      transforms[i] = new edu.wpi.first.math.geometry.Transform3d(translations[i], rotation);
    }
    return transforms;
  }

  private final SimRobot robot;
  private final String name;
  private final Transform3d[] zoneDimensions;
  private final Translation3d robotCenterOffset;
  private final Rotation3d robotRotation;
  private double exitRate = 0.0;
  private LinearVelocity exitVelocity = MetersPerSecond.of(0.0);
  private Rotation3d exitRotation = Rotation3d.kZero;
  private Translation3d exitTranslation = new Translation3d();
  private Mode mode = Mode.DISABLED;
  private Supplier<Mode> modeSupplier;
  private Supplier<LinearVelocity> exitVelocitySupplier;
  private Supplier<Rotation3d> exitRotationSupplier;

  /**
   * Internal rule record used to evaluate boolean enter and exit conditions.
   */
  private static final class Rule {
    final Mode mode;
    final Supplier<Boolean> enter;
    final Supplier<Boolean> exit;
    final Supplier<Double> exitRateMetersPerSecondSupplier;
    final Supplier<Rotation3d> rot;
    final Supplier<Translation3d> trans;

    Rule(
        Mode mode,
        Supplier<Boolean> enter,
        Supplier<Boolean> exit,
        Supplier<Double> exitRateMetersPerSecondSupplier,
        Supplier<Rotation3d> rot,
        Supplier<Translation3d> trans) {
      this.mode = mode;
      this.enter = enter;
      this.exit = exit;
      this.exitRateMetersPerSecondSupplier = exitRateMetersPerSecondSupplier;
      this.rot = rot;
      this.trans = trans;
    }
  }

  private final List<Rule> rules = new ArrayList<>();

  /**
   * Compatibility constructor for example code that passes {@code null}.
   *
   * @param ignored compatibility placeholder
   */
  public GamepieceZone(Object ignored) {
    this(null, null, new Transform3d[0], new Translation3d(), Rotation3d.kZero, false);
  }

  /**
   * Creates a zone attached to a robot.
   *
   * @param robot the simulated robot that owns this zone
   * @param name the zone name used for retrieval from the robot
   * @param zoneDimensions the zone polygon transforms relative to the robot center
   * @param robotCenterOffset the zone offset from the robot center
   * @param robotRotation the zone rotation relative to the robot
   */
  GamepieceZone(
      SimRobot robot,
      String name,
      Transform3d[] zoneDimensions,
      Translation3d robotCenterOffset,
      Rotation3d robotRotation) {
    this(robot, name, zoneDimensions, robotCenterOffset, robotRotation, true);
  }

  private GamepieceZone(
      SimRobot robot,
      String name,
      Transform3d[] zoneDimensions,
      Translation3d robotCenterOffset,
      Rotation3d robotRotation,
      boolean register) {
    this.robot = robot;
    this.name = name;
    this.zoneDimensions = zoneDimensions == null ? new Transform3d[0] : zoneDimensions.clone();
    this.robotCenterOffset = robotCenterOffset;
    this.robotRotation = robotRotation;
    if (register) {
      StateManager.getInstance().registerGamepieceZone(this);
    }
  }

  public SimRobot getRobot() {
    return robot;
  }

  public String getName() {
    return name;
  }

  public Transform3d[] getZoneDimensions() {
    return zoneDimensions.clone();
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

  public void disable() {
    setMode(Mode.DISABLED);
  }

  public void setExitParameters(LinearVelocity velocity, Rotation3d rotation) {
    this.exitVelocity = velocity;
    this.exitRate = velocity.in(MetersPerSecond);
    this.exitRotation = rotation;
  }

  public void setExitParameters(LinearVelocity velocity) {
    setExitParameters(velocity, Rotation3d.kZero);
  }

  public void intake(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.INTAKE);
  }

  public void outtake(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.OUTTAKE);
  }

  public void shoot(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.SHOOT);
  }

  public void configure(
      Supplier<Mode> modeSupplier,
      Supplier<LinearVelocity> exitVelocitySupplier,
      Supplier<Rotation3d> exitRotationSupplier) {
    this.modeSupplier = modeSupplier;
    this.exitVelocitySupplier = exitVelocitySupplier;
    this.exitRotationSupplier = exitRotationSupplier;
  }

  /**
   * Configures a rule-driven zone state.
   *
   * @param mode the mode to activate while the enter condition is true
   * @param enter supplies whether the zone should activate
   * @param exit supplies whether the zone should disable
   * @param exitRateMetersPerSecondSupplier supplies the exit rate in meters per second
   * @param rot supplies the exit rotation
   * @param trans supplies the exit translation
   */
  public void configure(
      Mode mode,
      Supplier<Boolean> enter,
      Supplier<Boolean> exit,
      Supplier<Double> exitRateMetersPerSecondSupplier,
      Supplier<Rotation3d> rot,
      Supplier<Translation3d> trans) {
    rules.add(new Rule(mode, enter, exit, exitRateMetersPerSecondSupplier, rot, trans));
  }

  public double getExitRate() {
    return exitRate;
  }

  public LinearVelocity getExitVelocity() {
    if (exitVelocitySupplier != null) {
      return exitVelocitySupplier.get();
    }
    return exitVelocity;
  }

  public Rotation3d getExitRotation() {
    if (exitRotationSupplier != null) {
      return exitRotationSupplier.get();
    }
    return exitRotation;
  }

  public Translation3d getExitTranslation() {
    return exitTranslation;
  }

  public void refresh() {
    if (modeSupplier != null) {
      mode = modeSupplier.get();
    }
  }

  /**
   * Evaluates rule-driven zone updates.
   *
   * <p>If a rule's enter condition is true, the zone is set to the rule's mode and its exit
   * parameters are refreshed from the configured suppliers. If the exit condition is true, the
   * zone is disabled.
   */
  public void evaluate() {
    for (Rule rule : rules) {
      if (rule.exit != null && rule.exit.get()) {
        disable();
        continue;
      }

      if (rule.enter != null && rule.enter.get()) {
        mode = rule.mode;
        if (rule.exitRateMetersPerSecondSupplier != null) {
          exitRate = rule.exitRateMetersPerSecondSupplier.get();
          exitVelocity = MetersPerSecond.of(exitRate);
        }
        if (rule.rot != null) {
          exitRotation = rule.rot.get();
        }
        if (rule.trans != null) {
          exitTranslation = rule.trans.get();
        }
      }
    }
  }

  public Mode getMode() {
    return modeSupplier != null ? modeSupplier.get() : mode;
  }
}
