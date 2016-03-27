javaOptions in Test += s"-Djava.library.path=${baseDirectory.value / "lib"}"

fork in Test := true

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.3.12"

assemblyExcludedJars in assembly := {
  val cp = (fullClasspath in assembly).value
  cp filter {_.data.getName == "opencv-310.jar"}
}