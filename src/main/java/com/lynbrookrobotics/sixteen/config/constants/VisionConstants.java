package com.lynbrookrobotics.sixteen.config.constants;

import com.lynbrookrobotics.sixteen.sensors.digitalgyro.DigitalGyro;
import com.lynbrookrobotics.sixteen.tasks.shooter.VisionReceiverActor;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.japi.Creator;

public class VisionConstants {
  public static DigitalGyro gyro = null;
  public static double targetAngle;
  public static double angularError;

  public static double DEG_TO_RAD = (Math.PI * 2)/360;

  private static ActorRef communicator =
      RobotConstants.system.actorOf(Props.create(new Creator<Actor>() {
        @Override
        public Actor create() throws Exception {
          return new VisionReceiverActor(
              null,
              target -> {
                if (gyro != null) {
                  double yDegreesFromCenter =
                      (target._2 - VisionConstants.IMAGE_HEIGHT/2) *
                          (VisionConstants.IMAGE_VERTICAL_FOV / VisionConstants.IMAGE_HEIGHT);
//                  System.out.println("yDegreesFromCenter = " + yDegreesFromCenter);
                  double yAbsoluteDegrees = yDegreesFromCenter + VisionConstants.CAMERA_TILT;
//                  System.out.println("yAbsoluteDegrees = " + yAbsoluteDegrees);

                  double robotToTower = VisionConstants.CAMERA_TOWER_HEIGHT / Math.tan(yAbsoluteDegrees * DEG_TO_RAD);
                  double cameraToGoal = VisionConstants.CAMERA_TOWER_HEIGHT / Math.sin(yAbsoluteDegrees * DEG_TO_RAD);

//                  System.out.println("robotToTower = " + robotToTower);
//                  System.out.println("cameraToGoal = " + cameraToGoal);

                  double xDegreesFromCenter =
                      (target._1 - VisionConstants.IMAGE_WIDTH/2) *
                          (VisionConstants.IMAGE_HORIZONTAL_FOV / VisionConstants.IMAGE_WIDTH);
//                  System.out.println("xDegreesFromCenter = " + xDegreesFromCenter);
                  double xCameraOffset = Math.tan(xDegreesFromCenter * DEG_TO_RAD) * cameraToGoal;
//                  System.out.println("xCameraOffset = " + xCameraOffset);
                  double xRobotOffset = xCameraOffset + VisionConstants.CAMERA_TO_MIDDLE;
//                  System.out.println("xRobotOffset = " + xRobotOffset);

                  double xAngularOffset = (Math.atan(xRobotOffset / cameraToGoal) / DEG_TO_RAD) + 15;
                  System.out.println("xAngularOffset = " + xAngularOffset);

                  angularError = xAngularOffset;
                  targetAngle = gyro.currentPosition().valueZ() + xAngularOffset;
                }
              }
          );
        }
      }));

  public static final int IMAGE_HEIGHT = 720;
  public static final int IMAGE_WIDTH = 1280;

  public static final double IMAGE_VERTICAL_FOV = 33.6;
  public static final double IMAGE_HORIZONTAL_FOV = 59.7;

  public static final double CAMERA_TILT = 36;

  public static final double TOWER_HEIGHT = 53.251 / 12D;
  public static final double CAMERA_HEIGHT = 13.25/12D;
  public static final double CAMERA_TOWER_HEIGHT = TOWER_HEIGHT - CAMERA_HEIGHT;

  public static final double CAMERA_TO_MIDDLE = 11D/12;
}
