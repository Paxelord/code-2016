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
  private static DigitalGyro gyro = null;
  private static double targetAngle;
  private static double angularError;

  private static ActorRef communicator =
      RobotConstants.system.actorOf(Props.create((Creator<Actor>) () -> new VisionReceiverActor(
          new InetSocketAddress("localhost", 0),
          target -> {
            if (gyro != null) {
              double yDegreesFromCenter =
                  (target._2 - VisionConstants.IMAGE_HEIGHT/2) *
                      (VisionConstants.IMAGE_VERTICAL_FOV / VisionConstants.IMAGE_HEIGHT);
              System.out.println("yDegreesFromCenter = " + yDegreesFromCenter);
              double yAbsoluteDegrees = yDegreesFromCenter + VisionConstants.CAMERA_TILT;
              System.out.println("yAbsoluteDegrees = " + yAbsoluteDegrees);

              double robotToTower = VisionConstants.CAMERA_TOWER_HEIGHT / Math.tan(yAbsoluteDegrees);
              double cameraToGoal = VisionConstants.CAMERA_TOWER_HEIGHT / Math.sin(yAbsoluteDegrees);

              System.out.println("robotToTower = " + robotToTower);
              System.out.println("cameraToGoal = " + cameraToGoal);

              double xDegreesFromCenter =
                  (target._1 - VisionConstants.IMAGE_WIDTH/2) *
                      (VisionConstants.IMAGE_HORIZONTAL_FOV / VisionConstants.IMAGE_WIDTH);
              System.out.println("xDegreesFromCenter = " + xDegreesFromCenter);
              double xCameraOffset = Math.tan(xDegreesFromCenter) * cameraToGoal;
              System.out.println("xCameraOffset = " + xCameraOffset);
              double xRobotOffset = xCameraOffset + VisionConstants.CAMERA_TO_MIDDLE;
              System.out.println("xRobotOffset = " + xRobotOffset);

              double xAngularOffset = Math.atan(xRobotOffset / cameraToGoal);
              System.out.println("xAngularOffset = " + xAngularOffset);

              angularError = xAngularOffset;
              targetAngle = gyro.currentPosition().valueZ() + xAngularOffset;
            }
          }
      )));

  private final RobotHardware hardware;
  private final Drivetrain drivetrain;

  public AimForShot(RobotHardware hardware, Drivetrain drivetrain) {
    AimForShot.gyro = hardware.drivetrainHardware.mainGyro;
    this.hardware = hardware;
    this.drivetrain = drivetrain;
  }

  private TurnToAngleController control;
  @Override
  protected void startTask() {
    angularError = Double.POSITIVE_INFINITY;
    targetAngle = gyro.currentPosition().valueZ();
    control = new TurnToAngleController(
        () -> targetAngle,
        hardware
    );
    drivetrain.setController(control);
  }

  @Override
  protected void update() {
    System.out.println(angularError);
    if (angularError <= 10) {
      finished();
    }
  }

  @Override
  protected void endTask() {
    drivetrain.resetToDefault();
  }
}
