package com.lynbrookrobotics.sixteen.sensors.imu;

import com.lynbrookrobotics.sixteen.sensors.Value3D;

/**
 * Created by Philip on 6/13/2016.
 */
public class Value3DLogEntry {
  private Value3D value3DEntry;
  private long timeEntry;

  public Value3DLogEntry(Value3D value3DEntry, long timeEntry) {
    this.value3DEntry = value3DEntry;
    this.timeEntry = timeEntry;
  }

  public Value3D getValue3DEntry() {
    return value3DEntry;
  }

  public long getTimeEntry() {
    return timeEntry;
  }
}
