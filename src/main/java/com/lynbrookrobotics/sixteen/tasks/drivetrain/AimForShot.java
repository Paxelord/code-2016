package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.drivetrain.TurnToAngleController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.sensors.digitalgyro.DigitalGyro;
import com.lynbrookrobotics.sixteen.sensors.vision.VisionCalculation;

public class AimForShot extends FiniteTask {
  private final DigitalGyro gyro;

  private final RobotHardware hardware;
  private final Drivetrain drivetrain;

  /**
   * Constructs a task that aims the robot for a high goal shot.
   */
  public AimForShot(RobotHardware hardware, Drivetrain drivetrain) {
    VisionCalculation.gyro = hardware.drivetrainHardware.mainGyro;
    this.gyro = hardware.drivetrainHardware.mainGyro;
    this.hardware = hardware;
    this.drivetrain = drivetrain;
  }

  private TurnToAngleController control;
  private int countdown = 0;
  private long startTime;

  @Override
  protected void startTask() {
    VisionCalculation.angularError = Double.POSITIVE_INFINITY;
    VisionCalculation.targetAngle = gyro.currentPosition().valueZ();
    countdown = 25;
    startTime = System.currentTimeMillis();
    control = new TurnToAngleController(
        () -> VisionCalculation.targetAngle,
        hardware
    );
    drivetrain.setController(control);
  }

  @Override
  protected void update() {
    if (System.currentTimeMillis() >= startTime + 2000) {
      finished();
    } else if (Math.abs(VisionCalculation.angularError) <= 1 && Math.abs(control.difference()) <= 1) {
      countdown--;

      if (countdown <= 0) {
        finished();
      }
    }
  }

  @Override
  protected void endTask() {
    drivetrain.resetToDefault();
  }
}
