package com.lynbrookrobotics.sixteen.config.constants;

public class VisionConstants {
  public static final String NUC_MAC_ADDRESS = "B8:AE:ED:7E:78:E1";
  public static final int IMAGE_HEIGHT = 240;
  public static final int IMAGE_WIDTH = 320;

  public static final double IMAGE_VERTICAL_FOV = 70.0D;
//  public static final double IMAGE_HORIZONTAL_FOV = 59.7D;
  public static final double IMAGE_HORIZONTAL_FOV = 70.0D;

  public static final double CAMERA_TILT = 30D; // degrees camera is offset from vertical

  public static final double TOWER_HEIGHT = 53.251D / 12D; // tower height from ground
  public static final double CAMERA_HEIGHT = 10.5D / 12D; // camera height from ground
  public static final double CAMERA_TOWER_HEIGHT = TOWER_HEIGHT - CAMERA_HEIGHT; // tower height from camera level

  public static final double CAMERA_TO_MIDDLE_HORIZONTAL = 11D / 12D; // distance camera is from center of robot (side axis)
  public static final double MIDDLE_TO_CAMERA_FORWARD = 26D / 12D;
}
