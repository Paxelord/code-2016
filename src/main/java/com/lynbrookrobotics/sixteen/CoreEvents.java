package com.lynbrookrobotics.sixteen;

import com.lynbrookrobotics.funkydashboard.TimeSeriesNumeric;
import com.lynbrookrobotics.potassium.defaults.events.InGameState;
import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.drivetrain.DrivetrainController;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;
import com.lynbrookrobotics.sixteen.components.intake.roller.IntakeRoller;
import com.lynbrookrobotics.sixteen.components.lights.Lights;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.secondary.ShooterSecondary;
import com.lynbrookrobotics.sixteen.config.DriverControls;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.DriverButtonAssignments;
import com.lynbrookrobotics.sixteen.config.constants.DrivetrainConstants;
import com.lynbrookrobotics.sixteen.config.constants.IntakeArmConstants;
import com.lynbrookrobotics.sixteen.config.constants.OperatorButtonAssignments;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.config.constants.ShooterArmConstants;
import com.lynbrookrobotics.sixteen.sensors.potentiometer.Potentiometer;
import com.lynbrookrobotics.sixteen.tasks.DefenseRoutines;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.AimForShot;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.TurnByAngle;
import com.lynbrookrobotics.sixteen.tasks.drivetrain.TurnByAngleEncoders;
import com.lynbrookrobotics.sixteen.tasks.intake.IntakeTasks;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.DirectIntakeArmSpeed;
import com.lynbrookrobotics.sixteen.tasks.intake.arm.MoveIntakeArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.intake.roller.DirectIntakeRollerSpeed;
import com.lynbrookrobotics.sixteen.tasks.shooter.ShooterTasks;
import com.lynbrookrobotics.sixteen.tasks.shooter.arm.DirectShooterArmSpeed;
import com.lynbrookrobotics.sixteen.tasks.shooter.arm.MoveShooterArmToAngle;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.CollectMinMax;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel.DirectFlywheelSpeed;
import com.lynbrookrobotics.sixteen.tasks.shooter.spinners.secondary.SpinSecondary;
import com.ni.vision.NIVision;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.vision.USBCamera;

import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

/**
 * CoreEvents class creates events and maps these to handlers.
 */
public class CoreEvents {
  private final DriverControls controls;
  private final RobotHardware hardware;

  private final Drivetrain drivetrain;

  private final IntakeArm intakeArm;
  private final IntakeRoller intakeRoller;

  private final ShooterArm shooterArm;
  private final ShooterFlywheel shooterFlywheel;
  private final ShooterSecondary shooterSecondary;

  private final Lights lights;

  private final PowerDistributionPanel pdp;

  boolean initialCalibrationDone = false;

  // Game State
  private final InGameState autonomousStateEvent;
  private final InGameState enabledStateEvent;

  private final FiniteTask transportTask;

  /**
   * Initializes hardware for events.
   */
  public CoreEvents(DriverControls controls,
                    RobotHardware hardware,
                    Drivetrain drivetrain,
                    IntakeArm intakeArm,
                    IntakeRoller intakeRoller,
                    ShooterArm shooterArm,
                    ShooterFlywheel shooterFlywheel,
                    ShooterSecondary shooterSecondary,
                    Lights lights) {
    this.controls = controls;
    this.hardware = hardware;

    this.drivetrain = drivetrain;

    this.intakeRoller = intakeRoller;
    this.intakeArm = intakeArm;

    this.shooterArm = shooterArm;
    this.shooterFlywheel = shooterFlywheel;
    this.shooterSecondary = shooterSecondary;

    this.lights = lights;

    this.autonomousStateEvent = new InGameState(
        controls.driverStation,
        InGameState.GameState.AUTONOMOUS
    );

    this.enabledStateEvent = new InGameState(
        controls.driverStation,
        InGameState.GameState.ENABLED
    );

    this.transportTask = new MoveShooterArmToAngle(
        ShooterArmConstants.TRANSPORT_SETPOINT,
        hardware,
        shooterArm)
      .then(
        new MoveIntakeArmToAngle(
        IntakeArmConstants.TRANSPORT_SETPOINT,
        intakeArm,
        hardware
    ));

    pdp = new PowerDistributionPanel();

    initEventMappings();
  }

