javaOptions in Test += s"-Djava.library.path=${baseDirectory.value / "lib"}"

fork in Test := true

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.12"