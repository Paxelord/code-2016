package com.lynbrookrobotics.sixteen.components.shooter.arm;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterSpinnersConstants;
import com.lynbrookrobotics.sixteen.control.pid.PID;

public abstract class ShooterArmPositionController extends ShooterArmController {
  private PID pid;
  private int currentPosition;
  private RobotHardware hardware;

  public ShooterArmPositionController(int targetPotPosition, RobotHardware hardware) {
    this.hardware = hardware;

    pid = new PID(() -> (double)currentPosition, (double)targetPotPosition)
                        .withP(ShooterArmConstants.P_GAIN)
                        .withI(ShooterArmConstants.I_GAIN, ShooterArmConstants.I_MEMORY); // TODO: experimentally determine PID factors
  }

  @Override
  public double armMotorSpeed()
  {
    currentPosition = (int) hardware.shooterArmHardware.potentiometer.getAngle();
    return pid.get() * ShooterArmConstants.CONVERSION_FACTOR; // TODO: experimentally determine conversion factor
  }
}
