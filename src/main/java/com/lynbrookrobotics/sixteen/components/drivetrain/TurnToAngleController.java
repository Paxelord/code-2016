package com.lynbrookrobotics.sixteen.components.drivetrain;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.control.pid.PID;

import java.util.function.Supplier;

/**
 * A controller that turns to an absolute angle.
 */
public class TurnToAngleController extends ClosedArcadeDriveController {
  private PID angleControl;

  /**
   * Constructs a new TurnToAngleController.
   * @param angle the absolute angle to turn to
   * @param hardware the robot hardware to use
   */
  public TurnToAngleController(Supplier<Double> angle, RobotHardware hardware) {
    super(hardware);

    this.angleControl = new PID(
        () -> hardware.drivetrainHardware.mainGyro.currentPosition().valueZ(),
        angle
    ).withP(4D / 90);
  }

  public double difference() {
    return angleControl.difference();
  }

  @Override
  public double forwardVelocity() {
    return 0;
  }

  @Override
  public double turnVelocity() {
    return angleControl.get();
  }
}
