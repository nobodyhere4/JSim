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
  /**
   * Internal rule record used to evaluate boolean enter and exit conditions.
   */
  private static final class Rule {
    /** The mode to activate when the enter condition becomes true. */
    final Mode mode;
    /** Supplies the condition that enables the rule. */
    final Supplier<Boolean> enter;
    /** Supplies the condition that disables the zone. */
    final Supplier<Boolean> exit;
    /** Supplies the exit rate in meters per second. */
    final Supplier<Double> exitRateMetersPerSecondSupplier;
    /** Supplies the exit rotation relative to the robot. */
    final Supplier<Rotation3d> rot;
    /** Supplies the exit translation relative to the robot. */
    final Supplier<Translation3d> trans;

    /**
     * Creates a rule that updates the zone when the enter or exit conditions are met.
     */
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

  /**
   * Configures a default intake rule using boolean enter/exit conditions.
   *
   * @param enterCondition supplies {@code true} when the rule should activate
   * @param exitCondition supplies {@code true} when the zone should disable
    * @param exitRateMetersPerSecondSupplier supplies the exit rate in meters per second
   * @param rotationSupplier supplies the exit rotation relative to the robot
   */
  public void configure(
      Supplier<Boolean> enterCondition,
      Supplier<Boolean> exitCondition,
      Supplier<Double> exitRateMetersPerSecondSupplier,
      Supplier<Rotation3d> rotationSupplier) {
    addRule(
        Mode.INTAKE,
        enterCondition,
        exitCondition,
        exitRateMetersPerSecondSupplier,
        rotationSupplier,
        null);
  }

  /**
   * Configures an automatic rule for the given mode.
   *
   * @param mode the interaction mode to enable when the enter condition is met
   * @param enterCondition supplies {@code true} when the rule should activate
   * @param exitCondition supplies {@code true} when the zone should disable
    * @param exitRateMetersPerSecondSupplier supplies the exit rate in meters per second
   * @param rotationSupplier supplies the exit rotation relative to the robot
   * @param translationSupplier supplies the exit translation relative to the robot
   */
  public void configure(
      Mode mode,
      Supplier<Boolean> enterCondition,
      Supplier<Boolean> exitCondition,
      Supplier<Double> exitRateMetersPerSecondSupplier,
      Supplier<Rotation3d> rotationSupplier,
      Supplier<Translation3d> translationSupplier) {
    addRule(
        mode,
        enterCondition,
        exitCondition,
        exitRateMetersPerSecondSupplier,
        rotationSupplier,
        translationSupplier);
  }

  /**
   * Adds an automatic rule for this zone.
   *
   * @param mode the interaction mode to set when the enter condition is met
   * @param enterCondition supplies {@code true} when the rule should activate
   * @param exitCondition supplies {@code true} when the zone should disable
    * @param exitRateMetersPerSecondSupplier supplies the exit rate in meters per second
   * @param rotationSupplier supplies the exit rotation relative to the robot
   * @param translationSupplier supplies the exit translation relative to the robot
   */
  public void addRule(
      Mode mode,
      Supplier<Boolean> enterCondition,
      Supplier<Boolean> exitCondition,
      Supplier<Double> exitRateMetersPerSecondSupplier,
      Supplier<Rotation3d> rotationSupplier,
      Supplier<Translation3d> translationSupplier) {
    rules.add(
        new Rule(
            mode,
            enterCondition,
            exitCondition,
            exitRateMetersPerSecondSupplier,
            rotationSupplier,
            translationSupplier));
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

  /**
   * Evaluates configured automatic rules and updates the zone mode and exit parameters.
   */
  public void evaluate() {
    for (var rule : rules) {
      if (rule.enter != null && rule.enter.get()) {
        this.mode = rule.mode;
        if (rule.exitRateMetersPerSecondSupplier != null) {
          this.exitRate = rule.exitRateMetersPerSecondSupplier.get();
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

  /**
   * Returns the configured exit rate in meters per second.
   *
   * @return the exit rate
   */
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

  /**
   * Returns the configured exit translation.
   *
   * @return the exit translation relative to the robot
   */
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