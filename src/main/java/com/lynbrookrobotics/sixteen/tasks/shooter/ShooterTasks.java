package com.lynbrookrobotics.sixteen.tasks.shooter;

import com.lynbrookrobotics.potassium.tasks.ContinuousTask;
import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;
import com.lynbrookrobotics.sixteen.components.intake.roller.IntakeRoller;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.secondary.ShooterSecondary;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants;
import com.lynbrookrobotics.sixteen.tasks.FixedTime;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.ForceIntakeBrake;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.KeepIntakeArmAtAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.MoveIntakeArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.roller.DirectIntakeRollerSpeed;
import com.lynbrookrobotics.sixteen.tasks.lights.DirectLightsColor;
import com.lynbrookrobotics.sixteen.tasks.shooter.arm.MoveShooterArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.CollectMinMax;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.DirectFlywheelSpeed;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.SpinFlywheelAtRPM;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.SpinFlywheelToRPM;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.WaitForRPM;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.secondary.SpinSecondary;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.secondary.SpinSecondaryNoBall;

public class ShooterTasks {
  /**
   * Shooting high task.
   * @param shooterFlywheel Flywheel component
   * @param hardware Robot Hardware
   * @return FiniteTask for shooting
   */
  public static ContinuousTask prepareShootHigh(ShooterFlywheel shooterFlywheel,
                                                ShooterArm shooterArm,
                                                IntakeArm intakeArm,
                                                RobotHardware hardware) {
    return new MoveShooterArmToAngle(
        ShooterArmConstants.SHOOT_ANGLE,
        hardware,
        shooterArm
    ).toContinuous().and(new KeepIntakeArmAtAngle(
        IntakeArmConstants.SHOOT_HIGH_SETPOINT,
        intakeArm,
        hardware
    )).and(new SpinFlywheelAtRPM(
        true,
        ShooterFlywheelConstants.SHOOT_RPM,
        shooterFlywheel,
        hardware
    )).and(new ForceIntakeBrake(intakeArm));
  }

  /**
   * Shooting high task.
   * @param shooterFlywheel Flywheel component
   * @param shooterSecondary Secondary wheel component
   * @param hardware Robot Hardware
   * @return FiniteTask for shooting
   */
  public static FiniteTask shootHigh(ShooterFlywheel shooterFlywheel,
                                 ShooterSecondary shooterSecondary,
                                 ShooterArm shooterArm,
                                 IntakeArm intakeArm,
                                 RobotHardware hardware) {
    FiniteTask withoutFlywheel =
        (new WaitForRPM(ShooterFlywheelConstants.SHOOT_RPM, hardware)
            .and(new MoveShooterArmToAngle(
                ShooterArmConstants.SHOOT_ANGLE,
                hardware,
                shooterArm
            ))
        ).then(new SpinSecondaryNoBall(
          ShooterFlywheelConstants.SHOOT_SECONDARY_POWER,
          ShooterConstants.BALL_PROXIMITY_THRESHOLD,
          shooterSecondary,
          hardware
        )).then(new FixedTime(1000).andUntilDone(new SpinSecondary(
            () -> ShooterFlywheelConstants.SHOOT_SECONDARY_POWER,
            shooterSecondary
        )));

    return withoutFlywheel.andUntilDone(new SpinFlywheelAtRPM(
        true,
        ShooterFlywheelConstants.SHOOT_RPM,
        shooterFlywheel,
        hardware
    )).andUntilDone(new KeepIntakeArmAtAngle(
        IntakeArmConstants.SHOOT_HIGH_SETPOINT,
        intakeArm,
        hardware
    ).and(new ForceIntakeBrake(intakeArm)));
  }

  /**
   * Shooting long task.
   * @param shooterFlywheel Flywheel component
   * @param shooterSecondary Secondary wheel component
   * @param hardware Robot Hardware
   * @return FiniteTask for shooting
   */
  public static FiniteTask shootFar(ShooterFlywheel shooterFlywheel,
                                     ShooterSecondary shooterSecondary,
                                     ShooterArm shooterArm,
                                     IntakeArm intakeArm,
                                     RobotHardware hardware) {
    FiniteTask withoutFlywheel =
        (new WaitForRPM(ShooterFlywheelConstants.SHOOT_RPM, hardware)
            .and(new MoveShooterArmToAngle(
                ShooterArmConstants.SHOOT_FAR,
                hardware,
                shooterArm
            ))
        ).then(new SpinSecondaryNoBall(
            ShooterFlywheelConstants.SHOOT_SECONDARY_POWER,
            ShooterConstants.BALL_PROXIMITY_THRESHOLD,
            shooterSecondary,
            hardware
        ).then(new FixedTime(1000).andUntilDone(new SpinSecondary(
            () -> ShooterFlywheelConstants.SHOOT_SECONDARY_POWER,
            shooterSecondary
        ))).andUntilDone(new CollectMinMax(hardware)));

    return withoutFlywheel.andUntilDone(new SpinFlywheelAtRPM(
        true,
        ShooterFlywheelConstants.SHOOT_RPM,
        shooterFlywheel,
        hardware
    ));
  }

  /**
   * Shooting low task.
   * @param shooterFlywheel Flywheel component
   * @param shooterSecondary Secondary wheel component
   * @param hardware Robot Hardware
   * @return FiniteTask for shooting
   */
  public static FiniteTask shootLow(ShooterFlywheel shooterFlywheel,
                                    ShooterSecondary shooterSecondary,
                                    ShooterArm shooterArm,
                                    IntakeArm intakeArm,
                                    IntakeRoller intakeRoller,
                                    RobotHardware hardware) {
    FiniteTask prepareArms = new MoveIntakeArmToAngle(
        IntakeArmConstants.COLLECT_SETPOINT,
        intakeArm, hardware
    ).and(new MoveShooterArmToAngle(
        ShooterArmConstants.STOWED_SETPOINT,
        hardware,
        shooterArm
    ));

    ContinuousTask rolling = new DirectFlywheelSpeed(
        () -> ShooterFlywheelConstants.SHOOT_SECONDARY_LOW_POWER,
        shooterFlywheel
    ).and(new SpinSecondary(
        () -> 1.0,
        shooterSecondary
    )).and(new DirectIntakeRollerSpeed(
        () -> -ShooterFlywheelConstants.SHOOT_SECONDARY_LOW_POWER,
        intakeRoller
    ));

    FiniteTask rollOut = new SpinFlywheelToRPM(
        ShooterFlywheelConstants.MAX_RPM * 0.5,
        shooterFlywheel,
        hardware
    ).then(new SpinSecondaryNoBall(
        1.0,
        ShooterConstants.BALL_PROXIMITY_THRESHOLD,
        shooterSecondary,
        hardware
    ).andUntilDone(rolling.and(new DirectLightsColor(
        () -> false,
        () -> 1.0,
        () -> 0.0,
        () -> 0.0, RobotConstants.lights)))).then(
        new FixedTime(2000)
            .andUntilDone(rolling.and(
                new DirectLightsColor(
                    () -> false,
                    () -> 0.0,
                    () -> 1.0,
                    () -> 0.0, RobotConstants.lights)
            ))
    );

    return prepareArms.then(rollOut);
  }
}
