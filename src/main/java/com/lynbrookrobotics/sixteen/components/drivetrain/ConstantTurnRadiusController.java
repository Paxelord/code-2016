package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;

import java.util.function.Supplier;

/**
 * Drivetrain controller that calculates left and right wheel
 * velocities in feet per second for given curvature ( 1 / radius ) and
 * forward speed suppliers.
 */
public class ConstantTurnRadiusController extends ClosedTankDriveController {

  RobotHardware hardware;
  Supplier<Double> forwardSpeed;
  Supplier<Double> curvature;

  /**
   * Constructs a drivetrain controller that calculates left and right wheel
   * velocities in feet per second for given curvature ( 1 / radius ) and
   * forward speed suppliers.
   * @param hardware The robot hardware
   * @param forwardSpeed the normalized speed to move forward
   * @param curvature The curvature to turn at ( 1 / radius)
   */
  public ConstantTurnRadiusController(RobotHardware hardware,
                                      Supplier<Double> forwardSpeed,
                                      Supplier<Double> curvature) {
    super(hardware);

    this.hardware = hardware;
    this.forwardSpeed = forwardSpeed;
    this.curvature = curvature;
  }

  /**
   * Calculates the normalized linear speed of the right wheels
   * at the ground for a given forward speed and a constant curvature. AKA
   * calculates wheel speed while turning at a constant radius
   * @return The normalized speed of the left wheels
   */
  @Override
  public double leftVelocity() {
    return forwardSpeed.get() * ( 1D
        + curvature.get() * DrivetrainConstants.TRACK / 2 );
  }

  /**
   * Calculates the normalized linear speed of the right wheels
   * at the ground for a given forward speed and a constant curvature. AKA
   * calculates wheel speed while turning at a constant radius
   * @return The normalized speed of the right wheels
   */
  @Override
  public double rightVelocity() {
    return forwardSpeed.get() * ( 1D
        - curvature.get() * DrivetrainConstants.TRACK / 2 );
  }
}
