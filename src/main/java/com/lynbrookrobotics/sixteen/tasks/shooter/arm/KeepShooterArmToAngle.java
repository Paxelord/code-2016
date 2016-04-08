package com.lynbrookrobotics.sixteen.tasks.shooter.arm;

import com.lynbrookrobotics.potassium.tasks.ContinuousTask;
import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArmController;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArmPositionController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;
import com.lynbrookrobotics.sixteen.sensors.potentiometer.Potentiometer;

/**
 * Moves Shooter to specified angle with an accepted error range of 2 degrees.
 */
public class KeepShooterArmToAngle extends ContinuousTask {
  private Potentiometer pot;
  private ShooterArmController controller;
  private ShooterArm shooterArm;
  private double angle;

  /**
   * Constructor for the MoveShooterArmToAngle.
   * @param angle         the target angle.
   * @param robotHardware the robot hardware.
   * @param shooterArm    the shooter arm that has to be moved.
   */
  public KeepShooterArmToAngle(double angle, RobotHardware robotHardware, ShooterArm shooterArm) {
    this.angle = angle;
    this.pot = robotHardware.shooterArmHardware.pot;
    this.controller = new ShooterArmPositionController(angle, robotHardware);
    this.shooterArm = shooterArm;
  }

  /**
   * Creates a ShooterArmPositionController object with
   * the target angle and robotHardware,
   * and sets the controller of the shooterArm to it.
   */
  @Override
  protected void startTask() {
    shooterArm.setController(controller);
  }

  /**
   * If the current angle of the shooter arm equals
   * the target angle with an error of less
   * than 3 degrees, the task is finished.
   */
  @Override
  protected void update() {
  }

  /**
   * Now that the task is finished,
   * reset the shooter arm.
   */
  @Override
  protected void endTask() {
    shooterArm.resetToDefault();
  }
}
