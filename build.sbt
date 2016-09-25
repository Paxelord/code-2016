enablePlugins(FRCPlugin)

organization := "com.lynbrookrobotics"
teamNumber := 846

name := "code-2016"

version := "0.4.0-SNAPSHOT"

robotClass := "com.lynbrookrobotics.sixteen.LaunchRobot"

scalaVersion := "2.11.7"

lazy val vision = project
lazy val rpivision = project

resolvers += "WPILib-Maven" at "http://team846.github.io/wpilib-maven"
resolvers += "Funky-Repo" at "http://team846.github.io/repo"

libraryDependencies += "com.lynbrookrobotics" % "potassium" % "0.1-SNAPSHOT"

libraryDependencies += "com.lynbrookrobotics" % "funky-dashboard_2.11" % "0.1.0-SNAPSHOT"

libraryDependencies += "com.typesafe" % "config" % "1.3.0"

libraryDependencies += "org.mockito" % "mockito-core" % "1.10.19" % Test

libraryDependencies += "junit" % "junit" % "4.12"

libraryDependencies += "com.novocode" % "junit-interface" % "0.11" % "test"

import com.etsy.sbt.checkstyle._
Checkstyle.xsltTransformations := {
  Some(Set(XSLTSettings(baseDirectory(_ / "checkstyle-noframes.xsl").value, target(_ / "checkstyle-report.html").value)))
}

offline := true
staticIP := true
