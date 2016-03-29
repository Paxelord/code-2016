package com.lynbrookrobotics.sixteen.sensors.vision;

import com.lynbrookrobotics.funkydashboard.TimeSeriesNumeric;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.config.constants.VisionConstants;
import com.lynbrookrobotics.sixteen.sensors.digitalgyro.DigitalGyro;
import com.lynbrookrobotics.sixteen.tasks.shooter.VisionReceiverActor;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;

public class VisionCalculation {
  public static DigitalGyro gyro = null;
  public static double targetAngle;
  public static double angularError;

  private static double yAbsoluteDegrees = 0;
  private static double robotToTower = 0;
  public static double DEG_TO_RAD = (Math.PI * 2) / 360;

  static {
    try {
      WakeOnLan.awaken(VisionConstants.NUC_MAC_ADDRESS);
    } catch (Throwable e) {
      e.printStackTrace();
    }

    RobotConstants.dashboard.thenAccept(dashboard -> {
      dashboard.datasetGroup("vision").addDataset(new TimeSeriesNumeric<>(
          "Angular Error",
          () -> angularError
      ));

      dashboard.datasetGroup("vision").addDataset(new TimeSeriesNumeric<>(
          "Y Absolute Degrees",
          () -> yAbsoluteDegrees
      ));

      dashboard.datasetGroup("vision").addDataset(new TimeSeriesNumeric<>(
          "Robot to Tower",
          () -> robotToTower
      ));
    });
  }

  private static ActorRef communicator =
      RobotConstants.system.actorOf(Props.create(new Creator<Actor>() {
        @Override
        public Actor create() throws Exception {
          return new VisionReceiverActor(
              null,
              target -> {
                if (gyro != null) {
                  double yDegreesFromCenter =
                      (VisionConstants.IMAGE_HEIGHT/2 - target._2) *
                          (VisionConstants.IMAGE_VERTICAL_FOV / VisionConstants.IMAGE_HEIGHT);
//                  System.out.println("yDegreesFromCenter = " + yDegreesFromCenter);
                  double yAbsoluteDegrees = yDegreesFromCenter + VisionConstants.CAMERA_TILT;
                  VisionCalculation.yAbsoluteDegrees = yAbsoluteDegrees;

                  double robotToTower =
                      (VisionConstants.CAMERA_TOWER_HEIGHT
                          / Math.tan(yAbsoluteDegrees * DEG_TO_RAD))
                          + VisionConstants.MIDDLE_TO_CAMERA_FORWARD;
                  double robotToGoal = Math.sqrt(
                      robotToTower*robotToTower
                          + VisionConstants.CAMERA_TOWER_HEIGHT*VisionConstants.CAMERA_TOWER_HEIGHT
                  );
                  double cameraToGoal = VisionConstants.CAMERA_TOWER_HEIGHT / Math.sin(yAbsoluteDegrees * DEG_TO_RAD);

                  VisionCalculation.robotToTower = robotToTower;
//                  System.out.println("robotToGoal = " + robotToGoal);
//                  System.out.println("cameraToGoal = " + cameraToGoal);

                  double xDegreesFromCenter =
                      (target._1 - VisionConstants.IMAGE_WIDTH/2) *
                          (VisionConstants.IMAGE_HORIZONTAL_FOV / VisionConstants.IMAGE_WIDTH);
//                  System.out.println("xDegreesFromCenter = " + xDegreesFromCenter);
                  double xCameraOffset = Math.tan(xDegreesFromCenter * DEG_TO_RAD) * cameraToGoal;
//                  System.out.println("xCameraOffset = " + xCameraOffset);
                  double xRobotOffset = xCameraOffset + VisionConstants.CAMERA_TO_MIDDLE_HORIZONTAL;
//                  System.out.println("xRobotOffset = " + xRobotOffset);

                  double xAngularOffset = (Math.atan(xRobotOffset / robotToGoal) / DEG_TO_RAD) + 13;
                  System.out.println("xAngularOffset = " + xAngularOffset);

                  angularError = xAngularOffset;
                  targetAngle = gyro.currentPosition().valueZ() + xAngularOffset;
                }
              }
          );
        }
      }));
}
