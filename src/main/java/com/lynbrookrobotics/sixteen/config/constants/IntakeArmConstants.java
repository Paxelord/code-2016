package com.lynbrookrobotics.sixteen.config.constants;

import static com.lynbrookrobotics.sixteen.config.constants.ConfigToConstants.config;

public class IntakeArmConstants {
  static {
    ConfigToConstants.loadInto(
        IntakeArmConstants.class,
        RobotConstants.config.getConfig("intake-arm")
    );
  }

  @ConfigLoaded public static final double FORWARD_LIMIT = config();
  @ConfigLoaded public static final double REVERSE_LIMIT = config();

  @ConfigLoaded public static final double STOWED_THRESHOLD = config();
  @ConfigLoaded public static final double SHOOTER_STOWED_REVERSE_LIMIT = config();
  @ConfigLoaded public static final double SHOOTER_LOW_REVERSE_LIMIT = config();

  @ConfigLoaded public static final double SHOOTER_NOTCH_FORWARD_LIMIT = config();

  @ConfigLoaded public static final double MAX_SPEED = config();

  // negative because pot increases as arm moves back
  public static final double P_GAIN = -1.5D / 40;

  public static final double I_GAIN = 0.0;

  public static final double I_MEMORY = 0.0;

  public static final double ARM_ERROR = 3.0;

  // setpoints are from forward limit
  @ConfigLoaded public static final double COLLECT_SETPOINT = config();
  @ConfigLoaded public static final double TRANSPORT_SETPOINT = config();
  @ConfigLoaded public static final double SHOOT_SETPOINT = config();

  public static final double PORTCULLIS_DRIVE_DISTANCE = 5.928322751274909;

  public static final double CHEVAL_HIGH_POSITION = TRANSPORT_SETPOINT;
  @ConfigLoaded public static final double CHEVAL_LOW_POSITION = config();

  @ConfigLoaded public static final double LOWBAR_ANGLE = config();

  public static final double CHEVAL_DE_FRISE_DRIVE_DISTANCE = 6.274983161038968;
}
