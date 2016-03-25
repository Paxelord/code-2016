package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.potassium.components.Component;
import com.lynbrookrobotics.sixteen.config.DriverControls;
import com.lynbrookrobotics.sixteen.config.DrivetrainHardware;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;

import java.util.Optional;

/**
 * The component representing the drivetrain of the robot.
 * Made up of two independently controlled sides that allows for tank-style control.
 */
public class Drivetrain extends Component<DrivetrainController> {
  private final DrivetrainHardware hardware;
  private final DriverControls controls;
  private final DrivetrainController enabledDrive;

  /**
   * Constructs a new drivetrain component.
   * @param robotHardware the hardware to use
   */
  public Drivetrain(RobotHardware robotHardware, DriverControls controls) {
    super(DrivetrainController.of(() -> Optional.of(0.0), () -> Optional.of(0.0)));

    this.hardware = robotHardware.drivetrainHardware;
    this.controls = controls;

    this.enabledDrive = ClosedArcadeDriveController.of(
        robotHardware,
        () -> -controls.driverStick.getY(),
        controls.driverWheel::getX
    );
  }

  private double currentLimit(double targetPower, double currentSpeed) {
    // negation of value with negated parameters
    if (currentSpeed < 0) {
      return -currentLimit(-targetPower, -currentSpeed);
    }

    // At this point speed >= 0
    if (targetPower > currentSpeed) { // Current limit accelerating
      targetPower = Math.min(targetPower, currentSpeed + DrivetrainConstants.CURRENT_LIMIT_PERCENT);
    } else if (targetPower < 0) { // Current limit reversing direction
      // speed >= 0 so dutyCycle < -currentLimit
      double limitedDutyCycle = -DrivetrainConstants.CURRENT_LIMIT_PERCENT / (1.0 + currentSpeed);
      targetPower = Math.max(targetPower, limitedDutyCycle); // Both are negative
    }

    return targetPower;
  }

  @Override
  public void setOutputs(DrivetrainController drivetrainController) {
    final double currentLeftSpeed =
        hardware.leftEncoder.velocity.ground() / DrivetrainConstants.MAX_SPEED_FORWARD;
    final double currentRightSpeed =
        hardware.rightEncoder.velocity.ground() / DrivetrainConstants.MAX_SPEED_FORWARD;

    final double left = currentLimit(
        drivetrainController.leftPower().orElse(0.0),
        currentLeftSpeed);
    final double right = currentLimit(
        drivetrainController.rightPower().orElse(0.0),
        currentRightSpeed);

    hardware.frontLeftMotor.enableBrakeMode(!drivetrainController.leftPower().isPresent());
    hardware.backLeftMotor.enableBrakeMode(!drivetrainController.leftPower().isPresent());

    hardware.frontRightMotor.enableBrakeMode(!drivetrainController.rightPower().isPresent());
    hardware.backRightMotor.enableBrakeMode(!drivetrainController.rightPower().isPresent());

    hardware.frontLeftMotor.set(left);
    hardware.backLeftMotor.set(left);

    hardware.frontRightMotor.set(right);
    hardware.backRightMotor.set(right);
  }

  @Override
  public void resetToDefault() {
    if (controls != null
        && controls.driverStation.isEnabled()
        && controls.driverStation.isOperatorControl()) {
      setController(enabledDrive);
    } else {
      super.resetToDefault();
    }
  }
}
