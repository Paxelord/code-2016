package com.lynbrookrobotics.sixteen.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Optional;

import akka.japi.Pair;

public class TowerVision {
  private static int V_LOW_THRESHOLD = 67;

  private static Mat hsv = new Mat();
  private static Mat mask = new Mat();
  private static Mat matHierarchy = new Mat();

  public static Optional<Pair<Double, Double>> detectHighGoal(Mat image) {
    Imgproc.cvtColor(image, hsv, Imgproc.COLOR_BGR2HSV);

    Core.inRange(hsv, new Scalar(60, 99, V_LOW_THRESHOLD), new Scalar(87, 255, 255), mask);

    ArrayList<MatOfPoint> contours = new ArrayList<>();

    Imgproc.findContours(mask, contours, matHierarchy, Imgproc.RETR_EXTERNAL,
        Imgproc.CHAIN_APPROX_SIMPLE);

    Rect biggest = null;
    for (MatOfPoint matOfPoint: contours) {
      Rect rec = Imgproc.boundingRect(matOfPoint);

      boolean isTower = rec.area() < 100000 &&
          rec.width > rec.height * 0.75;
      if (isTower && (biggest == null || rec.area() > biggest.area())) {
        biggest = rec;
      }
    }

    if (biggest != null) {
      Imgproc.rectangle(image, biggest.br(), biggest.tl(), new Scalar(255, 255, 255));
      return Optional.of(new Pair<>((biggest.tl().x + biggest.br().x) / 2, biggest.br().y));
    } else {
      return Optional.empty();
    }
  }
}