  /**
   * Initializes all of the mappings, such as joysticks, autonomous, and vision.
   */
  public void initEventMappings() {
    // Camera Streaming
    if (RobotConstants.HAS_CAMERA) {
      try {
        NIVision.Image image = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 0);

        USBCamera camera = new USBCamera("cam1");
        camera.setBrightness(50);
        camera.setExposureAuto();
        camera.updateSettings();
        camera.startCapture();

        Timer updateTimer = new Timer("update-loop");

        CameraServer.getInstance().setQuality(50);
        updateTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            camera.getImage(image);
            CameraServer.getInstance().setImage(image);
          }
        }, 0, (long) (50));
      } catch (Exception cameraException) {
        System.out.println("Unable to boot camera stream");
      }
    }

    // Driver Controls
    enabledStateEvent.forEach(() -> {
      controls.driverStick.update();
      controls.driverWheel.update();
      controls.operatorStick.update();
    });

    // Gyro
    autonomousStateEvent.forEach(() -> initialCalibrationDone = true, () -> { });
    enabledStateEvent.forEach(() -> initialCalibrationDone = true, () -> { });

    // Drivetrain - telop control
    if (RobotConstants.HAS_DRIVETRAIN) {
      enabledStateEvent.forEach(
          drivetrain::resetToDefault,
          drivetrain::resetToDefault
      );

      controls.operatorStick
          .onHold(OperatorButtonAssignments.FREEZE_DRIVETRAIN)
          .forEach(
            () -> drivetrain.setController(DrivetrainController.of(
                () -> Optional.empty(),
                () -> Optional.empty()
            )),
            () -> drivetrain.resetToDefault());
    }

    // Overrides
    if (RobotConstants.HAS_INTAKE) {
      controls.operatorStick
          .onHold(OperatorButtonAssignments.OVERRIDE_INTAKE_ARM)
          .forEach(new DirectIntakeArmSpeed(
              () -> -controls.operatorStick.getY() * IntakeArmConstants.MAX_SPEED,
              intakeArm
          ));

      controls.operatorStick
          .onHold(OperatorButtonAssignments.OVERRIDE_INTAKE_ROLLER)
          .forEach(new DirectIntakeRollerSpeed(
              () -> -controls.operatorStick.getY(),
              intakeRoller
          ));
    }

    if (RobotConstants.HAS_SHOOTER) {
      controls.operatorStick
          .onHold(OperatorButtonAssignments.OVERRIDE_SHOOTER_ARM)
          .forEach(new DirectShooterArmSpeed(
              () -> -controls.operatorStick.getY(),
              shooterArm
          ));

      controls.operatorStick
          .onHold(OperatorButtonAssignments.OVERRIDE_SHOOTER_FLYWHEEL)
          .forEach(new DirectFlywheelSpeed(
              () -> -controls.operatorStick.getY(),
              shooterFlywheel
          ));

      controls.operatorStick
          .onHold(OperatorButtonAssignments.OVERRIDE_SHOOTER_SECONDARY)
          .forEach(new SpinSecondary(
              () -> -controls.operatorStick.getY(),
              shooterSecondary
          ));
    }

    if (RobotConstants.HAS_SHOOTER) {
      controls.operatorStick
          .onHold(OperatorButtonAssignments.PREPARE_SHOOT)
          .forEach(ShooterTasks.prepareShootHigh(
            shooterFlywheel,
            shooterArm,
            intakeArm,
            hardware
          ));

      // control RPM using operator joystick - start

//      controls.operatorStick
//          .onHold(OperatorButtonAssignments.PREPARE_SHOOT)
//          .forEach(() -> {
//            System.out.printf("Target RPM: %d\n", (int)(6000 + Math.abs(controls.operatorStick.getY()) * 5000));
//            return ShooterTasks.shootAtSpeed(
//                    6000 + Math.abs(controls.operatorStick.getY()) * 5000,
//                    shooterFlywheel,
//                    shooterSecondary,
//                    shooterArm,
//                    intakeArm,
//                    hardware);
//          });

      // control RPM using operator joystick - end

      controls.operatorStick
          .onHold(OperatorButtonAssignments.SHOOT_SHORT)
          .forEach(ShooterTasks.shootShort(
              shooterFlywheel,
              shooterSecondary,
              shooterArm,
              intakeArm,
              hardware));

      controls.operatorStick
          .onHold(OperatorButtonAssignments.SHOOT_MID)
          .forEach(ShooterTasks.shootMid(
              shooterFlywheel,
              shooterSecondary,
              shooterArm,
              intakeArm,
              hardware));

      controls.operatorStick
          .onHold(OperatorButtonAssignments.SHOOT_LONG)
          .forEach(ShooterTasks.shootFar(
              shooterFlywheel,
              shooterSecondary,
              shooterArm,
              intakeArm,
              hardware));

      try {
        AimForShot aim = new AimForShot(
            hardware,
            drivetrain
        );

        controls.operatorStick
            .onHold(OperatorButtonAssignments.CHEVAL)
            .forEach(aim);
      } catch (Throwable exception) {
        exception.printStackTrace();
      }
    }

    if (RobotConstants.HAS_INTAKE) {
      controls.driverStick
          .onHold(DriverButtonAssignments.COLLECT)
          .forEach(IntakeTasks.collect(
              intakeArm,
              intakeRoller,
              shooterArm,
              shooterFlywheel,
              shooterSecondary,
              hardware
          ));

      controls.driverStick
          .onHold(14)
          .forEach(new CollectMinMax(hardware));

      controls.operatorStick
          .onHold(OperatorButtonAssignments.POP_OUT)
          .forEach(IntakeTasks.popOut(intakeArm, intakeRoller, controls));
    }

    if (RobotConstants.HAS_INTAKE && RobotConstants.HAS_SHOOTER) {
      controls.operatorStick
          .onHold(OperatorButtonAssignments.SHOOT_LOW)
          .forEach(ShooterTasks.shootLow(
              shooterFlywheel,
              shooterSecondary,
              shooterArm,
              intakeArm,
              intakeRoller,
              hardware).toContinuous());

      controls.operatorStick
          .onHold(OperatorButtonAssignments.TRANSPORT_POSITION)
          .forEach(transportTask);
    }

    if (RobotConstants.HAS_DRIVETRAIN && RobotConstants.HAS_INTAKE && RobotConstants.HAS_SHOOTER) {
      controls.operatorStick
          .onHold(OperatorButtonAssignments.PORTCULLIS)
          .forEach(DefenseRoutines.preparePortcullis(
              intakeArm,
              shooterArm,
              hardware
          ));

      controls.operatorStick
          .onHold(OperatorButtonAssignments.LOWBAR)
          .forEach(DefenseRoutines.prepareCrossLowBar(
              intakeArm,
              shooterArm,
              hardware
          ));
    }

    // AUTO
    AutoGenerator generator = new AutoGenerator(
        hardware,
        drivetrain,
        intakeArm,
        intakeRoller,
        shooterArm,
        shooterFlywheel,
        shooterSecondary
    );

    for (int i = 0; i < AutoGenerator.Defense.values().length; i++) {
      SmartDashboard.putString(
          "DB/String " + i,
          "DB0 " + AutoGenerator.Defense.values()[i] + " - " + ((double) i / 2)
      );
    }

    SmartDashboard.putString(
        "DB/String " + AutoGenerator.Defense.values().length,
        "DB1 lt dfns: 1; rt dfns: 5"
    );

    if (RobotConstants.HAS_DRIVETRAIN
        && RobotConstants.HAS_INTAKE
        && RobotConstants.HAS_SHOOTER) {
      autonomousStateEvent.forEach(() -> {
//        long defenseID = Math.round(SmartDashboard.getNumber("DB/Slider 0") * 2);
//        AutoGenerator.Defense defense = AutoGenerator.Defense.values()[(int) defenseID];

//        long position = Math.round(SmartDashboard.getNumber("DB/Slider 1"));
//        return generator.generateRoutine(
//            defense,
//            (int) position
//        );

        return new TurnByAngleEncoders(180, hardware, drivetrain);
      });
    }

    // Dashboard data
    RobotConstants.dashboard.thenAccept(dashboard -> {
      try {
        if (RobotConstants.HAS_DRIVETRAIN) {
          dashboard.datasetGroup("drivetrain")
              .addDataset(new TimeSeriesNumeric<>(
                  "Angular Position",
                  () -> hardware.drivetrainHardware.mainGyro.currentPosition().valueZ()));

          dashboard.datasetGroup("drivetrain")
              .addDataset(new TimeSeriesNumeric<>(
                  "Angular Velocity",
                  () -> hardware.drivetrainHardware.mainGyro.currentVelocity().valueZ()));

          dashboard.datasetGroup("drivetrain")
              .addDataset(new TimeSeriesNumeric<>(
                  "Encoder-Measured Angle",
                  hardware.drivetrainHardware::currentRotation
              ));

          dashboard.datasetGroup("encoders")
              .addDataset(new TimeSeriesNumeric<>(
                  "Right Encoder Speed (% of max)",
                  () -> hardware.drivetrainHardware.rightEncoder.velocity.ground()
                      / DrivetrainConstants.MAX_SPEED_FORWARD));

          dashboard.datasetGroup("encoders")
              .addDataset(new TimeSeriesNumeric<>(
                  "Left Encoder Speed (% of max)",
                  () -> hardware.drivetrainHardware.leftEncoder.velocity.ground()
                      / DrivetrainConstants.MAX_SPEED_FORWARD));

          dashboard.datasetGroup("encoders")
              .addDataset(new TimeSeriesNumeric<>(
                  "Right Wheels Rotation",
                  hardware.drivetrainHardware.rightEncoder.position::rotation));

          dashboard.datasetGroup("encoders")
              .addDataset(new TimeSeriesNumeric<>(
                  "Left Wheels Rotation",
                  hardware.drivetrainHardware.leftEncoder.position::rotation));
        }

        if (RobotConstants.HAS_INTAKE) {
          dashboard.datasetGroup("intake-arm")
              .addDataset(new TimeSeriesNumeric<>(
                  "Potentiometer Angle",
                  hardware.intakeArmHardware.pot::getAngle
              ));

          dashboard.datasetGroup("intake-arm")
              .addDataset(new TimeSeriesNumeric<>(
                  "Potentiometer Voltage",
                  hardware.intakeArmHardware.pot::rawVoltage
              ));
        }

        if (RobotConstants.HAS_SHOOTER) {
          dashboard.datasetGroup("shooter")
              .addDataset((new TimeSeriesNumeric<>(
                  "Flywheel RPM",
                  hardware.shooterSpinnersHardware.hallEffect::getRPM)));

          dashboard.datasetGroup("shooter")
              .addDataset(new TimeSeriesNumeric<>(
                  "Shooter Flywheel power",
                  hardware.shooterSpinnersHardware.flywheelMotor::get));

          dashboard.datasetGroup("shooter")
              .addDataset((new TimeSeriesNumeric<>(
                  "Proximity Sensor Average Voltage",
                  hardware.shooterSpinnersHardware.proximitySensor::getAverageVoltage)));

          dashboard.datasetGroup("shooter")
              .addDataset(new TimeSeriesNumeric<>(
                  "Potentiometer Voltage",
                  hardware.shooterArmHardware.pot::rawVoltage));

          dashboard.datasetGroup("shooter")
              .addDataset(new TimeSeriesNumeric<>(
                  "Potentiometer Angle",
                  hardware.shooterArmHardware.pot::getAngle));
        }

        dashboard.datasetGroup("pot-baseline")
            .addDataset(new TimeSeriesNumeric<>(
                "Input Voltage",
                Potentiometer.baseline::getAverageVoltage));

        dashboard.datasetGroup("power")
            .addDataset(new TimeSeriesNumeric<>(
                "Voltage",
                pdp::getVoltage));

        dashboard.datasetGroup("power")
            .addDataset(new TimeSeriesNumeric<>(
                "Current",
                pdp::getTotalCurrent));

        dashboard.datasetGroup("power")
            .addDataset(new TimeSeriesNumeric<>(
                "Left Gearbox Current",
                (() -> hardware.drivetrainHardware.frontLeftMotor.getOutputCurrent() +
                hardware.drivetrainHardware.backLeftMotor.getOutputCurrent())));

        dashboard.datasetGroup("power")
            .addDataset(new TimeSeriesNumeric<>(
                "Right Gearbox Current",
                (() -> hardware.drivetrainHardware.frontRightMotor.getOutputCurrent() +
                hardware.drivetrainHardware.backRightMotor.getOutputCurrent())));

        System.out.println("FunkyDashboard is up!");
      } catch (Exception exception) {
        System.out.println("Funky Dashboard could not load");
        exception.printStackTrace();
      }
    });
  }
}
