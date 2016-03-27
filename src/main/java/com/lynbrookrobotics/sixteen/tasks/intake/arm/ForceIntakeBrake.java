package com.lynbrookrobotics.sixteen.tasks.intake.arm;

import com.lynbrookrobotics.potassium.tasks.ContinuousTask;
import com.lynbrookrobotics.sixteen.components.intake.arm.IntakeArm;

public class ForceIntakeBrake extends ContinuousTask {
  IntakeArm arm;

  public ForceIntakeBrake(IntakeArm arm) {
    this.arm = arm;
  }

  @Override
  protected void startTask() {
    arm.forceBrake = true;
  }

  @Override
  protected void update() {

  }

  @Override
  protected void endTask() {
    arm.forceBrake = false;
  }
}
