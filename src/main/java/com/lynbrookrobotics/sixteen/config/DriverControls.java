package com.lynbrookrobotics.sixteen.config;

import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.inputs.PollingJoystick;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

public class DriverControls {
  public final DriverStation driverStation;

  public final PollingJoystick driverStick;
  public final PollingJoystick driverWheel;
  public final PollingJoystick operatorStick;

  /**
   * Constructs a DriverControls object with instances of all control inputs.
   * @param driverStation the driver station interface
   * @param driverStick the driver-side joystick
   * @param driverWheel the driver steering wheel
   * @param operatorStick the operator-side joystick
   */
  public DriverControls(DriverStation driverStation,
                        Joystick driverStick,
                        Joystick driverWheel,
                        Joystick operatorStick) {
    this.driverStation = driverStation;

    this.driverStick = new PollingJoystick(driverStick, 0.01);
    this.driverWheel = new PollingJoystick(driverWheel, 0.01);
    this.operatorStick = new PollingJoystick(operatorStick, 0.01);
  }

  /**
   * Constructs a DriverControls object with default interfaces.
   */
  public DriverControls() {
    this(
        DriverStation.getInstance(),
        new Joystick(RobotConstants.DRIVER_STICK),
        new Joystick(RobotConstants.DRIVER_WHEEL),
        new Joystick(RobotConstants.OPERATOR_STICK)
    );
  }
}
