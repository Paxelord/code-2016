package com.lynbrookrobotics.sixteen.config.constants;

import static com.lynbrookrobotics.sixteen.config.constants.ConfigToConstants.config;

public class DrivetrainConstants {
  static {
    ConfigToConstants.loadInto(
        DrivetrainConstants.class,
        RobotConstants.config.getConfig("drivetrain")
    );
  }

  public static final double WHEEL_DIAMETER = 1D / 2; // 6 in = 0.5 ft
  public static final double GEAR_REDUCTION = 2.13;
  public static final double FT_TO_ENC = (360D / (Math.PI * WHEEL_DIAMETER)) * GEAR_REDUCTION;
  @ConfigLoaded public static final double MAX_SPEED_FORWARD = config();

  @ConfigLoaded public static final double MAX_ROTATIONAL_SPEED = config();

  public static final double CURRENT_LIMIT_PERCENT = 0.5;

  public static final double SPY_TO_SHOOT =
      (100 - ShootingPositionConstants.DISTANCE_WHEEL_BOTTOM) / 12D;
  public static final double DEFENSE_RAMP_DISTANCE = 23 / 12D;

  public static final double MOAT_FORWARD_DISTANCE =
      (51 + ShootingPositionConstants.DISTANCE_WHEEL_BOTTOM) / 12D;
  public static final double RAMPARTS_FORWARD_DISTANCE =
      (50 + ShootingPositionConstants.DISTANCE_WHEEL_BOTTOM) / 12D;
  public static final double ROCKWALL_FORWARD_DISTANCE =
      (51 + ShootingPositionConstants.DISTANCE_WHEEL_BOTTOM) / 12D;
  public static final double ROUGHTERRAIN_FORWARD_DISTANCE =
      (51 + ShootingPositionConstants.DISTANCE_WHEEL_BOTTOM) / 12D;

  public static final double LOWBAR_BACKWARD_DISTANCE =
      -(47.75 + ShootingPositionConstants.DISTANCE_WHEEL_BOTTOM) / 12D;

  //Track AKA distance between wheels
  public static final double TRACK = 21.75; //Measured in inches

  //Minimum radius is track /  2
  public static final double MAX_CURVATURE = 1D / (TRACK / 2 );

  public static final double BLEND_EXPONENT = 0.5; //TODO: Tune
}
