package com.lynbrookrobotics.sixteen.sensors.encoder;

import java.util.function.Supplier;

/**
 * An implementation of a pipeline for taking encoder position/velocity
 * and converting it into drivetrain units.
 */
public class DrivetrainPipeline {
  private Supplier<Double> encoderAngle;
  private double gearReduction;
  private double wheelDiameter;

  /**
   * Constructs a pipeline for converting encoder degrees to drivetrain units.
   */
  public DrivetrainPipeline(Supplier<Double> encoderAngle,
                            double gearReduction,
                            double wheelDiameter) {
    this.encoderAngle = encoderAngle;
    this.gearReduction = gearReduction;
    this.wheelDiameter = wheelDiameter;
  }

  public double rotation() {
    return encoderAngle.get() / gearReduction;
  }

  public double ground() {
    return rotation() * ((Math.PI * wheelDiameter) / 360D);
  }
}
