package com.lynbrookrobotics.sixteen.sensors.halleffect;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

public class HallEffect extends Counter {
  private DigitalInput source;
  private double curRPM = 0;

  /**
   * Marks that the sensor is detected to not be working.
   */
  public void markNotWorking() {
    System.out.println("ERROR ERROR: HALL EFFECT IS NOT WORKING");
  }

  /**
   * Constructs a hall effect sensor reading from the given DIO channel.
   */
  public HallEffect(int channel) {
    super();
    this.source = new DigitalInput(channel);
    setUpSource(source);
  }

  private void update() {
    double reported = 60 / getPeriod();
    if (getStopped()) {
      reported = 0;
    }

    if (reported < 20000) {
      curRPM = reported;
    }
  }

  /**
   * Gets the RPM measured by the sensor.
   */
  public double getRPM() {
    update();

    return curRPM;
  }
}
