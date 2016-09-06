package com.lynbrookrobotics.sixteen.rpivision;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;
import akka.io.Udp;
import akka.io.UdpMessage;
import akka.japi.Procedure;

import javaslang.Tuple;
import javaslang.Tuple2;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class VisionReceiverActor extends UntypedActor {
  private final Consumer<Tuple2<Double, Double>> onTarget;

  /**
   * Constructs an actor that listens for incoming vision target packets.
   */
  public VisionReceiverActor(Consumer<Tuple2<Double, Double>> onTarget) {
    this.onTarget = onTarget;

    final ActorRef mgr = Udp.get(getContext().system()).getManager();
    mgr.tell(
        UdpMessage.bind(getSelf(), new InetSocketAddress("192.168.42.1", 8846)),
        getSelf());
  }

  @Override
  public void onReceive(Object msg) {
    System.out.println(msg);
    if (msg instanceof Udp.Bound) {
      System.out.println("bound");
      getContext().become(ready());
    } else {
      unhandled(msg);
    }
  }

  private Procedure<Object> ready() {
    return msg -> {
      if (msg instanceof Udp.Received) {
        String data = ((Udp.Received) msg).data().utf8String();
        String[] split = data.split(Pattern.quote(" "));

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

