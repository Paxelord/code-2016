package com.lynbrookrobotics.sixteen.components.shooter.spinners.flywheel;

import com.lynbrookrobotics.sixteen.config.RobotHardware;
import com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants;

import com.lynbrookrobotics.sixteen.control.pid.PID;

public class ShooterFlywheelSpeedController extends ShooterFlywheelController {
  private PID flywheelPID;

  private double targetRPM;

  /**
   * Constructs a Controller that keeps a stable RPM.
   * @param targetRPM RPM that should be achieved
   * @param hardware RobotHardware
   */
  public ShooterFlywheelSpeedController(double targetRPM, RobotHardware hardware) {
    this.flywheelPID = new PID(
        hardware.shooterSpinnersHardware.hallEffect::getRPM,
        Math.abs(targetRPM)
    ).withP(ShooterFlywheelConstants.P_GAIN)
        .withI(ShooterFlywheelConstants.I_GAIN, ShooterFlywheelConstants.I_MEMORY);

    this.targetRPM = targetRPM;
  }

  public double error() {
    return flywheelPID.difference();
  }

  @Override
  public double flywheelSpeed() {
    double abs = (Math.abs(targetRPM) / ShooterFlywheelConstants.MAX_RPM) + flywheelPID.get();
//    System.out.println(targetRPM + " " + abs);
    return Math.copySign(abs, targetRPM);
  }
}
