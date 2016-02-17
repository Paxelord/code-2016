package com.lynbrookrobotics.sixteen.tasks.shooter.spinners;

import static com.lynbrookrobotics.sixteen.config.constants.ShooterSpinnersConstants.THRESHOLD_RPM;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheelSpeedController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

public class SpinToRPM extends FiniteTask {
  double targetRPM;
  ShooterFlywheel shooterFlywheel;
  RobotHardware hardware;

  ShooterFlywheelSpeedController controller;

  /**
   * Spins the spinner up to a given RPM and then ends.
   */
  public SpinToRPM(double targetRPM,
                   ShooterFlywheel shooterFlywheel,
                   RobotHardware hardware) {
    this.targetRPM = targetRPM;
    this.shooterFlywheel = shooterFlywheel;
    this.hardware = hardware;
    this.controller = new ShooterFlywheelSpeedController(targetRPM, hardware);
  }

  @Override
  protected void startTask() {
    shooterFlywheel.setController(controller);
  }

  @Override
  protected void update() {
    if (Math.abs(controller.error()) <= THRESHOLD_RPM) {
      finished();
    }
  }

  @Override
  protected void endTask() {
    shooterFlywheel.resetToDefault();
  }
}
