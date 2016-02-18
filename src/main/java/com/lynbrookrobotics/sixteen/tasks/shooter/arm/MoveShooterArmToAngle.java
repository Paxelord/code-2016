package com.lynbrookrobotics.sixteen.tasks.shooter.arm;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArmPositionController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.sensors.encoder.Encoder;

/**
 * Moves Shooter to specified angle with an accepted error range of 2 degrees
 */
public class MoveShooterArmToAngle extends FiniteTask {
  private RobotHardware robotHardware;
  private Encoder encoder;
  private ShooterArm shooterArm;
  private int angle;

  /**
   * Constructor for the MoveShooterArmToAngle.
   * @param angle         the target angle
   * @param robotHardware the robot hardware
   * @param shooterArm    the shooter arm that has to be moved
   */
  public MoveShooterArmToAngle(int angle, RobotHardware robotHardware, ShooterArm shooterArm) {
    this.angle = angle;
    this.robotHardware = robotHardware;
    this.shooterArm = shooterArm;
    encoder = robotHardware.shooterArmHardware.encoder;


  }

  /**
   * Creates a ShooterArmPositionController object with the target angle and robotHardware,
   * and sets the controller of the shooterArm to it
   */
  @Override
  protected void startTask() {
    shooterArm.setController(new ShooterArmPositionController(angle, robotHardware));

  }

  /**
   * If the current angle of the shooter arm equals the target angle with an error of less than 3 degrees,
   * the task is finished
   */
  @Override
  protected void update() {
    if ( Math.abs(encoder.getAngle() - angle) < 3 ) {
      finished();
    }
  }

  /**
   * Now that the task is finished, reset the shooter arm
   */
  @Override
  protected void endTask() {
    shooterArm.resetToDefault();

  }
}
