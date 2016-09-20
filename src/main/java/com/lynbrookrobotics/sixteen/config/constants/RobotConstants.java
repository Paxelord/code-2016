package com.lynbrookrobotics.sixteen.config.constants;

import com.lynbrookrobotics.funkydashboard.FunkyDashboard;
import com.lynbrookrobotics.sixteen.components.lights.Lights;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import akka.actor.ActorSystem;

public class RobotConstants {
  public static final boolean HAS_CAMERA = true;
  public static final boolean HAS_DRIVETRAIN = true;
  public static final boolean HAS_INTAKE = true;
  public static final boolean HAS_SHOOTER = true;

  public static final double TICK_PERIOD = 1D / 200; // every 5ms
  public static final double SLOW_PERIOD = 1D / 50; // every 20ms, matches IterativeRobot

  public static final int DRIVER_STICK = 0;
  public static final int OPERATOR_STICK = 1;
  public static final int DRIVER_WHEEL = 2;

  public static final Config config =
      ConfigFactory.parseFile(new File("/home/lvuser/constants.conf"));

  public static Lights lights;

  public static final Executor executor = Executors.newFixedThreadPool(2);
  public static final ActorSystem system = ActorSystem.create();
  public static final CompletableFuture<FunkyDashboard> dashboard =
      CompletableFuture.supplyAsync(() -> {
        boolean onRobot = System.getProperty("user.name").equals("lvuser");
        FunkyDashboard ret = new FunkyDashboard();
        if (onRobot) {
          ret.bindRoute("0.0.0.0", 8080, system);
        } else {
          ret.bindRoute("localhost", 8080, system);
        }

        return ret;
      }, executor);

  /**
   * Clamps a value between the low and high values.
   */
  public static double clamp(double value, double low, double high) {
    return Math.max(
        Math.min(
            value,
            high
        ),
        low
    );
  }
}
