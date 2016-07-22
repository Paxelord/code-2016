package com.lynbrookrobotics.sixteen.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Optional;

import akka.japi.tuple.Tuple3;

// java -Djava.library.path=/usr/local/share/OpenCV/java -jar vision-ly-0.1-SNAPSHOT.jar
public class TowerVision {
  private static int V_LOW_THRESHOLD = 150;

  private static Mat destination = new Mat();
  private static Mat mask = new Mat();
  private static Mat matHeirarchy = new Mat();
  private static Mat out = new Mat();

  public static Optional<Tuple3<Mat, Double, Double>> detectHighGoal(Mat image) {

    Profiler.start("detectHighGoal beginning");
    Imgproc.cvtColor(image, destination, Imgproc.COLOR_BGR2HSV);

    Core.inRange(destination, new Scalar(0, 0, V_LOW_THRESHOLD), new Scalar(255, 255, 255), mask);

    ArrayList<MatOfPoint> contours = new ArrayList<>();

    Core.bitwise_and(destination, destination, out, mask);
    Imgproc.findContours(mask, contours, matHeirarchy, Imgproc.RETR_EXTERNAL,
        Imgproc.CHAIN_APPROX_SIMPLE);
    Profiler.end("detectHighGoal beginning");

    Profiler.start("MatOfPoint iterator");
    Rect biggest = null;
    for (MatOfPoint matOfPoint: contours) {
      Rect rec = Imgproc.boundingRect(matOfPoint);

//      double percent = Imgproc.contourArea(matOfPoint) / rec.area();
      boolean isTower = rec.area() < 100000 &&
          rec.width > rec.height * 0.75/* &&
          percent >= 0.1 && percent <= 0.5*/;
      if (isTower && (biggest == null || rec.area() > biggest.area())) {
        biggest = rec;
      }
    }
    Profiler.end("MatOfPoint iterator");

    Profiler.start("detectHighGoal ending");
    if (biggest != null) {
      Imgproc.rectangle(out, biggest.br(), biggest.tl(), new Scalar(255, 255, 255));
      Profiler.end("detectHighGoal ending");

      return Optional.of(new Tuple3<>(out, (biggest.tl().x + biggest.br().x) / 2, biggest.br().y));
    } else {
      out.release();
      Profiler.end("detectHighGoal ending");
      return Optional.empty();
    }
  }
}
