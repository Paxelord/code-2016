package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;
import com.lynbrookrobotics.sixteen.control.pid.PID;

public abstract class ClosedTankDriveController extends DitheredTankDriveController {
  private final PID leftPID;
  private final PID rightPID;

  /**
   * Constructs a drivetrain controller that runs closed loop on each side.
   */
  public ClosedTankDriveController(RobotHardware hardware) {
    super(hardware);

    this.leftPID = new PID(
        () -> hardware.drivetrainHardware.leftEncoder.velocity.ground()
            / DrivetrainConstants.MAX_SPEED_FORWARD,
        this::leftVelocity
    ).withP(1D);

    this.rightPID = new PID(
        () -> hardware.drivetrainHardware.rightEncoder.velocity.ground()
            / DrivetrainConstants.MAX_SPEED_FORWARD,
        this::rightVelocity
    ).withP(1D);
  }

  public abstract double leftVelocity();

  public abstract double rightVelocity();

  @Override
  public double leftTarget() {
    return leftVelocity() + leftPID.get();
  }

  @Override
  public double rightTarget() {
    return rightVelocity() + rightPID.get();
  }
}
