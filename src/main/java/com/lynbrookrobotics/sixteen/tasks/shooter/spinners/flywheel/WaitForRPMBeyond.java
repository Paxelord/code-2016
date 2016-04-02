package com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

import static com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants.THRESHOLD_RPM;

public class WaitForRPMBeyond extends FiniteTask {
  double targetRPM;
  RobotHardware hardware;

  /**
   * Spins the spinner up to a given RPM and then ends.
   */
  public WaitForRPMBeyond(double targetRPM,
                          RobotHardware hardware) {
    this.targetRPM = targetRPM;
    this.hardware = hardware;
  }

  private long startTime = 0;

  @Override
  protected void startTask() {
    startTime = System.currentTimeMillis();
  }

  @Override
  protected void update() {
    double curRPM = hardware.shooterSpinnersHardware.hallEffect.getRPM();
    if (System.currentTimeMillis() > startTime + 1000 && curRPM < Math.min(targetRPM, 250)) {
      hardware.shooterSpinnersHardware.hallEffect.markNotWorking();
    }

    if (curRPM > Math.abs(targetRPM)) {
      finished();
    }
  }

  @Override
  protected void endTask() {
  }
}
