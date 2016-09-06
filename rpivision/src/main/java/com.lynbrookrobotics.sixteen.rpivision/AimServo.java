package com.lynbrookrobotics.sixteen.rpivision;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

import akka.actor.Actor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;

/**
 * Created by sanghakchun on 9/4/16.
 */
public class AimServo {
  private static final ActorSystem system = ActorSystem.create();
  private static Timer timer = new Timer();

  private static FileOutputStream fostream;
  private static PrintStream postream;

  private static final int IMAGE_WIDTH = 320;

  private static int cycle;
  private static final int cycleLength = 20;

  private static final int HERTZ = 60;

  private static double speed = 0.0;
  private static double timeout = 0.0;

  private static final int goal = IMAGE_WIDTH / 2;
  private static final double DEADBAND = 0.005;
  private static final double kP = (1.0/(double) IMAGE_WIDTH / 2.0) * 0.1;

  private static double targetHorizontal = goal;

  private static void setServoPulsewidth(int width) {
      postream.println("SERVO 18 " + width);
  }

  private static void controlLoopStep() {
    double error = goal - targetHorizontal;
    speed = error * kP;
  }

  private static class UpdateLoop extends TimerTask {
    public void run() {
      cycle++;
      if (cycle > cycleLength) {
        cycle = 0;
      }

      controlLoopStep();

      timeout -= (1/HERTZ);
      if (timeout <= 0.0) {
        speed = 0.0;
      } else {
        System.out.printf("speed: %f\n", speed);
      }

      if (speed > DEADBAND) {
        setServoPulsewidth((int)(1270 - 150 * Math.abs(speed)));
      } else if (speed < -1 * DEADBAND) {
        setServoPulsewidth((int)(1730 + 150 * Math.abs(speed)));
      } else {
        setServoPulsewidth(1500);
      }
    }
  }

  public static void main(String[] args) {
    try {
      fostream = new FileOutputStream("/dev/pigpio");
      postream = new PrintStream(fostream);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    timer.schedule(new UpdateLoop(), 0, (1000 / HERTZ));

    system.actorOf(Props.create(new Creator<Actor>() {
      @Override
      public Actor create() throws Exception {
        return new VisionReceiverActor(
        target -> {
          //System.out.printf("_1: %f, _2: %f \n", target._1, target._2);

          targetHorizontal = target._1;
          timeout = 1.0;
        });
      }
    }));
  }
}
