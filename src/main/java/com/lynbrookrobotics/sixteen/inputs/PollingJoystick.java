package com.lynbrookrobotics.sixteen.inputs;

import com.lynbrookrobotics.potassium.defaults.events.ButtonHold;
import com.lynbrookrobotics.potassium.defaults.events.ButtonPress;

import edu.wpi.first.wpilibj.Joystick;

public class PollingJoystick {
  private double x;
  private double y;
  private double z;

  private double inputScalingFactor = 1D;
  private final double deadband;

  public final Joystick underlying;


  public PollingJoystick(Joystick underlying, double deadband) {
    this.underlying = underlying;
    this.deadband = deadband;
  }

  /**
   * Updates the joystick value.
   */
  public synchronized void update() {
    x = underlying.getX();
    y = underlying.getY();
    z = underlying.getZ();
  }

  public synchronized double getX() {
    return Math.abs(x) <= deadband ? 0.0 : x;
  }


  public synchronized double getY() {
    return Math.abs(y) <= deadband ? 0.0 : y;
  }

  public synchronized double getZ() {
    return Math.abs(z) <= deadband ? 0.0 : z;
  }

  /**
   * Creates an event triggered when the button is pressed.
   */
  public ButtonPress onPress(int button) {
    return new ButtonPress(
        underlying,
        button
    );
  }

  /**
   * Creates an event triggered when the button is held.
   */
  public ButtonHold onHold(int button) {
    return new ButtonHold(
        underlying,
        button
    );
  }
}
