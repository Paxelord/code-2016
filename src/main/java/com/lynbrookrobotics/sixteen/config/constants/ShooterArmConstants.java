package com.lynbrookrobotics.sixteen.config.constants;

import static com.lynbrookrobotics.sixteen.config.constants.ConfigToConstants.config;

public class ShooterArmConstants {
  static {
    ConfigToConstants.loadInto(
        ShooterArmConstants.class,
        RobotConstants.config.getConfig("shooter-arm")
    );
  }

  @ConfigLoaded public static final double FORWARD_LIMIT = config();
  @ConfigLoaded public static final double FORWARD_INTAKE_STOWED_LIMIT = config();
  @ConfigLoaded public static final double REVERSE_LIMIT = config();
  @ConfigLoaded public static final double STOWED_THRESHOLD = config();
  @ConfigLoaded public static final double LOW_THRESHOLD = config();

  @ConfigLoaded public static final double MAX_SPEED = config();

  public static final double P_GAIN = 1.0 / 45D;

  public static final double I_GAIN = 0.0d;
  public static final int I_MEMORY = 1;
  public static final int SHOOTER_ARM_ERROR = 2;

  public static final double SHOOT_ANGLE = REVERSE_LIMIT;
  @ConfigLoaded public static final double SHOOT_FAR = config();

  public static final double STOWED_SETPOINT = FORWARD_LIMIT - 2;

  @ConfigLoaded public static final double TRANSPORT_SETPOINT = config();
}
