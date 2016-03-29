package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.drivetrain.TurnToAngleController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.sensors.vision.VisionCalculation;
import com.lynbrookrobotics.sixteen.sensors.digitalgyro.DigitalGyro;

public class AimForShot extends FiniteTask {
  private final DigitalGyro gyro;

  private final RobotHardware hardware;
  private final Drivetrain drivetrain;

  public AimForShot(RobotHardware hardware, Drivetrain drivetrain) {
    VisionCalculation.gyro = hardware.drivetrainHardware.mainGyro;
    this.gyro = hardware.drivetrainHardware.mainGyro;
    this.hardware = hardware;
    this.drivetrain = drivetrain;
  }

  private int goodTicks = 0;
  private TurnToAngleController control;

  @Override
  protected void startTask() {
    VisionCalculation.angularError = Double.POSITIVE_INFINITY;
    VisionCalculation.targetAngle = gyro.currentPosition().valueZ();
    control = new TurnToAngleController(
        () -> VisionCalculation.targetAngle,
        hardware
    );
    drivetrain.setController(control);

    goodTicks = 0;
  }

  @Override
  protected void update() {
    if (Math.abs(VisionCalculation.angularError) <= 1 && Math.abs(control.difference()) <= 1) {
      goodTicks++;

      if (goodTicks >= 50) {
        finished();
      }
    } else {
      goodTicks = 0;
    }
  }

  @Override
  protected void endTask() {
    drivetrain.resetToDefault();
  }
}
