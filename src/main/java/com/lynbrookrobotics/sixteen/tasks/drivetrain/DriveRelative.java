package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.DriveStraightController;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;

/**
 * Finite task to drive to some position relative to the position at the time of startTask().
 */
public class DriveRelative extends FiniteTask {
  private static final double errorThresholdForward = 1D / 12;
  private static final double errorThresholdTurn = 3;

  double distance;
  RobotHardware hardware;
  DriveStraightController driveDistanceController;
  Drivetrain drivetrain;

  double maxSpeed;

  /**
   * Finite task to drive to some position relative to the starting position.
   * @param hardware Robot hardware
   * @param drivetrain The drivetrain component
   */
  public DriveRelative(RobotHardware hardware, double forwardDistance,
                       double maxSpeed,
                       Drivetrain drivetrain) {
    this.maxSpeed = maxSpeed;
    this.distance = forwardDistance;
    this.hardware = hardware;
    this.drivetrain = drivetrain;
  }

  /**
   * Finite task to drive to some position relative to the starting position.
   * @param hardware Robot hardware
   * @param drivetrain The drivetrain component
   */
  public DriveRelative(RobotHardware hardware, double forwardDistance,
                       Drivetrain drivetrain) {
    this(hardware, forwardDistance, 1.0, drivetrain);
  }

  private int goodTicks = 0;

  @Override
  public void startTask() {
    double currentAverage = hardware.drivetrainHardware.currentDistance();
    driveDistanceController = new DriveStraightController(hardware,
        currentAverage + distance,
        hardware.drivetrainHardware.mainGyro.currentPosition().valueZ(),
        maxSpeed);
    drivetrain.setController(driveDistanceController);

    goodTicks = 0;
  }

  @Override
  public void update() {
    if (driveDistanceController.forwardError() < errorThresholdForward
        && driveDistanceController.angularError() < errorThresholdTurn) {
      goodTicks++;

      if (goodTicks >= 25) {
        finished();
      }
    }
  }

  @Override
  public void endTask() {
    drivetrain.resetToDefault();
  }
}
