package com.lynbrookrobotics.sixteen.vision;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Main {
  public static void main(String[] args) throws Exception {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    File inFolder = new File("./vision/test-images");
    String outFolder = "./vision/outImages/";
    new File(outFolder).mkdir();

    for (File file: inFolder.listFiles()) {
      if (file.isFile()) {
        System.out.println(file.getName());
        BufferedImage image = ImageIO.read(file);

        byte[] data = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        Mat mat = new Mat(image.getHeight(),image.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, data);

//        ArrayList<Mat> hsvChannel = new ArrayList<>();
//        Core.split(TowerVision.detectHighGoal(mat), hsvChannel);

        Imgcodecs.imwrite(outFolder + file.getName(), TowerVision.detectHighGoal(mat).get().t1());
      }
    }
  }
}
