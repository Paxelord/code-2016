package com.lynbrookrobotics.sixteen.sensors.potentiometer;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Class for the guitar potentiometer with part number 987-1325.
 */
public class Potentiometer {
  public static final AnalogInput baseline = new AnalogInput(3);

  AnalogInput input;
  double conversionFactor = 90D / (2.568359 - 0.71533);
  double positionOffset;

  /**
   * Class for a ten turn potentiometer.
   * @param channel The input channel of the pot on the roborio's analog input
   */
  public Potentiometer(int channel, double positionOffset) {
    this.input = new AnalogInput(channel);
    this.positionOffset = positionOffset;
  }

  public double rawVoltage() {
    return input.getAverageVoltage();
  }

  /**
   * Finds the angle that the pot has turned in degrees.
   * @return angle that the pot has turned in degrees
   */
  public double getAngle() {
    return (conversionFactor * input.getAverageVoltage())
        - positionOffset;
  }
}
