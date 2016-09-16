package com.lynbrookrobotics.sixteen;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;
import com.lynbrookrobotics.sixteen.components.intake.roller.IntakeRoller;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.secondary.ShooterSecondary;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShootingPositionConstants;
import com.lynbrookrobotics.sixteen.tasks.DefenseRoutines;
import com.lynbrookrobotics.sixteen.tasks.FixedTime;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.AimForShot;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.ContinuousStraightDrive;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.DriveAbsolute;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.DriveRelative;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.TurnByAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.KeepIntakeArmAtAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.MoveIntakeArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.shooter.ShooterTasks;
import com.lynbrookrobotics.sixteen.tasks.shooter.arm.MoveShooterArmToAngle;

public class AutoGenerator {
  public enum Defense {
    PORTCULLIS,
    CHEVAL,
    MOAT,
    RAMPARTS,
    DRAWBRIDGE,
    SALLYPORT,
    ROCKWALL,
    ROUGHTERRAIN,
    LOWBAR
  }

  private RobotHardware hardware;
  private Drivetrain drivetrain;
  private IntakeArm intakeArm;
  private IntakeRoller intakeRoller;
  private ShooterArm shooterArm;
  private ShooterFlywheel shooterFlywheel;
  private ShooterSecondary shooterSecondary;

  /**
   * Generates autonomous routines with different configurations.
   */
  public AutoGenerator(RobotHardware hardware,
                       Drivetrain drivetrain,
                       IntakeArm intakeArm,
                       IntakeRoller intakeRoller,
                       ShooterArm shooterArm,
                       ShooterFlywheel shooterFlywheel,
                       ShooterSecondary shooterSecondary) {
    this.hardware = hardware;
    this.drivetrain = drivetrain;
    this.intakeArm = intakeArm;
    this.intakeRoller = intakeRoller;
    this.shooterArm = shooterArm;
    this.shooterFlywheel = shooterFlywheel;
    this.shooterSecondary = shooterSecondary;
  }

  private FiniteTask cross(Defense defense) {
    double start = hardware.drivetrainHardware.currentDistance();
    if (defense == Defense.PORTCULLIS) {
      return DefenseRoutines.crossPortcullis(
          intakeArm,
          shooterArm,
          drivetrain,
          hardware
      );
    } else if (defense == Defense.CHEVAL) {
      return DefenseRoutines.crossChevalDeFrise(intakeArm, hardware, drivetrain);
    } else if (defense == Defense.MOAT) {
      return new DriveRelative(
          hardware,
          DrivetrainConstants.DEFENSE_RAMP_DISTANCE + DrivetrainConstants.MOAT_FORWARD_DISTANCE,
          0.75,
          drivetrain
      );
    } else if (defense == Defense.RAMPARTS) {
      return new DriveRelative(
          hardware,
          DrivetrainConstants.RAMPARTS_FORWARD_DISTANCE,
          MAX_FORWARD_SPEED,
          drivetrain
      );
    } else if (defense == Defense.DRAWBRIDGE) {
      return FiniteTask.empty();
    } else if (defense == Defense.SALLYPORT) {
      return FiniteTask.empty();
    } else if (defense == Defense.ROCKWALL) {
      return new DriveRelative(
          hardware,
          DrivetrainConstants.DEFENSE_RAMP_DISTANCE + DrivetrainConstants.ROCKWALL_FORWARD_DISTANCE,
          0.75,
          drivetrain
      );
    } else if (defense == Defense.ROUGHTERRAIN) {
      return new DriveRelative(
          hardware,
          DrivetrainConstants.ROUGHTERRAIN_FORWARD_DISTANCE,
          MAX_FORWARD_SPEED,
          drivetrain
      );
    } else {
      return (new DriveAbsolute(
          hardware,
          start - DrivetrainConstants.DEFENSE_RAMP_DISTANCE,
          0.4,
          drivetrain
      ).and(new MoveIntakeArmToAngle(
          IntakeArmConstants.LOWBAR_ANGLE,
          intakeArm,
          hardware
      ).and(new MoveShooterArmToAngle(
          ShooterArmConstants.FORWARD_LIMIT,
          hardware,
          shooterArm
      )))).then(new DriveAbsolute(
          hardware,
          start - DrivetrainConstants.DEFENSE_RAMP_DISTANCE - DrivetrainConstants.LOWBAR_DISTANCE,
          0.3,
          drivetrain
      ).andUntilDone(new KeepIntakeArmAtAngle(
          IntakeArmConstants.LOWBAR_ANGLE,
          intakeArm,
          hardware
      )));
    }
  }

  private double MAX_FORWARD_SPEED = 0.5;

