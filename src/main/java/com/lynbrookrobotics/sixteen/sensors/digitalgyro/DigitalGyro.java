package com.lynbrookrobotics.sixteen.sensors.digitalgyro;

import com.lynbrookrobotics.sixteen.config.constants.RobotConstants;
import com.lynbrookrobotics.sixteen.sensors.Value3D;
import com.lynbrookrobotics.sixteen.sensors.imu.Value3DLogEntry;

import java.util.ArrayList;

public abstract class DigitalGyro {
  Value3D currentVelocity = new Value3D(0, 0, 0);
  Value3D currentPosition = new Value3D(0, 0, 0);

  private ArrayList<Value3DLogEntry> angleLog = new ArrayList<>();
  private ArrayList<Long> angleLogTimes = new ArrayList<>(200);

  Value3D currentDrift;

  private ArrayList<Value3D> values = new ArrayList<>(200);
  int index = 0;
  boolean calibrating = true;

  /**
   * Gets the current velocity.
   */
  public abstract Value3D retrieveVelocity();

  /**
   * Updates values for the drift on the axis.
   */
  public void calibrateUpdate() {
    currentVelocity = retrieveVelocity();
    values.add(index++, currentVelocity);

    if (index > 200) {
      index = 0;
    }
  }

  /**
   * Updates values for the angle on the gyro.
   */
  public void angleUpdate() {
    if (calibrating) {
      Value3D sum = new Value3D(0, 0, 0);
      values.forEach(value3D -> sum.plusMutable(
          value3D.valueX(),
          value3D.valueY(),
          value3D.valueZ()));
      currentDrift = sum.times(-1D / values.size());
      values = null;

      calibrating = false;
    }

    Value3D previousVelocity = currentVelocity;
    currentVelocity = retrieveVelocity().plus(currentDrift);
    currentPosition = currentPosition
        .plus(new Value3D(
            trapaziodalIntegration(currentVelocity.valueX(), previousVelocity.valueX()),
            trapaziodalIntegration(currentVelocity.valueY(), previousVelocity.valueY()),
            trapaziodalIntegration(currentVelocity.valueZ(), previousVelocity.valueZ())
        ));
    angleLog.add(new Value3DLogEntry(currentPosition, toNearestLowerMultipleTickPeriod(
        System.currentTimeMillis()) ) );
  }

  public Value3D currentPosition() {
    return currentPosition;
  }

  public Value3D currentVelocity() {
    return currentVelocity;
  }

  /**
   * Integrates a single gyro axis.
   * Takes the average of the 2 velocities and multiplies by tick period (5 ms)
   * @param velocity         The current velocity read from the gyro
   * @param previousVelocity the previous velocity read from the gyro
   * @return The angle gotten by taking the integral of the angular velocity
   */
  private double trapaziodalIntegration(double velocity, double previousVelocity) {
    return (RobotConstants.TICK_PERIOD * ((velocity + previousVelocity) / 2));
  }

  /**
   * Return an instance of Value3D corresponding to the current angular position at a give time in
   * milliseconds
   * @param atTime the time to get the angular position in milliseconds since the initialization of
   * System. values is rounded to the nearest lower multiple of 5, since our tickperiod is 5
   * milliseconds
   * @return Angular position at the given time
   */
  public Value3D getPostionAtTime(long atTime) {
    if ( angleLogTimes.contains( toNearestLowerMultipleTickPeriod(atTime) ) ) {
      return angleLog.get( angleLogTimes.indexOf(atTime) ).getValue3DEntry();
    }

    return null;
  }

  /**
   * Round number down to the nearest lower multiple of of the robot tick period. For example to
   * toNearestLowerMultipleTickPeriod(11) returns 10 and toNearestLowerMultipleTickPeriod(19)
   * returns 15
   * @param toRound what to round  down to the nearest multiple of the robot tick period
   * @return toRound rounded to the nearest lower multiple of 5
   */
  public Long toNearestLowerMultipleTickPeriod(Long toRound) {
    return toRound - toRound % (long) RobotConstants.TICK_PERIOD;
  }
}
