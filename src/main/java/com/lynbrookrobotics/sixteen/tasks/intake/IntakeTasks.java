package com.lynbrookrobotics.sixteen.tasks.intake;

import com.lynbrookrobotics.potassium.tasks.ContinuousTask;
import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;
import com.lynbrookrobotics.sixteen.components.intake.roller.IntakeRoller;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.secondary.ShooterSecondary;
import com.lynbrookrobotics.sixteen.config.DriverControls;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.IntakeRollerConstants;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants;
import com.lynbrookrobotics.sixteen.tasks.FixedTime;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.DirectIntakeArmSpeed;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.KeepIntakeArmAtAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.MoveIntakeArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.roller.DirectIntakeRollerSpeed;
import com.lynbrookrobotics.sixteen.tasks.lights.DirectLightsColor;
import com.lynbrookrobotics.sixteen.tasks.shooter.arm.MoveShooterArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.SpinUntilBall;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.SpinFlywheelAtRPM;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.WaitForRPMBeyond;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.secondary.SpinSecondary;

public class IntakeTasks {
  /**
   * Constructs the collection routine, which collects the ball and transfers it to the shooter.
   */
  public static FiniteTask collect(IntakeArm arm,
                                   IntakeRoller roller,
                                   ShooterArm shooterArm,
                                   ShooterFlywheel flywheel,
                                   ShooterSecondary secondary,
                                   RobotHardware hardware) {
    FiniteTask withoutSignal =
        ((new MoveIntakeArmToAngle(
            IntakeArmConstants.COLLECT_SETPOINT,
            arm,
            hardware
        ).and(new MoveShooterArmToAngle(
          ShooterArmConstants.STOWED_SETPOINT,
          hardware,
          shooterArm
        )).and(new WaitForRPMBeyond(
            IntakeRollerConstants.FLYWHEEL_COLLECT_SPEED * ShooterFlywheelConstants.MAX_RPM,
            hardware
        ))).then(
            (new SpinUntilBall(hardware, flywheel, secondary)
                .then(new FixedTime(50).andUntilDone(new DirectLightsColor(
                    () -> false,
                    () -> 0.0,
                    () -> 1.0,
                    () -> 0.0,
                    RobotConstants.lights
                ).and(new SpinSecondary(
                    () -> -IntakeRollerConstants.COLLECT_SPEED, secondary
                ))))
            ).andUntilDone(
                new DirectLightsColor(
                    () -> false,
                    () -> 1.0,
                    () -> 0.0,
                    () -> 1.0,
                    RobotConstants.lights
                ).and(new DirectIntakeRollerSpeed(
                    () -> IntakeRollerConstants.COLLECT_SPEED, roller
                )).and(new KeepIntakeArmAtAngle(
                    IntakeArmConstants.COLLECT_SETPOINT,
                    arm,
                    hardware
                ))
            )
        )).andUntilDone(new SpinFlywheelAtRPM(false,
            -IntakeRollerConstants.FLYWHEEL_COLLECT_SPEED * ShooterFlywheelConstants.MAX_RPM,
            flywheel, hardware
        ));

    return withoutSignal.then(new FixedTime(1000).andUntilDone(new DirectLightsColor(
        () -> false,
        () -> 0.0,
        () -> 1.0,
        () -> 0.0,
        RobotConstants.lights
    )));
  }

  /**
   * A routine that allows for a stuck boulder to be "popped" out.
   */
  public static ContinuousTask popOut(IntakeArm arm,
                                      IntakeRoller roller,
                                      DriverControls controls) {
    return new DirectIntakeRollerSpeed(() -> -1.0, roller).and(
        new DirectIntakeArmSpeed(
            () -> -controls.operatorStick.getY() * IntakeArmConstants.MAX_SPEED,
            arm
        )
    );
  }
}
