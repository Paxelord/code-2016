package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;
import com.lynbrookrobotics.sixteen.sensors.encoder.DrivetrainEncoder;

import java.util.Optional;

import javaslang.control.Either;

public abstract class DitheredTankDriveController extends DrivetrainController {
  private final DrivetrainEncoder leftEncoder;
  private final DrivetrainEncoder rightEncoder;

  /**
   * Constructs a drivetrain controller that runs closed loop on each side.
   */
  public DitheredTankDriveController(RobotHardware hardware) {
    leftEncoder = hardware.drivetrainHardware.leftEncoder;
    rightEncoder = hardware.drivetrainHardware.rightEncoder;
  }

  // left is power, right is brake
  private static Either<Double, Double> powerOrBrake(double target, double current) {
    if (current < 0) {
      return powerOrBrake(-target, -current).bimap(p -> -p, b -> b);
    } else { // current speed is positive
      if (target >= current) { // speeding up
        return Either.left(target);
      } else { // slowing down
        double error = target - current;

        if (target >= 0) { // braking is based on speed alone; reverse power unnecessary
          if (current > -error + 0.05) {
            return Either.right(-error / current);
          } else {
            return Either.right(1D);
          }
        } else { // Input < 0, braking with reverse power
          return Either.left(error / (1.0 + current));
        }
      }
    }
  }

  private static final int[] ditherPattern = {
      0x00, // 0: 0000 0000 = 0x00
      0x01, // 1: 0000 0001 = 0x01
      0x11, // 2: 0001 0001 = 0x11
      0x25, // 3: 0010 0101 = 0x25
      0x55, // 4: 0101 0101 = 0x55
      0xD5, // 5: 1101 0101 = 0xD5
      0xEE, // 6: 1110 1110 = 0xEE
      0xFE, // 7: 1111 1110 = 0xFE
      0xFF  // 8: 1111 1111 = 0xFF
  };

  private static boolean calculateDither(double brakePower, int tick) {
    int brakeLevel = (int) Math.round(Math.abs(brakePower) * 8);
    return (ditherPattern[brakeLevel] & (1 << tick)) == 1;
  }

  public abstract double leftTarget();

  public abstract double rightTarget();

  private int leftTick = 0;

  @Override
  public Optional<Double> leftPower() {
    leftTick = (leftTick + 1) % 8;

    Either<Double, Double> powerOrBrake = powerOrBrake(
        leftTarget(),
        leftEncoder.velocity.ground() / DrivetrainConstants.MAX_SPEED_FORWARD
    );

    if (powerOrBrake.isLeft()) {
      return Optional.of(powerOrBrake.left().get());
    } else {
      return calculateDither(powerOrBrake.right().get(), leftTick)
          ? Optional.empty() : Optional.of(leftTarget());
    }
  }

  private int rightTick = 0;

  @Override
  public Optional<Double> rightPower() {
    rightTick = (rightTick + 1) % 8;

    Either<Double, Double> powerOrBrake = powerOrBrake(
        rightTarget(),
        rightEncoder.velocity.ground() / DrivetrainConstants.MAX_SPEED_FORWARD
    );

    if (powerOrBrake.isLeft()) {
      return Optional.of(powerOrBrake.left().get());
    } else {
      return calculateDither(powerOrBrake.right().get(), rightTick)
          ? Optional.empty() : Optional.of(rightTarget());
    }
  }
}
