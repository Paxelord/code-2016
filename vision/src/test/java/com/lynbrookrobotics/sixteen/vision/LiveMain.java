package com.lynbrookrobotics.sixteen.vision;

import com.lynbrookrobotics.funkydashboard.FunkyDashboard;
import com.lynbrookrobotics.funkydashboard.ImageStream;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;

import akka.actor.ActorSystem;

public class LiveMain {
  static {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
  }

  static Mat last = new Mat();

  public static void main (String args[]) throws InterruptedException{
    ActorSystem system = ActorSystem.apply("foo");
    FunkyDashboard dashboard = new FunkyDashboard();
    dashboard.bindRoute("localhost", 8080, system);

    VideoCapture camera = new VideoCapture(1);
    Utils.initializeCamera(camera);

    new Thread(() -> {
      Mat frame = new Mat();

      while (true) {
        if (camera.read(frame)) {
          TowerVision.detectHighGoal(frame);
          last = frame.clone();
        }
      }
    }).start();

    dashboard.datasetGroup("vision").addDataset(
        new ImageStream(
            "Detected Image",
            () -> Utils.matToBufferedImage(last, 200, 0)
        )
    );
  }
}