// Copyright (c) JSim contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the LGPLv3 license file in the root directory of this project.

package jsim.api;

import static edu.wpi.first.units.Units.MetersPerSecond;

import java.util.ArrayList;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.units.measure.LinearVelocity;
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

  private final SimRobot robot;
  private final String name;
  private final Transform3d[] zoneDimensions;
  private final Translation3d robotCenterOffset;
  private final Rotation3d robotRotation;
  private double exitRate = 0.0;
  private LinearVelocity exitVelocity = MetersPerSecond.of(0.0);
  private Rotation3d exitRotation = new Rotation3d();
  private Translation3d exitTranslation = new Translation3d();
  private Mode mode = Mode.DISABLED;
  private Supplier<Mode> modeSupplier;
  private Supplier<LinearVelocity> exitVelocitySupplier;
  private Supplier<Rotation3d> exitRotationSupplier;

  // Optional automatic configuration rules.
  private static final class Rule {
    final Mode mode;
    final Supplier<Boolean> enter;
    final Supplier<Boolean> exit;
    final Supplier<Double> rate;
    final Supplier<Rotation3d> rot;
    final Supplier<Translation3d> trans;

    Rule(
        Mode mode,
        Supplier<Boolean> enter,
        Supplier<Boolean> exit,
        Supplier<Double> rate,
        Supplier<Rotation3d> rot,
        Supplier<Translation3d> trans) {
      this.mode = mode;
      this.enter = enter;
      this.exit = exit;
      this.rate = rate;
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
    this(null, null, new Transform3d[0], new Translation3d(), new Rotation3d(), false);
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

  /**
   * Returns the robot associated with this zone.
   *
   * @return the owning simulated robot
   */
  public SimRobot getRobot() {
    return robot;
  }

  /**
   * Returns the configured zone name.
   *
   * @return the zone name
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the zone dimensions relative to the robot center.
   *
   * @return a copy of the zone dimensions
   */
  public Transform3d[] getZoneDimensions() {
    return zoneDimensions.clone();
  }

  /**
   * Returns the zone offset from the robot center.
   *
   * @return the robot-center offset for this zone
   */
  public Translation3d getRobotCenterOffset() {
    return robotCenterOffset;
  }

  /**
   * Returns the zone rotation relative to the robot.
   *
   * @return the robot-relative zone rotation
   */
  public Rotation3d getRobotRotation() {
    return robotRotation;
  }

  /**
   * Sets the zone interaction mode.
   *
   * @param mode the new zone mode
   */
  public void setMode(Mode mode) {
    this.mode = mode;
  }

  /**
   * Disables gamepiece interaction for this zone.
   */
  public void disable() {
    setMode(Mode.DISABLED);
  }

  /**
   * Sets the gamepiece exit parameters for this zone.
   *
    * @param velocity the launch velocity
    * @param rotation the launch rotation relative to the robot
   */
  public void setExitParameters(LinearVelocity velocity, Rotation3d rotation) {
    this.exitVelocity = velocity;
    this.exitRate = velocity.in(MetersPerSecond);
    this.exitRotation = rotation;
  }

  /**
   * Sets the gamepiece exit velocity with a default zero rotation.
   *
   * @param velocity the launch velocity
   */
  public void setExitParameters(LinearVelocity velocity) {
    setExitParameters(velocity, new Rotation3d());
  }

  /**
   * Sets the gamepiece exit parameters using a numeric exit rate and rotation.
   *
   * @param rate the launch rate in meters per second
   * @param rotation the launch rotation relative to the robot
   */
  public void setExitParameters(double rate, Rotation3d rotation) {
    this.exitRate = rate;
    this.exitVelocity = MetersPerSecond.of(rate);
    this.exitRotation = rotation;
  }

  /**
   * Sets the gamepiece exit parameters using a numeric exit rate, rotation, and translation.
   *
   * @param rate the launch rate in meters per second
   * @param rotation the launch rotation relative to the robot
   * @param translation the launch translation relative to the robot
   */
  public void setExitParameters(double rate, Rotation3d rotation, Translation3d translation) {
    this.exitRate = rate;
    this.exitVelocity = MetersPerSecond.of(rate);
    this.exitRotation = rotation;
    this.exitTranslation = translation;
  }

  /**
   * Configures the zone to intake gamepieces.
   *
    * @param velocity the launch velocity
    * @param rotation the launch rotation relative to the robot
   */
  public void intake(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.INTAKE);
  }

  /**
   * Configures the zone to outtake gamepieces.
   *
    * @param velocity the launch velocity
    * @param rotation the launch rotation relative to the robot
   */
  public void outtake(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.OUTTAKE);
  }

  /**
   * Configures the zone to shoot gamepieces.
   *
    * @param velocity the launch velocity
    * @param rotation the launch rotation relative to the robot
   */
  public void shoot(LinearVelocity velocity, Rotation3d rotation) {
    setExitParameters(velocity, rotation);
    setMode(Mode.SHOOT);
  }

  /**
   * Configures this zone from suppliers that can be refreshed each simulation step.
   *
    * @param modeSupplier supplies the current interaction mode relative to the robot
    * @param exitVelocitySupplier supplies the current exit velocity
    * @param exitRotationSupplier supplies the current exit rotation relative to the robot
   */
  public void configure(
      Supplier<Mode> modeSupplier,
      Supplier<LinearVelocity> exitVelocitySupplier,
      Supplier<Rotation3d> exitRotationSupplier) {
    this.modeSupplier = modeSupplier;
    this.exitVelocitySupplier = exitVelocitySupplier;
    this.exitRotationSupplier = exitRotationSupplier;
  }

  /** Configure a single automatic rule. */
  public void configure(
      Supplier<Boolean> enterCondition,
      Supplier<Boolean> exitCondition,
      Supplier<Double> exitRateSupplier,
      Supplier<Rotation3d> rotationSupplier) {
    addRule(Mode.INTAKE, enterCondition, exitCondition, exitRateSupplier, rotationSupplier, null);
  }

  public void configure(
      Mode mode,
      Supplier<Boolean> enterCondition,
      Supplier<Boolean> exitCondition,
      Supplier<Double> exitRateSupplier,
      Supplier<Rotation3d> rotationSupplier,
      Supplier<Translation3d> translationSupplier) {
    addRule(mode, enterCondition, exitCondition, exitRateSupplier, rotationSupplier, translationSupplier);
  }

  /** Add a rule that sets the zone to the provided mode when the enter condition is true. */
  public void addRule(
      Mode mode,
      Supplier<Boolean> enterCondition,
      Supplier<Boolean> exitCondition,
      Supplier<Double> exitRateSupplier,
      Supplier<Rotation3d> rotationSupplier,
      Supplier<Translation3d> translationSupplier) {
    rules.add(new Rule(mode, enterCondition, exitCondition, exitRateSupplier, rotationSupplier, translationSupplier));
  }

  /**
   * Refreshes this zone from its configured suppliers, if present.
   */
  public void refresh() {
    if (modeSupplier != null) {
      setMode(modeSupplier.get());
    }
    if (exitVelocitySupplier != null) {
      exitVelocity = exitVelocitySupplier.get();
      exitRate = exitVelocity.in(MetersPerSecond);
    }
    if (exitRotationSupplier != null) {
      exitRotation = exitRotationSupplier.get();
    }
    evaluate();
  }

  /** Evaluate configured rules and update the zone mode/params. Call from sim step. */
  public void evaluate() {
    for (var rule : rules) {
      if (rule.enter != null && rule.enter.get()) {
        this.mode = rule.mode;
        if (rule.rate != null) {
          this.exitRate = rule.rate.get();
          this.exitVelocity = MetersPerSecond.of(exitRate);
        }
        if (rule.rot != null) {
          this.exitRotation = rule.rot.get();
        }
        if (rule.trans != null) {
          this.exitTranslation = rule.trans.get();
        }
        return;
      }
      if (rule.exit != null && rule.exit.get()) {
        this.mode = Mode.DISABLED;
        return;
      }
    }
  }

  /**
   * Returns the configured exit velocity.
   *
   * @return the exit velocity in meters per second
   */
  public LinearVelocity getExitVelocity() {
    return exitVelocity;
  }

  /** Returns the configured exit rate in meters per second. */
  public double getExitRate() {
    return exitRate;
  }

  /**
   * Returns the configured exit rotation.
   *
   * @return the exit rotation
   */
  public Rotation3d getExitRotation() {
    return exitRotation;
  }

  /** Returns the configured exit translation. */
  public Translation3d getExitTranslation() {
    return exitTranslation;
  }

  /**
   * Returns the active interaction mode.
   *
   * @return the current mode
   */
  public Mode getMode() {
    return mode;
  }
}