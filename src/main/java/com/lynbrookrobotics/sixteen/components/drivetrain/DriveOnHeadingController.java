package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.control.pid.PID;
import com.lynbrookrobotics.sixteen.sensors.digitalgyro.DigitalGyro;

import java.util.function.Supplier;

public class DriveOnHeadingController extends ClosedArcadeDriveController {
  private PID angleControl;

  RobotHardware hardware;
  DigitalGyro gyro;
  double targetAngle;
  Supplier<Double> forwardSpeed;

  /**
   * Constructs a new controller that drives on a fixed heading.
   * @param angle the absolute angle to drive on
   * @param speed a function that produces speeds to drive at
   * @param hardware the robot hardware to use
   */
  public DriveOnHeadingController(double angle, Supplier<Double> speed, RobotHardware hardware) {
    super(hardware);

    this.hardware = hardware;
    this.gyro = hardware.drivetrainHardware.mainGyro;
    this.targetAngle = angle;
    this.forwardSpeed = speed;

    this.angleControl = new PID(
        () -> hardware.drivetrainHardware.mainGyro.currentPosition().valueZ(),
        angle
    ).withP(1D / 90);
  }

  @Override
  public double forwardVelocity() {
    return forwardSpeed.get();
  }

  @Override
  public double turnVelocity() {
    return angleControl.get();
  }
}
