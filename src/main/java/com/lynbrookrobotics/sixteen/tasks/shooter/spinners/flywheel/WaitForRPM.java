package com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel;

import static com.lynbrookrobotics.sixteen.config.constants.ShooterFlywheelConstants.THRESHOLD_RPM;

import com.lynbrookrobotics.potassium.tasks.FiniteTask;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

public class WaitForRPM extends FiniteTask {
  double targetRPM;
  RobotHardware hardware;

  /**
   * Spins the spinner up to a given RPM and then ends.
   */
  public WaitForRPM(double targetRPM,
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

    double error = Math.abs(curRPM - targetRPM);

    if (error <= THRESHOLD_RPM || System.currentTimeMillis() > startTime + 10000) {
      finished();
    }
  }

  @Override
  protected void endTask() {
  }
}
