package com.lynbrookrobotics.sixteen.tasks.shooter;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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
        UdpMessage.bind(getSelf(), new InetSocketAddress("roborio-846-frc.local", 8846)),
        getSelf());
  }

  @Override
  public void onReceive(Object msg) {
    System.out.println(msg);
    if (msg instanceof Udp.Bound) {
      System.out.println("bound");
      getContext().become(ready());
    } else unhandled(msg);
  }

  private Procedure<Object> ready() {
    return msg -> {
      if (msg instanceof Udp.Received) {
        String data = ((Udp.Received) msg).data().utf8String();
//        System.out.println(data);
        String[] split = data.split(Pattern.quote(" "));
//        System.out.println(Arrays.toString(split));

        if (split.length == 2) {
          onTarget.accept(Tuple.of(
              Double.parseDouble(split[0]),
              Double.parseDouble(split[1])
          ));
        }
      }
    };
  }
}
