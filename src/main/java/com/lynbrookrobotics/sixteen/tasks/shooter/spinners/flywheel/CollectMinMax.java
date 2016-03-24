package com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel;

import com.lynbrookrobotics.potassium.tasks.ContinuousTask;
import com.lynbrookrobotics.sixteen.components.lights.LightsController;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheelSpeedController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;

public class CollectMinMax extends ContinuousTask {
  RobotHardware hardware;

  /**
   * Displays the max and min RPM seen during execution of the task.
   */
  public CollectMinMax(RobotHardware hardware) {
    this.hardware = hardware;
  }

  double min = Double.POSITIVE_INFINITY;
  double max = 0;

  @Override
  protected void startTask() {
    min = Double.POSITIVE_INFINITY;
    max = 0;
  }

  @Override
  protected void update() {
    double currentRPM = hardware.shooterSpinnersHardware.hallEffect.rawOutput();

    min = Math.min(min, currentRPM);
    max = Math.max(min, currentRPM);
  }

  @Override
  protected void endTask() {
    logger.info(String.format("Shot flywheel speeds: max - %.2f, min - %.2f", max, min));
  }
}
