package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.drivetrain.TurnByAngleController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

public class TurnByAngle extends FiniteTask {
  RobotHardware hardware;
  Drivetrain drivetrain;

  double angle;

  TurnByAngleController controller;

  /**
   * Constructs a task to turn by a fixed angle.
   * @param angle the number of degrees to turn
   * @param hardware the hardware to use
   * @param drivetrain the drivetrain to use
   */
  public TurnByAngle(double angle, RobotHardware hardware, Drivetrain drivetrain) {
    this.hardware = hardware;
    this.drivetrain = drivetrain;

    this.angle = angle;
  }

  private int goodTicks = 0;

  @Override
  protected void startTask() {
    controller = new TurnByAngleController(angle, hardware);
    drivetrain.setController(controller);

    goodTicks = 0;
  }

  @Override
  protected void update() {
    if (controller.difference() < 5) {
      goodTicks++;

      if (goodTicks >= 25) {
        finished();
      }
    }
  }

  @Override
  protected void endTask() {
    drivetrain.resetToDefault();
    controller = null;
  }
}
