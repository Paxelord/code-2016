package com.lynbrookrobotics.sixteen.config;

import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;
import com.lynbrookrobotics.sixteen.sensors.digitalgyro.DigitalGyro;
import com.lynbrookrobotics.sixteen.sensors.encoder.DrivetrainEncoder;
import com.lynbrookrobotics.sixteen.sensors.encoder.Encoder;
import com.lynbrookrobotics.sixteen.sensors.gyro.GyroL3GD20H;
import com.lynbrookrobotics.sixteen.sensors.imu.ADIS16448;

import edu.wpi.first.wpilibj.CANTalon;

/**
 * Contains all the hardware components for the drivetrain.
 * Includes motors and gyros for angle and acceleration measurement.
 */
public class DrivetrainHardware {
  public final CANTalon frontLeftMotor;
  public final CANTalon frontRightMotor;
  public final CANTalon backLeftMotor;
  public final CANTalon backRightMotor;

  public final DrivetrainEncoder leftEncoder;
  public final DrivetrainEncoder rightEncoder;

  private final GyroL3GD20H gyro;
  private final ADIS16448 imu;
  public final DigitalGyro mainGyro;


  /**
   * Constructs a new default DrivetrainHardware object given the interfaces.
   */
  public DrivetrainHardware(CANTalon frontLeftMotor,
                            CANTalon frontRightMotor,
                            CANTalon backLeftMotor,
                            CANTalon backRightMotor,
                            GyroL3GD20H gyro,
                            ADIS16448 imu) {
    this.frontLeftMotor = frontLeftMotor;
    this.frontRightMotor = frontRightMotor;
    this.backLeftMotor = backLeftMotor;
    this.backRightMotor = backRightMotor;

    this.leftEncoder = new DrivetrainEncoder(
        Encoder.talonEncoder(frontLeftMotor, false), // comp is opposite of practice
        DrivetrainConstants.GEAR_REDUCTION,
        DrivetrainConstants.WHEEL_DIAMETER
    );

    this.rightEncoder = new DrivetrainEncoder(
        Encoder.talonEncoder(frontRightMotor, true),
        DrivetrainConstants.GEAR_REDUCTION,
        DrivetrainConstants.WHEEL_DIAMETER
    );

    this.gyro = gyro;
    this.imu = imu;
    this.mainGyro = imu;
  }

  /**
   * Constructs a new default DrivetrainHardware object.
   * @param config the config to load interfaces from
   */
  public DrivetrainHardware(VariableConfiguration config) {
    this(
        new CANTalon(config.drivetrainPorts.portFrontLeft),
        new CANTalon(config.drivetrainPorts.portFrontRight),
        new CANTalon(config.drivetrainPorts.portBackLeft),
        new CANTalon(config.drivetrainPorts.portBackRight),
        new GyroL3GD20H(),
        new ADIS16448()
    );
  }

  public double currentDistance() {
    return (leftEncoder.position.ground() + rightEncoder.position.ground()) / 2;
  }

  public double currentForwardSpeed() {
    return (leftEncoder.velocity.ground() + rightEncoder.velocity.ground()) / 2;
  }

  public double currentRotation() {
    double left = leftEncoder.position.rotation();
    double right = rightEncoder.position.rotation();

    return (left - right) / (2 /* for cancelling out double factor */ * (22.0 / 6));
  }

  public double currentRotationVelocity() {
    double left = leftEncoder.velocity.rotation();
    double right = rightEncoder.velocity.rotation();

    return (left - right) / (2 /* for cancelling out double factor */ * (22.0 / 6));
  }
}
