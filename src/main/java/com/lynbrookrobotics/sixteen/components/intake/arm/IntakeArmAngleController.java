package com.lynbrookrobotics.sixteen.components.intake.arm;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.control.pid.PID;

/**
 * The Controller that moves the intake arm to a certain angle.
 */
public class IntakeArmAngleController extends IntakeArmController {
  PID pid;
  RobotHardware robotHardware;
  boolean closeToMax;

  /**
   * The IntakeArmAngleController uses the target angle, and robotHardware
   * to create Controller.
   * @param targetAngle   Target Angle for the intake component.
   * @param robotHardware The RobotHardware to get current angle of comp..
   */
  public IntakeArmAngleController(double targetAngle, RobotHardware robotHardware) {
    this.robotHardware = robotHardware;
    this.closeToMax = Math.abs(targetAngle - IntakeArmConstants.FORWARD_LIMIT) <= 5;
    pid = new PID(robotHardware.intakeArmHardware.pot::getAngle, targetAngle)
        .withP(IntakeArmConstants.P_GAIN)
        .withDeadband(IntakeArmConstants.ARM_ERROR);
  }

  /**
   * Updates the pid to a current position to the current angle.
   *
   * @return the angle created by the PID.
   */
  @Override
  public double armSpeed() {
    if (closeToMax) {
      return RobotConstants.clamp(pid.get(), -0.2, 0.2);
    } else {
      return pid.get();
    }
  }
}
