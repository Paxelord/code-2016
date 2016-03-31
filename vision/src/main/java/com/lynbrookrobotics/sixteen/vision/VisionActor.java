package com.lynbrookrobotics.sixteen.vision;

import com.lynbrookrobotics.funkydashboard.FunkyDashboard;
import com.lynbrookrobotics.funkydashboard.ImageStream;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.net.InetSocketAddress;
import java.util.Optional;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;
import akka.japi.tuple.Tuple3;
import akka.util.ByteString;

public class VisionActor extends UntypedActor {
  private static class ProcessTarget {
  }

  final InetSocketAddress roboRIO;

  private final VideoCapture camera = new VideoCapture(0);

  public VisionActor() {
    this(new InetSocketAddress("roborio-846-frc.local", 8846));
  }

  BufferedImage lastImage = null;
  public VisionActor(InetSocketAddress roboRIO) {
    this.roboRIO = roboRIO;

    // request creation of a SimpleSender
    final ActorRef mgr = Udp.get(getContext().system()).getManager();
    mgr.tell(UdpMessage.simpleSender(), getSelf());

    FunkyDashboard dashboard = new FunkyDashboard();
    dashboard.bindRoute("tarsier.local", 8080, getContext().system());
    dashboard.datasetGroup("vision").addDataset(
        new ImageStream(
            "Vision Output",
            () -> lastImage
        )
    );
  }

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof Udp.SimpleSenderReady) {
      getContext().become(ready(getSender()));
    } else unhandled(msg);
  }

  private Procedure<Object> ready(final ActorRef send) {
    self().tell(new ProcessTarget(), getSelf());

    return msg -> {
      if (msg instanceof ProcessTarget) {
        Mat frame = new Mat();
        try {
          if (camera.read(frame)) {
            Optional<Tuple3<Mat, Double, Double>> detect = TowerVision.detectHighGoal(frame);
            detect.ifPresent(processed -> {
              lastImage = matToBufferedImage(processed.t1());
              processed.t1().release();
              send.tell(UdpMessage.send(ByteString.fromString(
                  processed.t2() + " " + processed.t3()
              ), roboRIO), getSelf());
            });

            if (!detect.isPresent()) {
              lastImage = matToBufferedImage(frame);
            }
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        frame.release();

        self().tell(new ProcessTarget(), getSelf());
      }
    };
  }

  private BufferedImage matToBufferedImage(Mat frame) {
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

    Image resize = image.getScaledInstance(200, (int) (frame.height() * (200D / frame.width())), Image.SCALE_SMOOTH);
    BufferedImage ret = new BufferedImage(200, (int) (frame.height() * (200D / frame.width())), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = ret.createGraphics();
    g2d.drawImage(resize, 0, 0, null);
    g2d.dispose();

    return ret;
  }
}
