package com.lynbrookrobotics.sixteen.components.intake.arm;

import com.lynbrookrobotics.potassium.components.Component;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;

import edu.wpi.first.wpilibj.DriverStation;

/**
 * Intake Component that sets the arms speed.
 */
public class IntakeArm extends Component<IntakeArmController> {
  private RobotHardware robotHardware;

  /**
   * Creates a new component representing the intake arm.
   *
   * @param robotHardware RobotHardware used for setting output.
   */
  public IntakeArm(RobotHardware robotHardware) {
    super(IntakeArmController.of(() -> 0.0));

    this.robotHardware = robotHardware;
  }

  public boolean forceCoast = false;

  @Override
  protected void setOutputs(IntakeArmController intakeArmController) {
    robotHardware.intakeArmHardware.motor.enableBrakeMode(
        DriverStation.getInstance().isEnabled() && !forceCoast
    );

    double output = intakeArmController.armSpeed();

    final double intakePosition = robotHardware.intakeArmHardware.pot.getAngle();
    final double shooterPosition = robotHardware.shooterArmHardware.pot.getAngle();

    if (intakePosition < IntakeArmConstants.FORWARD_LIMIT
        && output > 0) {
      System.out.println("limiting to zero forward");
      output = 0; // only allow reverse
    }

    if (intakePosition > IntakeArmConstants.REVERSE_LIMIT
        && output < 0) {
      System.out.println("limiting to zero reverse");
      output = 0; // only allow forward
    }

    if (shooterPosition > ShooterArmConstants.STOWED_THRESHOLD
        && intakePosition > IntakeArmConstants.SHOOTER_STOWED_REVERSE_LIMIT
        && output < 0) {
      System.out.println("Not allowing reverse because shooter is stowed");
      output = 0;
    }

    if (shooterPosition > ShooterArmConstants.LOW_THRESHOLD
        && intakePosition > IntakeArmConstants.SHOOTER_LOW_REVERSE_LIMIT
        && output < 0) {
      System.out.println("Not allowing reverse because shooter is stowed");
      output = 0;
    }

    output = RobotConstants.clamp(
        output,
        -IntakeArmConstants.MAX_SPEED,
        IntakeArmConstants.MAX_SPEED
    );

    robotHardware.intakeArmHardware.motor.set(output);
  }
}
