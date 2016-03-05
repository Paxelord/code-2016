package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.potassium.components.Component;
import com.lynbrookrobotics.sixteen.config.DriverControls;
import com.lynbrookrobotics.sixteen.config.DrivetrainHardware;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;

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
    super(DrivetrainController.of(() -> 0.0, () -> 0.0));

    this.hardware = robotHardware.drivetrainHardware;
    this.controls = controls;

    this.enabledDrive = ArcadeDriveController.of(
        robotHardware,
        () -> -controls.driverStick.getY(),
        () -> controls.driverWheel.getX()
    );
  }

  private boolean forceBrake = false;

  public void toggleForceBrake() {
    forceBrake = !forceBrake;
  }

  public void setForceBrake(boolean brake) {
    forceBrake = brake;
  }

  private int ditheredTick = 0;
  @Override
  public void setOutputs(DrivetrainController drivetrainController) {
    double left = drivetrainController.leftSpeed();
    double right = drivetrainController.rightSpeed();

    if (controls.driverStation.isEnabled()) {
      if ((ditheredTick++ % 20) == 0 || forceBrake) {
        hardware.frontLeftMotor.enableBrakeMode(true);
        hardware.backLeftMotor.enableBrakeMode(true);
        hardware.frontRightMotor.enableBrakeMode(true);
        hardware.backRightMotor.enableBrakeMode(true);
      } else {
        hardware.frontLeftMotor.enableBrakeMode(false);
        hardware.backLeftMotor.enableBrakeMode(false);
        hardware.frontRightMotor.enableBrakeMode(false);
        hardware.backRightMotor.enableBrakeMode(false);
      }
    }

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
