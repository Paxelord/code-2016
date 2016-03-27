package com.lynbrookrobotics.sixteen.tasks.intake.arm;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArmAngleController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;

/**
 * A finite task that moves the intake arm to a certain angle.
 */
public class MoveIntakeArmToAngle extends FiniteTask {
  IntakeArm intakeArm;
  double targetAngle;
  RobotHardware robotHardware;

  /**
   * Constructs a finite task that takes the targetAnge, IntakeArm, and RobotHardware.
   *
   * @param targetAngle   The target angle for the Intake Arm.
   * @param intakeArm     The Intake Component.
   * @param robotHardware The robot hardware used in the Controller.
   */
  public MoveIntakeArmToAngle(double targetAngle, IntakeArm intakeArm,
                              RobotHardware robotHardware) {
    this.intakeArm = intakeArm;
    this.targetAngle = targetAngle;
    this.robotHardware = robotHardware;
  }

  private int goodTicks = 0;

  @Override
  protected void startTask() {
    goodTicks = 0;
    intakeArm.setController(new IntakeArmAngleController(targetAngle, robotHardware));
  }

  /**
   * If the Angle is within the error then finish the task.
   */
  @Override
  protected void update() {
    if (Math.abs(robotHardware.intakeArmHardware.pot.getAngle() - targetAngle)
        < IntakeArmConstants.ARM_ERROR) {
      goodTicks++;

      if (goodTicks >= 25) {
        finished();
      }
    }
  }

  @Override
  protected void endTask() {
    intakeArm.resetToDefault();
  }
}
