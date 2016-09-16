package com.lynbrookrobotics.sixteen.config.constants;

public class VisionConstants {
  public static final String NUC_MAC_ADDRESS = "B8:AE:ED:7E:78:E1";
  public static final int IMAGE_HEIGHT = 240;
  public static final int IMAGE_WIDTH = 320;

  public static final double IMAGE_VERTICAL_FOV = 33.6; // TODO find out for logitech cam
  public static final double IMAGE_HORIZONTAL_FOV = 60;

  public static final double CAMERA_TILT = 58;

  public static final double TOWER_HEIGHT = 53.251 / 12D;
  public static final double CAMERA_HEIGHT = 9 / 12D;
  public static final double CAMERA_TOWER_HEIGHT = TOWER_HEIGHT - CAMERA_HEIGHT;

  public static final double CAMERA_TO_MIDDLE_HORIZONTAL = -11D / 12;
  public static final double MIDDLE_TO_CAMERA_FORWARD = 10 / 12D;
}
