javaOptions in Test += s"-Djava.library.path=${baseDirectory.value / "lib"}"

fork in Test := true

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.12"

resolvers += "Funky-Repo" at "http://team846.github.io/repo"
libraryDependencies += "com.lynbrookrobotics" % "funky-dashboard_2.11" % "0.1.0-SNAPSHOT"
