name := "Walker"

version := "1.0"

scalaVersion := "2.12.1"

libraryDependencies += "com.typesafe" % "config" % "1.3.1"

val akkaVersion = "2.5.6"
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j"   % akkaVersion,
  "com.typesafe.akka" %% "akka-remote"  % akkaVersion,
  "com.typesafe.akka" %% "akka-agent"   % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test"
)