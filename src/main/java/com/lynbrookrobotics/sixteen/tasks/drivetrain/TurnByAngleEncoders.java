package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.drivetrain.TurnByAngleController;
import com.lynbrookrobotics.sixteen.components.drivetrain.TurnByAngleEncoderController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

public class TurnByAngleEncoders extends FiniteTask {
  RobotHardware hardware;
  Drivetrain drivetrain;

  double angle;

  TurnByAngleEncoderController controller;

  /**
   * Constructs a task to turn by a fixed angle.
   * @param angle the number of degrees to turn
   * @param hardware the hardware to use
   * @param drivetrain the drivetrain to use
   */
  public TurnByAngleEncoders(double angle, RobotHardware hardware, Drivetrain drivetrain) {
    this.hardware = hardware;
    this.drivetrain = drivetrain;

    this.angle = angle;
  }

  @Override
  protected void startTask() {
    controller = new TurnByAngleEncoderController(angle, hardware);
    drivetrain.setController(controller);
  }

  @Override
  protected void update() {
    double vel = hardware.drivetrainHardware.currentRotationVelocity();
    if (Math.abs(controller.difference()) <= 1 && Math.abs(vel) <= 1.0) {
      finished();
    }
  }

  @Override
  protected void endTask() {
    drivetrain.resetToDefault();
    controller = null;
  }
}
