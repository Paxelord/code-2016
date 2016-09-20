package com.lynbrookrobotics.sixteen;

import com.lynbrookrobotics.potassium.components.Component;
import com.lynbrookrobotics.potassium.events.Event;
import com.lynbrookrobotics.potassium.tasks.Task;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;
import com.lynbrookrobotics.sixteen.components.intake.roller.IntakeRoller;
import com.lynbrookrobotics.sixteen.components.lights.Lights;
import com.lynbrookrobotics.sixteen.components.shooter.arm.ShooterArm;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel.ShooterFlywheel;
import com.lynbrookrobotics.sixteen.components.shooter.spinners.secondary.ShooterSecondary;
import com.lynbrookrobotics.sixteen.config.DriverControls;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.VariableConfiguration;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Notifier;

/**
 * CoreRobot class loads config and creates components.
 */
public class CoreRobot {
  /**
   * Returns the value of the getter if the condition is true, otherwise null.
   */
  public static <T> T orNull(boolean cond, Supplier<T> getter) {
    if (cond) {
      return getter.get();
    } else {
      return null;
    }
  }

  VariableConfiguration config = Timing.time(VariableConfiguration::new, "Config loading ");
  RobotHardware hardware = Timing.time(
      () -> new RobotHardware(config),
      "Robot hardware loading "
  );
  DriverControls controls = Timing.time(
      DriverControls::new,
      "Driver controls loading "
  );

  private Lights getLights() {
    RobotConstants.lights = new Lights();
    return RobotConstants.lights;
  }

  CoreEvents events = Timing.time(
      () -> new CoreEvents(
          controls, hardware,
          orNull(RobotConstants.HAS_DRIVETRAIN, () -> new Drivetrain(hardware, controls)),
          orNull(RobotConstants.HAS_INTAKE, () -> new IntakeArm(hardware)),
          orNull(RobotConstants.HAS_INTAKE, () -> new IntakeRoller(hardware)),
          orNull(RobotConstants.HAS_SHOOTER, () -> new ShooterArm(hardware)),
          orNull(RobotConstants.HAS_SHOOTER, () -> new ShooterFlywheel(hardware)),
          orNull(RobotConstants.HAS_SHOOTER, () -> new ShooterSecondary(hardware)),
          getLights()
      ),
      "Core events loading "
  );

  /**
   * Sets up tick function with timer.
   */
  public CoreRobot() {
    Notifier componentNotifier = new Notifier(Component::updateComponents);
    componentNotifier.startPeriodic(RobotConstants.TICK_PERIOD);

    if (RobotConstants.HAS_DRIVETRAIN) {
      Notifier gyroNotifier = new Notifier(() -> {
        if (!events.initialCalibrationDone) {
          hardware.drivetrainHardware.mainGyro.calibrateUpdate();
        } else {
          hardware.drivetrainHardware.mainGyro.angleUpdate();
        }
      });
      gyroNotifier.startPeriodic(RobotConstants.TICK_PERIOD);
    }

    Notifier slowNotifier = new Notifier(() -> {
      Event.updateEvents();
      Task.updateCurrentTask();
    });
    slowNotifier.startPeriodic(RobotConstants.SLOW_PERIOD);
  }
}
