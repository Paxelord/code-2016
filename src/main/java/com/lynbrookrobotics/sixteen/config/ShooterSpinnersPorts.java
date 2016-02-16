package com.lynbrookrobotics.sixteen.config;

import com.typesafe.config.Config;

/**
 * All of the ports for shooter wheels.
 */
public class ShooterSpinnersPorts {
  public final int frontWheelPort;
  public final int backWheelPort;
  public final int frontHallPort;
  public final int backHallPort;
  public final int proximityPort;
  public final int potentiometerPort;

  /**
   * Constructors for ShooterPorts.
   * @param frontWheelPort front wheel port
   * @param backWheelPort back wheel port
   * @param frontHallPort front Hall Effect sensor port
   * @param backHallPort back Hall Effect sensor port
   * @param proximityPort proximity sensor port
   */
  public ShooterSpinnersPorts(int frontWheelPort,
                              int backWheelPort,
                              int frontHallPort,
                              int backHallPort,
                              int proximityPort,
                              int potentiometerPort) {
    this.frontWheelPort = frontWheelPort;
    this.backWheelPort = backWheelPort;
    this.frontHallPort = frontHallPort;
    this.backHallPort = backHallPort;
    this.proximityPort = proximityPort;
    this.potentiometerPort = potentiometerPort;
  }

  /**
   * Grabs shooter ports from configuration.
   * @param config the config to use
   */
  public ShooterSpinnersPorts(Config config) {
    this(
        config.getInt("front-wheel-port"),
        config.getInt("back-wheel-port"),
        config.getInt("front-hall-port"),
        config.getInt("back-hall-port"),
        config.getInt("proximity-port"),
        config.getInt("pot-port")
    );
  }
}