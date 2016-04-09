package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.DriveStraightController;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

/**
 * Finite task to drive to some position relative to the position at the time of startTask().
 */
public class DriveAbsolute extends FiniteTask {
  private static final double errorThresholdForward = 1D / 12;
  private static final double errorThresholdTurn = 3;

  double distance;
  RobotHardware hardware;
  DriveStraightController driveDistanceController;
  Drivetrain drivetrain;

  double maxSpeed;
  boolean wait;

  /**
   * Finite task to drive to some position relative to the starting position.
   * @param hardware Robot hardware
   * @param drivetrain The drivetrain component
   */
  public DriveAbsolute(RobotHardware hardware, double forwardDistance,
                       double maxSpeed, boolean wait,
                       Drivetrain drivetrain) {
    this.maxSpeed = maxSpeed;
    this.distance = forwardDistance;
    this.hardware = hardware;
    this.drivetrain = drivetrain;
    this.wait = wait;
  }

  /**
   * Finite task to drive to some position relative to the starting position.
   * @param hardware Robot hardware
   * @param drivetrain The drivetrain component
   */
  public DriveAbsolute(RobotHardware hardware, double forwardDistance,
                       double maxSpeed,
                       Drivetrain drivetrain) {
    this(hardware, forwardDistance, maxSpeed, false, drivetrain);
  }

  /**
   * Finite task to drive to some position relative to the starting position.
   * @param hardware Robot hardware
   * @param drivetrain The drivetrain component
   */
  public DriveAbsolute(RobotHardware hardware, double forwardDistance,
                       Drivetrain drivetrain) {
    this(hardware, forwardDistance, 1.0, drivetrain);
  }

  private int goodTicks = 0;

  @Override
  public void startTask() {
    driveDistanceController = new DriveStraightController(hardware,
        distance,
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

      if (goodTicks >= 25 || !wait) {
        finished();
      }
    }
  }

  @Override
  public void endTask() {
    drivetrain.resetToDefault();
  }
}
