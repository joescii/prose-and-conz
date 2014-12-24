import sbt._
import sbt.Keys._

object ProseAndConz extends Build {

  val liftVersion = SettingKey[String]("liftVersion", "Full version number of the Lift Web Framework")

  val liftEdition = SettingKey[String]("liftEdition", "Lift Edition (short version number to append to artifact name)")

  lazy val project = Project(
    id = "prose-and-conz", 
    base = file("."),
    settings = Project.defaultSettings)
}
