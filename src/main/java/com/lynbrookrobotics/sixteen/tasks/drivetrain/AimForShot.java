package com.lynbrookrobotics.sixteen.tasks.drivetrain;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.drivetrain.TurnToAngleController;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.config.constants.VisionConstants;
import com.lynbrookrobotics.sixteen.sensors.digitalgyro.DigitalGyro;
import com.lynbrookrobotics.sixteen.tasks.shooter.VisionReceiverActor;

import java.net.InetSocketAddress;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;

public class AimForShot extends FiniteTask {
  private final DigitalGyro gyro;

  private final RobotHardware hardware;
  private final Drivetrain drivetrain;

  public AimForShot(RobotHardware hardware, Drivetrain drivetrain) {
    VisionConstants.gyro = hardware.drivetrainHardware.mainGyro;
    this.gyro = hardware.drivetrainHardware.mainGyro;
    this.hardware = hardware;
    this.drivetrain = drivetrain;
  }

  private TurnToAngleController control;
  @Override
  protected void startTask() {
    VisionConstants.angularError = Double.POSITIVE_INFINITY;
    VisionConstants.targetAngle = gyro.currentPosition().valueZ();
    control = new TurnToAngleController(
        () -> VisionConstants.targetAngle,
        hardware
    );
    drivetrain.setController(control);
  }

  @Override
  protected void update() {
    if (Math.abs(VisionConstants.angularError) <= 1 && Math.abs(control.difference()) <= 1) {
      finished();
    }
  }

  @Override
  protected void endTask() {
    drivetrain.resetToDefault();
  }
}
