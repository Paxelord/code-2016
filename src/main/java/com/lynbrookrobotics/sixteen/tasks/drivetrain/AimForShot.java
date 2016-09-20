package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.ClosedArcadeDriveController;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.VisionConstants;
import com.lynbrookrobotics.sixteen.sensors.vision.VisionCalculation;
import com.lynbrookrobotics.sixteen.sensors.vision.WakeOnLan;

public class AimForShot extends FiniteTask {
  private final RobotHardware hardware;
  private final Drivetrain drivetrain;

  /**
   * Constructs a task that aims the robot for a high goal shot.
   */
  public AimForShot(RobotHardware hardware, Drivetrain drivetrain) {
    WakeOnLan.awaken(VisionConstants.NUC_MAC_ADDRESS);
    System.out.println(VisionCalculation.angularError);

    this.hardware = hardware;
    this.drivetrain = drivetrain;
  }

  @Override
  protected void startTask() {
    VisionCalculation.angularError = Double.POSITIVE_INFINITY;
    drivetrain.setController(ClosedArcadeDriveController.of(
        hardware,
        () -> 0.0,
        () -> {
          System.out.println(VisionCalculation.angularError);
          if (Math.abs(VisionCalculation.angularError) > 60) {
            return 0.0;
          } else {
            return VisionCalculation.angularError * (1D / 60);
          }
        }
    ));
  }

  @Override
  protected void update() {
    double vel = hardware.drivetrainHardware.currentRotationVelocity();
    if (Math.abs(VisionCalculation.angularError) <= 1  && Math.abs(vel) <= 1.0) {
      finished();
    }
  }

  @Override
  protected void endTask() {
    drivetrain.resetToDefault();
  }
}
