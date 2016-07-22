package com.lynbrookrobotics.sixteen.vision;

import org.opencv.core.Core;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

// To launch on NUC
// java -Djava.library.path=/usr/local/share/OpenCV/java -jar vision-assemblyly-0.1-SNAPSHOT.jar
public class RemoteMain {
  public static void main(String[] args) {
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

    ActorSystem system = ActorSystem.apply("vision-remote");

    ActorRef visionActor = system.actorOf(Props.create(VisionActor.class));
  }
}
