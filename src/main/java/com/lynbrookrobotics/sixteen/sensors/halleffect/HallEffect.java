package com.lynbrookrobotics.sixteen.sensors.halleffect;

import com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants;

import edu.wpi.first.wpilibj.Counter;
import edu.wpi.first.wpilibj.DigitalInput;

public class HallEffect extends Counter {
  DigitalInput source;
  double curRPM = 0;

  boolean working = true;

  public void markNotWorking() {
    System.out.println("ERROR ERROR: HALL EFFECT IS NOT WORKING");
    working = false;
  }

  public boolean working() {
    return working;
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

  public double rawOutput() {
    update();

    return curRPM;
  }

  /**
   * Gets the RPM measured by the sensor.
   */
  public double getRPM() {
    update();

    return curRPM;
  }
}
