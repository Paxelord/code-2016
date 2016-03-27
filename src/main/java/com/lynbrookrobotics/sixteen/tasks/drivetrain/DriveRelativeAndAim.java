package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.DriveStraightController;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.VisionConstants;

/**
 * Finite task to drive to some position relative to the position at the time of startTask().
 */
public class DriveRelativeAndAim extends FiniteTask {
  private static final double errorThresholdForward = 1D/12;
  private static final double errorThresholdTurn = 5;

  private double distance;
  private RobotHardware hardware;
  private DriveStraightController driveDistanceController;
  private Drivetrain drivetrain;

  private double maxSpeed;

  /**
   * Finite task to drive to some position relative to the starting position.
   * @param hardware Robot hardware
   * @param drivetrain The drivetrain component
   */
  public DriveRelativeAndAim(RobotHardware hardware,
                             double forwardDistance,
                             double maxSpeed,
                             Drivetrain drivetrain) {
    VisionConstants.gyro = hardware.drivetrainHardware.mainGyro;
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
  public DriveRelativeAndAim(RobotHardware hardware, double forwardDistance,
                             Drivetrain drivetrain) {
    this(hardware, forwardDistance, 1.0, drivetrain);
  }

  private int goodTicks = 0;

  @Override
  public void startTask() {
    VisionConstants.angularError = Double.POSITIVE_INFINITY;
    VisionConstants.targetAngle = hardware.drivetrainHardware.mainGyro.currentPosition().valueZ();

    double currentAverage = hardware.drivetrainHardware.currentDistance();
    driveDistanceController = new DriveStraightController(hardware,
        currentAverage + distance,
        () -> VisionConstants.targetAngle,
        maxSpeed
    );
    drivetrain.setController(driveDistanceController);

    goodTicks = 0;
  }

  @Override
  public void update() {
    if (driveDistanceController.forwardError() < errorThresholdForward
        && Math.abs(driveDistanceController.angularError()) <= errorThresholdTurn) {
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
