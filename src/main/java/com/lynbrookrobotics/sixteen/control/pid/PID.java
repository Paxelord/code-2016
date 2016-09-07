package com.lynbrookrobotics.sixteen.control.pid;

import java.util.function.Supplier;

public class PID {
  private Supplier<Double> input;

  private Supplier<Double> target;

  private double pGain = 0;
  private double dGain = 0;
  private double deadband = 0;

  private double lastError = 0;

  /**
   * Constructs a new PID controller.
   * @param input the function producing input data
   * @param target the target value for the input
   */
  public PID(Supplier<Double> input, Supplier<Double> target) {
    this.input = input;
    this.target = target;
  }

  /**
   * Constructs a new PID controller.
   * @param input the function producing input data
   * @param target the target value for the input
   */
  public PID(Supplier<Double> input, double target) {
    this(
        input,
        () -> target
    );
  }

  /**
   * Adds a proportional component to the controller.
   * @param gain the gain to apply to error
   */
  public PID withP(double gain) {
    pGain = gain;

    return this;
  }

  /**
   * Adds a derivative component to the controller.
   * @param gain the gain to apply to the derivative of error
   */
  public PID withD(double gain) {
    dGain = gain;

    return this;
  }

  /**
   * Adds a deadband component to the controller.
   * @param threshold the amount of error to allow
   */
  public PID withDeadband(double threshold) {
    deadband = threshold;

    return this;
  }

  /**
   * Gets the difference to the target value.
   */
  public double difference() {
    return target.get() - input.get();
  }

  /**
   * Gets the output of the PID controller.
   */
  public double get() {
    double in = input.get();
    double error = target.get() - in;

    if (Math.abs(error) <= deadband) {
      return 0;
    }

    double propOut = error * pGain;
    double derOut = (error - lastError) * dGain;

    lastError = error;

    return propOut + derOut;
  }
}
