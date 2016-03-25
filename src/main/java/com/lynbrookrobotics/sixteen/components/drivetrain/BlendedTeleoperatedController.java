package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;

import java.util.function.Supplier;

/**
 * A controller that blends outputs of turning in place controller (arcadeDrive)
 * and turning at a constant radius to make control drive intuitive.
 */
public class BlendedTeleoperatedController extends ClosedTankDriveController {
  Supplier<Double> forwardSpeed;
  Supplier<Double> curvature;

  ArcadeDriveController arcadeDriveController;
  ConstantTurnRadiusController constantTurnRadiusController;

  /**
   * Constructs a blended Blended Teloperatored Controller
   * @param turnInput -1 to 1, turning wheel input.
   */
  public BlendedTeleoperatedController(RobotHardware hardware,
                                       Supplier<Double> forwardSpeed,
                                       Supplier<Double> turnInput) {
    super(hardware);

    this.forwardSpeed = forwardSpeed;
    this.curvature = () -> turnInput.get() * DrivetrainConstants.MAX_CURVATURE;

    this.arcadeDriveController = ArcadeDriveController.of(hardware,
        forwardSpeed,
        turnInput);

    this.constantTurnRadiusController = new ConstantTurnRadiusController(
        hardware,
        forwardSpeed,
        turnInput);
  }

  /**
   *  Blend function is square root of forward speed, then weighted average of
   *  turn in place and constant radius outputs.
   * @param forwardSpeed How much to blend by based on forward speed
   * @param turnInPlaceOutput Turn in place output
   * @param constantRadiusOutput Turn at constant radius output
   * @return The blend of turn in place and constant radius outputs.
   */
  public double blend(double forwardSpeed, double turnInPlaceOutput,
                      double constantRadiusOutput) {
    double percentConstantTurn = Math.pow(forwardSpeed,
        DrivetrainConstants.BLEND_EXPONENT);

    return percentConstantTurn * constantRadiusOutput
        + (1 - percentConstantTurn) * turnInPlaceOutput;
  }

  /**
   * Sums blended turn in place controller (arcade controller) and
   * blended turning at a constant turn radius controller outputs for linear
   * velocity for the left wheels in feet per second.
   * @return The blended, normalized left velocity output of both controllers
   */
  @Override
  public double leftVelocity() {
    return blend(forwardSpeed.get(), arcadeDriveController.leftVelocity(),
        constantTurnRadiusController.leftVelocity());
  }

  /**
   * Sums blended turn in place controller (arcade controller) and
   * blended turning at a constant turn radius controller outputs for linear
   * velocity for the right wheels in feet per second.
   * @return The blended, normalized right velocity output of both controllers
   */
  @Override
  public double rightVelocity() {
    return blend(forwardSpeed.get(), arcadeDriveController.rightVelocity(),
        constantTurnRadiusController.rightVelocity());
  }
}
