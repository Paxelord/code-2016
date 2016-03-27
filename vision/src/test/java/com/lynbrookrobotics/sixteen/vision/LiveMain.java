package com.lynbrookrobotics.sixteen.vision;

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

import akka.japi.tuple.Tuple3;

public class LiveMain extends JPanel{
  BufferedImage image;

  public static void main (String args[]) throws InterruptedException{
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    LiveMain t = new LiveMain();
    VideoCapture camera = new VideoCapture(0);

    Mat frame = new Mat();
    camera.read(frame);

    if(!camera.isOpened()){
      System.out.println("Error");
    } else {
      while(true){
        if (camera.read(frame)) {
          Optional<Tuple3<Mat, Double, Double>> detect = TowerVision.detectHighGoal(frame);
          if (detect.isPresent()) {
            BufferedImage image = t.matToBufferedImage(detect.get().t1());
            t.image = image;
            t.repaint();
          } else {
            BufferedImage image = t.matToBufferedImage(frame);
            t.image = image;
            t.repaint();
          }
        }
      }
    }
    camera.release();
  }

  @Override
  public void paint(Graphics g) {
    g.drawImage(image, 0, 0, this);
  }

  public LiveMain() {
    JFrame frame0 = new JFrame();
    frame0.getContentPane().add(this);
    frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame0.setSize(1000, 1000);
    frame0.setVisible(true);
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