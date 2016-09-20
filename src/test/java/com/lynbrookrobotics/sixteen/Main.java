package com.lynbrookrobotics.sixteen;

import com.lynbrookrobotics.potassium.Potassium;
import com.lynbrookrobotics.sixteen.components.drivetrain.Drivetrain;
import com.lynbrookrobotics.sixteen.config.DriverControls;
import com.lynbrookrobotics.sixteen.config.DrivetrainHardware;
import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;

import java.util.Timer;
import java.util.TimerTask;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DriverStation;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class Main {
  static CANTalon frontLeftMotor = mock(CANTalon.class, withSettings().verboseLogging());
  static CANTalon frontRightMotor = mock(CANTalon.class, withSettings().verboseLogging());
  static CANTalon backLeftMotor = mock(CANTalon.class, withSettings().verboseLogging());
  static CANTalon backRightMotor = mock(CANTalon.class, withSettings().verboseLogging());

  static DriverStation ds = mock(DriverStation.class);

  static RobotHardware hardware = new RobotHardware(
      new DrivetrainHardware(
          frontLeftMotor,
          frontRightMotor,
          backLeftMotor,
          backRightMotor,
          null,
          null
      ),
      null,
      null,
      null,
      null
  );

  public static void main(String[] args) {
    when(ds.isEnabled()).thenReturn(false);
    when(ds.isOperatorControl()).thenReturn(false);

    Drivetrain drivetrain = new Drivetrain(hardware, new DriverControls(
        ds, null, null, null
    ));

    Timer updateTimer = new Timer("update-loop");

    updateTimer.scheduleAtFixedRate(new TimerTask() {
      @Override
      public void run() {
        Potassium.updateAll();
      }
    }, 0, (long) (RobotConstants.TICK_PERIOD * 1000));
  }
}
