package com.lynbrookrobotics.sixteen.config.constants;

import static com.lynbrookrobotics.sixteen.config.constants.ConfigToConstants.config;

public class ShooterFlywheelConstants {
  static {
    ConfigToConstants.loadInto(
        ShooterFlywheelConstants.class,
        RobotConstants.config.getConfig("shooter-flywheel")
    );
  }

  public static final double P_GAIN = 1D / 6000;

  public static final double I_GAIN = 8D / 125;
  public static final double I_MEMORY = 0.9;

  public static final double MAX_RPM = 8150;

  public static final double THRESHOLD_RPM = 50;

  // with partial battery only 7396
  @ConfigLoaded public static final double SHOOT_RPM = config();
  public static final double SHOOT_SECONDARY_POWER = 1;
  public static final double SHOOT_SECONDARY_LOW_POWER = 0.75;

  public static final double INTAKE_POWER = -0.5;
}
