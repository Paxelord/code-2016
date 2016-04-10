package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.control.pid.PID;

import java.util.function.Supplier;

/**
 * A controller that drives to an absolute position of the robot, AKA origin.
 */
public class DriveStraightController extends ClosedArcadeDriveController {
  RobotHardware hardware;

  private PID forwardControl;
  private PID turningControl;

  private double maxSpeed;

  /**
   * Constructs a controller that drives to an absolute position.
   * @param hardware robot hardware to use
   * @param targetDistance the distance to drive to
   * @param targetAngle the angle to drive to
   * @param maxSpeed the maximum forward speed
   */
  public DriveStraightController(RobotHardware hardware,
                                 double targetDistance,
                                 Supplier<Double> targetAngle,
                                 double maxSpeed) {
    super(hardware);

    this.hardware = hardware;
    this.maxSpeed = maxSpeed;

    this.forwardControl = new PID(
        hardware.drivetrainHardware::currentDistance,
        targetDistance)
        .withP(0.5D / 1D);

    this.turningControl = new PID(
        () -> hardware.drivetrainHardware.mainGyro.currentPosition().valueZ(),
        targetAngle)
        .withP(1D / 90).withI(3D / (90), 0.4);
  }

  /**
   * Constructs a controller that drives to an absolute position.
   * @param hardware robot hardware to use
   * @param targetDistance the distance to drive to
   * @param targetAngle the angle to drive to
   * @param maxSpeed the maximum forward speed
   */
  public DriveStraightController(RobotHardware hardware,
                                 double targetDistance,
                                 double targetAngle,
                                 double maxSpeed) {
    this(hardware, targetDistance, () -> targetAngle, maxSpeed);
  }

  /**
   * Controller that drives to an absolute position.
   * @param hardware The robot hardware
   * @param forwardTarget the distance to drive to
   * @param turnTarget the angle to drive to
   */
  public DriveStraightController(RobotHardware hardware, double forwardTarget,
                                 double turnTarget) {
    this(hardware, forwardTarget, turnTarget, 1.0);
  }

  @Override
  public double forwardVelocity() {
    return RobotConstants.clamp(forwardControl.get(), -maxSpeed, maxSpeed);
  }

  @Override
  public double turnVelocity() {
    return turningControl.get();
  }

  public double forwardError() {
    return Math.abs(forwardControl.difference());
  }

  public double angularError() {
    return Math.abs(turningControl.difference());
  }
}
