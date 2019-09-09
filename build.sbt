name := "Ddos Detect"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
	"org.apache.spark" %% "spark-core" % "2.2.0" % "provided",
	"org.apache.spark" %% "spark-streaming" % "2.2.0" % "provided",
	"org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.2.0"
)

assemblyMergeStrategy in assembly := {
 case PathList("META-INF", xs @ _*) => MergeStrategy.discard
 case x => MergeStrategy.first
}
