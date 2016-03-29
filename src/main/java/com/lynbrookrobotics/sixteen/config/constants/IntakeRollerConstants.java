package com.lynbrookrobotics.sixteen.config.constants;

import static com.lynbrookrobotics.sixteen.config.constants.ConfigToConstants.config;

public class IntakeRollerConstants {
  static {
    ConfigToConstants.loadInto(
        IntakeRollerConstants.class,
        RobotConstants.config.getConfig("intake-roller")
    );
  }

  @ConfigLoaded public static final double COLLECT_SPEED = config();

  public static final double FLYWHEEL_COLLECT_SPEED = 0.5;
}
