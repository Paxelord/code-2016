package com.lynbrookrobotics.sixteen.vision;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;

public class Utils {
  public static void initializeCamera(VideoCapture camera) {
    // TODO: use process to use v4l2-ctl on linux systems to disable auto exposure
    camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 320);
    camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 200);
  }

  /**
   * Converts an OpenCV Mat to a BufferedImage to show in Funky Dashboard
   * @param frame the frame to convert
   * @return the buffered image, scaled down to 200px wide
   */
  public static BufferedImage matToBufferedImage(Mat frame, int width, int rotation) {
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

    Image resize = image.getScaledInstance(
        width,
        (int) (frame.height() * ((double) width / frame.width())),
        Image.SCALE_SMOOTH);

    BufferedImage ret = new BufferedImage(
        width,
        (int) (frame.height() * ((double) width / frame.width())),
        BufferedImage.TYPE_INT_ARGB);

    Graphics2D g2d = ret.createGraphics();
    g2d.drawImage(resize, 0, 0, null);
    g2d.dispose();


    if (rotation != 0) {
      AffineTransform tx = new AffineTransform();
      tx.rotate(Math.toRadians(rotation), ret.getWidth(), ret.getHeight());

      AffineTransformOp op = new AffineTransformOp(tx,
          AffineTransformOp.TYPE_BILINEAR);
      ret = op.filter(ret, null);
    }

    return ret;
  }
}
