package com.lynbrookrobotics.sixteen.tasks.shooter.spinners.flywheel;

import com.lynbrookrobotics.potassium.tasks.ContinuousTask;
import com.lynbrookrobotics.sixteen.config.RobotHardware;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import edu.wpi.first.wpilibj.CircularBuffer;
import edu.wpi.first.wpilibj.DoubleSolenoid;

public class CollectMinMax extends ContinuousTask {
  RobotHardware hardware;

  private static FileWriter diagFile;
  private static PrintWriter diagWriter;
  String desc;

  private static ArrayList<Double> values = new ArrayList<>();

  static {
    try {
      diagFile = new FileWriter("/home/lvuser/shooter_diag.log", true);
      diagWriter = new PrintWriter(new BufferedWriter(diagFile));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Displays the max and min RPM seen during execution of the task.
   */
  public CollectMinMax(RobotHardware hardware, String desc) {
    this.hardware = hardware;
    this.desc = desc;
  }

  double min = Double.POSITIVE_INFINITY;
  double max = 0;

  @Override
  protected void startTask() {
    min = Double.POSITIVE_INFINITY;
    max = 0;
  }

  @Override
  protected void update() {
    double currentRPM = hardware.shooterSpinnersHardware.hallEffect.getRPM();

    min = Math.min(min, currentRPM);
    max = Math.max(min, currentRPM);
  }

  @Override
  protected void endTask() {
    if (values.size() >= 10) {
      values.remove(0);
    }

    values.add(100D - ((max - min) / max) * 100);
    double sum = values.stream().mapToDouble((v)->v).sum();
    double avg = sum / values.size();

    double errorSqrSum = values.stream().mapToDouble((v)->Math.pow(v - avg, 2)).sum();
    double stdDeviation = Math.sqrt(errorSqrSum / values.size());

    logger.info(String.format("Speeds: max - %.2f, percentage drop: %.2f%%", max, ((max - min) / max) * 100));
    diagWriter.println(String.format("[" + desc + "] Speeds: max - %.2f, percentage: %.2f%%, std dev: %.2f%%", max, 100D - ((max - min) / max) * 100, stdDeviation));
    diagWriter.flush();
  }

}
