package com.lynbrookrobotics.sixteen.vision;

import com.lynbrookrobotics.funkydashboard.FunkyDashboard;
import com.lynbrookrobotics.funkydashboard.ImageStream;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;
import akka.japi.Pair;
import akka.util.ByteString;

public class VisionActor extends UntypedActor {
  private static class ProcessTarget {}
  private static ProcessTarget processTargetInstance = new ProcessTarget();

  final InetSocketAddress roboRIO;

  private final VideoCapture camera = new VideoCapture(0);

  public VisionActor() {
    this(new InetSocketAddress("10.8.46.2", 8846));
  }

  private Mat lastProcessedImage = new Mat();

  public VisionActor(InetSocketAddress roboRIO) {
    this.roboRIO = roboRIO;

    camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 320);
    camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 200);

    try {
      new ProcessBuilder("/usr/bin/v4l2-ctl", "-c", "exposure_auto=1", "-c", "exposure_absolute=400", "-c", "gain=200").start();
    } catch (IOException e) {
      e.printStackTrace();
    }

    // request creation of a SimpleSender
    final ActorRef mgr = Udp.get(getContext().system()).getManager();
    mgr.tell(UdpMessage.simpleSender(), getSelf());

    FunkyDashboard dashboard = new FunkyDashboard();
    dashboard.bindRoute("0.0.0.0", 8080, getContext().system());
    dashboard.datasetGroup("vision").addDataset(
        new ImageStream(
            "Vision Output",
            () -> Utils.matToBufferedImage(lastProcessedImage, 500, 0)
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
    self().tell(processTargetInstance, getSelf());

    Mat frame = new Mat();

    return msg -> {
      if (msg instanceof ProcessTarget) {
        try {
          //long start = System.currentTimeMillis();
          if (camera.read(frame)) {
            Optional<Pair<Double, Double>> detect = TowerVision.detectHighGoal(frame);

            detect.ifPresent(processed -> {
              send.tell(UdpMessage.send(ByteString.fromString(
                  processed.first() + " " + processed.second()
              ), roboRIO), getSelf());
            });

            lastProcessedImage = frame.clone();
            //System.out.println("time: " + (System.currentTimeMillis() - start));
          }
        } catch (Throwable throwable) {
          throwable.printStackTrace();
        }

        self().tell(processTargetInstance, getSelf());
      }
    };
  }
}
