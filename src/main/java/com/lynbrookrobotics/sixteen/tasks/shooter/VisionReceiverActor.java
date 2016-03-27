package com.lynbrookrobotics.sixteen.tasks.shooter;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;
import javaslang.Tuple;
import javaslang.Tuple2;

public class VisionReceiverActor extends UntypedActor {
//  final InetSocketAddress remote;
  private final Consumer<Tuple2<Double, Double>> onTarget;

  public VisionReceiverActor(InetSocketAddress remote, Consumer<Tuple2<Double, Double>> onTarget) {
//    this.remote = remote;
    this.onTarget = onTarget;

    // request creation of a SimpleSender
    final ActorRef mgr = Udp.get(getContext().system()).getManager();
    mgr.tell(
        UdpMessage.bind(getSelf(), new InetSocketAddress("localhost", 5846)),
        getSelf());
  }

  @Override
  public void onReceive(Object msg) {
    if (msg instanceof Udp.Bound) {
      getContext().become(ready());
    } else unhandled(msg);
  }

  private Procedure<Object> ready() {
    return msg -> {
      if (msg instanceof Udp.Received) {
        String[] split = ((Udp.Received) msg).data().mkString("").split("//s+");
        onTarget.accept(Tuple.of(
            Double.parseDouble(split[0]),
            Double.parseDouble(split[1])
        ));
      }
    };
  }
}
