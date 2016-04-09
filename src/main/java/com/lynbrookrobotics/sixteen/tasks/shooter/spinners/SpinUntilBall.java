package com.lynbrookrobotics.sixteen.tasks.shooter.spinners;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheelController;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.secondary.ShooterSecondary;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.secondary.ShooterSecondaryController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.ShooterConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants;
import com.lynbrookrobotics.sixteen.sensors.proximitysensor.ProximitySensor;

public class SpinUntilBall extends FiniteTask {
  ProximitySensor sensor;
//  ShooterFlywheelController controllerFlywheel;
  ShooterSecondaryController controllerSecondary;
//  ShooterFlywheel shooterFlywheel;
  ShooterSecondary shooterSecondary;

  /**
   * Task that spins until proximity sensor detects a ball.
   * @param hardware Robot Hardware
   * @param shooterFlywheel Shooter Flywheel component
   * @param shooterSecondary Shooter Secondary component
   */
  public SpinUntilBall(RobotHardware hardware,
                       ShooterFlywheel shooterFlywheel,
                       ShooterSecondary shooterSecondary) {
    this.sensor = hardware.shooterSpinnersHardware.proximitySensor;
//    this.controllerFlywheel = ShooterFlywheelController.of(
//        () -> -0.75);
    this.controllerSecondary = ShooterSecondaryController.of(
        () -> ShooterFlywheelConstants.INTAKE_POWER);
//    this.shooterFlywheel = shooterFlywheel;
    this.shooterSecondary = shooterSecondary;
  }


  @Override
  protected void startTask() {
//    shooterFlywheel.setController(controllerFlywheel);
    shooterSecondary.setController(controllerSecondary);
  }

  @Override
  protected void update() {
    if (sensor.isWithinDistance(ShooterConstants.BALL_PROXIMITY_THRESHOLD)) {
      finished();
    }
  }

  @Override
  protected void endTask() {
//    shooterFlywheel.resetToDefault();
    shooterSecondary.resetToDefault();
  }
}
