package com.lynbrookrobotics.sixteen.tasks;

import com.lynbrookrobotics.potassium.tasks.ContinuousTask;
import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.DriveRelative;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.KeepIntakeArmAtAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.MoveIntakeArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.shooter.arm.MoveShooterArmToAngle;

public class DefenseRoutines {
  /**
   * The Routine used when passing through defense portcullis.
   * @param intakeArm The intake arm used to move the portcullis to a certain position.
   * @param robotHardware The robot hardware used to access the ports.
   * @return A finite task with the portcullis routine.
   */
  public static FiniteTask preparePortcullis(IntakeArm intakeArm,
                                             ShooterArm shooterArm,
                                             RobotHardware robotHardware) {
    return new MoveIntakeArmToAngle(
        IntakeArmConstants.FORWARD_LIMIT + 5,
        intakeArm,
        robotHardware
    ).and(new MoveShooterArmToAngle(
        ShooterArmConstants.FORWARD_LIMIT,
        robotHardware,
        shooterArm
    ));
  }

  /**
   * The Routine used when passing through defense portcullis.
   * @param intakeArm The intake arm used to move the portcullis to a certain position.
   * @param drivetrain The drive train used to move through portcullis after lifting.
   * @param robotHardware The robot hardware used to access the ports.
   * @return A finite task with the portcullis routine.
   */
  public static FiniteTask crossPortcullis(IntakeArm intakeArm,
                                           ShooterArm shooterArm,
                                           Drivetrain drivetrain,
                                           RobotHardware robotHardware) {
    return preparePortcullis(intakeArm, shooterArm, robotHardware).then(new DriveRelative(
        robotHardware,
        DrivetrainConstants.LOWBAR_DISTANCE,
        0.25,
        drivetrain
    ));
  }

  /**
   * The Routine for crossing the ChevaldeFrise defense.
   *
   * @param intakeArm     The intake arm used to move the ChevaldeFrise down.
   * @param robotHardware Robot hardware used to access the robot ports.
   * @param drivetrain    The Drivertrain is used to move through the defense, ChevaldeFrise.
   * @return A finite task used to cross the cheval de frise.
   */
  public static FiniteTask crossChevalDeFrise(IntakeArm intakeArm,
                                              RobotHardware robotHardware,
                                              Drivetrain drivetrain) {
    return new MoveIntakeArmToAngle(
        IntakeArmConstants.CHEVAL_LOW_POSITION,
        intakeArm,
        robotHardware
    ).then(new DriveRelative(
        robotHardware,
        IntakeArmConstants.CHEVAL_DE_FRISE_DRIVE_DISTANCE,
        0.4,
        drivetrain
    ).and(
        (new FixedTime(1000).andUntilDone(new KeepIntakeArmAtAngle(
            IntakeArmConstants.CHEVAL_LOW_POSITION,
            intakeArm,
            robotHardware
        ))).then(new MoveIntakeArmToAngle(
            IntakeArmConstants.CHEVAL_HIGH_POSITION,
            intakeArm,
            robotHardware
        ))
    ));
  }

  /**
   * Creates a routine that prepares the robot to cross the low bar.
   */
  public static ContinuousTask prepareCrossLowBar(IntakeArm intakeArm,
                                                  ShooterArm shooterArm,
                                                  RobotHardware robotHardware) {
    return new MoveIntakeArmToAngle(
        IntakeArmConstants.LOWBAR_ANGLE,
        intakeArm,
        robotHardware
    ).then(new MoveShooterArmToAngle(
        ShooterArmConstants.FORWARD_LIMIT,
        robotHardware,
        shooterArm
    )).then(new InfiniteFinite().andUntilDone(
        new KeepIntakeArmAtAngle(
            IntakeArmConstants.LOWBAR_ANGLE,
            intakeArm,
            robotHardware
        )
    )).toContinuous();
  }
}
