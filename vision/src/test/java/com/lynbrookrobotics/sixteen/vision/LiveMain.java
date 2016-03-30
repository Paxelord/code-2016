package com.lynbrookrobotics.sixteen.vision;

import com.lynbrookrobotics.funkydashboard.FunkyDashboard;
import com.lynbrookrobotics.funkydashboard.ImageStream;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Optional;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.*;
import org.opencv.videoio.VideoCapture;

import akka.actor.ActorSystem;
import akka.japi.tuple.Tuple3;

import static org.opencv.videoio.Videoio.CAP_PROP_AUTO_EXPOSURE;
import static org.opencv.videoio.Videoio.CAP_PROP_EXPOSURE;

public class LiveMain {
  BufferedImage image = null;

  public static void main (String args[]) throws InterruptedException{
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    ActorSystem system = ActorSystem.apply("foo");
    FunkyDashboard dashboard = new FunkyDashboard();
    dashboard.bindRoute("localhost", 8080, system);

    LiveMain t = new LiveMain();
    VideoCapture camera = new VideoCapture(0);
    camera.set(CAP_PROP_AUTO_EXPOSURE, 0);

    Mat frame = new Mat();
    dashboard.datasetGroup("vision").addDataset(
        new ImageStream(
            "foo",
            () -> {
              if (camera.read(frame)) {
                Optional<Tuple3<Mat, Double, Double>> detect = TowerVision.detectHighGoal(frame);
                if (detect.isPresent()) {
                  t.image = t.matToBufferedImage(detect.get().t1());
                } else {
                  t.image = t.matToBufferedImage(frame);
                }
              }

              return t.image;
            }
        )
    );
  }

  public BufferedImage matToBufferedImage(Mat frame) {
    //Mat() to BufferedImage
    int type = 0;
    if (frame.channels() == 1) {
      type = BufferedImage.TYPE_BYTE_GRAY;
    } else if (frame.channels() == 3) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    BufferedImage image = new BufferedImage(frame.width(), frame.height(), type);
    WritableRaster raster = image.getRaster();
    DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
    byte[] data = dataBuffer.getData();
    frame.get(0, 0, data);

    return image;
  }
}