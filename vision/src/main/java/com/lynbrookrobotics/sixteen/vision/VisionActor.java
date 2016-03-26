package com.lynbrookrobotics.sixteen.vision;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.net.InetSocketAddress;

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

  public VisionActor(InetSocketAddress roboRIO) {
    this.roboRIO = roboRIO;

    // request creation of a SimpleSender
    final ActorRef mgr = Udp.get(getContext().system()).getManager();
    mgr.tell(UdpMessage.simpleSender(), getSelf());
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
        camera.read(frame);
        Tuple3<Mat, Double, Double> processed = TowerVision.detectHighGoal(frame);

        send.tell(UdpMessage.send(ByteString.fromString(
            processed.t2() + " " + processed.t3()
        ), roboRIO), getSelf());

        self().tell(new ProcessTarget(), getSelf());
      }
    };
  }
}
