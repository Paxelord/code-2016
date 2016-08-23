package com.lynbrookrobotics.sixteen;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import edu.wpi.first.wpilibj.IterativeRobot;
import org.slf4j.LoggerFactory;

public class LaunchRobot extends IterativeRobot {
  CoreRobot robot;

  @Override
  public void robotInit() {
    Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    root.setLevel(Level.INFO);

    robot = new CoreRobot();
  }
}
