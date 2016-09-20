package com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheelSpeedController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

import static com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants.THRESHOLD_RPM;

public class SpinFlywheelToRPM extends FiniteTask {
  double targetRPM;
  ShooterFlywheel shooterFlywheel;
  RobotHardware hardware;

  ShooterFlywheelSpeedController controller;

  /**
   * Spins the spinner up to a given RPM and then ends.
   */
  public SpinFlywheelToRPM(double targetRPM,
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