  private FiniteTask driveToShootingPosition(int startingPosition) {
    double start = hardware.drivetrainHardware.currentDistance();
    if (startingPosition == 1) {
      return new DriveAbsolute(
          hardware,
          start - DrivetrainConstants.DEFENSE_RAMP_DISTANCE - DrivetrainConstants.LOWBAR_DISTANCE + ShootingPositionConstants.ONE_FORWARD,
          MAX_FORWARD_SPEED,
          true,
          drivetrain
      ).then(new TurnByAngle(
          ShootingPositionConstants.ONE_TURN,
          hardware,
          drivetrain
      ).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.ONE_FORWARD_SECOND,
          MAX_FORWARD_SPEED,
          drivetrain
      )));
    } else if (startingPosition == 2) {
      return new DriveRelative(
          hardware,
          ShootingPositionConstants.TWO_FORWARD,
          MAX_FORWARD_SPEED,
          drivetrain
      ).then(new TurnByAngle(
          ShootingPositionConstants.TWO_TURN,
          hardware,
          drivetrain
      )).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.TWO_FORWARD_SECOND,
          MAX_FORWARD_SPEED,
          drivetrain
      ));
    } else if (startingPosition == 3) {
      return new DriveRelative(
          hardware,
          ShootingPositionConstants.THREE_FORWARD_FIRST,
          MAX_FORWARD_SPEED,
          drivetrain
      ).then(new TurnByAngle(
          ShootingPositionConstants.THREE_TURN_FIRST,
          hardware,
          drivetrain
      )).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.THREE_FORWARD_SECOND,
          MAX_FORWARD_SPEED,
          drivetrain
      )).then(new TurnByAngle(
          ShootingPositionConstants.THREE_TURN_SECOND,
          hardware,
          drivetrain
      )).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.THREE_FORWARD_THIRD,
          MAX_FORWARD_SPEED,
          drivetrain
      ));
    } else if (startingPosition == 4) {
      return new DriveRelative(
          hardware,
          ShootingPositionConstants.FOUR_FORWARD_FIRST,
          MAX_FORWARD_SPEED,
          drivetrain
      ).then(new TurnByAngle(
          ShootingPositionConstants.FOUR_TURN_FIRST,
          hardware,
          drivetrain
      )).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.FOUR_FORWARD_SECOND,
          MAX_FORWARD_SPEED,
          drivetrain
      )).then(new TurnByAngle(
          ShootingPositionConstants.FOUR_TURN_SECOND,
          hardware,
          drivetrain
      )).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.FOUR_FORWARD_THIRD,
          MAX_FORWARD_SPEED,
          drivetrain
      ));
    } else {
      return new DriveRelative(
          hardware,
          ShootingPositionConstants.FIVE_FORWARD_FIRST,
          MAX_FORWARD_SPEED,
          drivetrain
      ).then(new TurnByAngle(
          ShootingPositionConstants.FIVE_TURN_FIRST,
          hardware,
          drivetrain
      )).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.FIVE_FORWARD_SECOND,
          MAX_FORWARD_SPEED,
          drivetrain
      )).then(new TurnByAngle(
          ShootingPositionConstants.FIVE_TURN_SECOND,
          hardware,
          drivetrain
      )).then(new DriveRelative(
          hardware,
          ShootingPositionConstants.FIVE_FORWARD_THIRD,
          MAX_FORWARD_SPEED,
          drivetrain
      ));
    }
  }

  /**
   * Generates autonomous routines.
   * @param defense the defense the robot is in front of
   * @param startingPosition the initial position of the robot
   * @return a full autonomous routine for the initial configuration
   */
  public FiniteTask generateRoutine(Defense defense, int startingPosition) {
    if (startingPosition < 0) {
      return FiniteTask.empty();
    } else if (startingPosition == 0) {
      return new DriveRelative(
          hardware,
          DrivetrainConstants.SPY_TO_SHOOT,
          MAX_FORWARD_SPEED,
          drivetrain
      ).then(ShooterTasks.shootShort(
          shooterFlywheel,
          shooterSecondary,
          shooterArm,
          intakeArm,
          hardware
      ));
    } else if (startingPosition == 6) {
      return new FixedTime(3000).andUntilDone(new ContinuousStraightDrive(
          () -> 0.75,
          hardware,
          drivetrain
      ));
    } else if (startingPosition == 7) {
      return new FixedTime(3000).andUntilDone(new ContinuousStraightDrive(
          () -> -0.75,
          hardware,
          drivetrain
      ));
    } else {
      FiniteTask driveUp = new DriveRelative(
          hardware,
          DrivetrainConstants.DEFENSE_RAMP_DISTANCE,
          MAX_FORWARD_SPEED,
          drivetrain
      );

      if (defense == Defense.ROCKWALL || defense == Defense.MOAT || defense == Defense.LOWBAR) {
        driveUp = FiniteTask.empty();
      }

      if (defense == Defense.DRAWBRIDGE || defense == Defense.SALLYPORT) {
        return driveUp.then(new DriveRelative(hardware, 0.5, 0.1, drivetrain));
      } else if (defense == Defense.LOWBAR) {
        return ((driveUp
            .then(cross(defense))
            .then(driveToShootingPosition(startingPosition)
                .andUntilDone(ShooterTasks.prepareShootHigh(
                    shooterFlywheel,
                    shooterArm,
                    intakeArm,
                    hardware
                ))).then(
                new AimForShot(hardware, drivetrain).andUntilDone(ShooterTasks.prepareShootHigh(
                    shooterFlywheel,
                    shooterArm,
                    intakeArm,
                    hardware
                ))
            )).withTimeout(13000)).then(ShooterTasks.shootMid(
                shooterFlywheel,
                shooterSecondary,
                shooterArm,
                intakeArm,
                hardware
            ));
      } else {
        return driveUp.then(cross(defense));
      }
    }
  }
}
