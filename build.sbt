import NativePackagerKeys._

name := "prose-and-conz"

organization := "com.joescii"

version := "0.0.1"

scalaVersion := "2.11.7"

liftVersion <<= liftVersion ?? "3.0-M6"

liftEdition <<= liftVersion { _.substring(0,3) }

scalacOptions ++= Seq("-deprecation", "-unchecked")

resolvers ++= Seq(
//  "my-snapshots" at "http://ec2-54-173-168-127.compute-1.amazonaws.com:8080/archiva/repository/snapshots",
  "staging"   at "https://oss.sonatype.org/service/local/staging/deploy/maven2",
  "snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "releases"  at "https://oss.sonatype.org/content/repositories/releases"
)

seq(webSettings :_*)

unmanagedResourceDirectories in Test <+= (baseDirectory) { _ / "src/main/webapp" }

libraryDependencies <++= (liftVersion, liftEdition) { (ver, ed) =>
  Seq(
    "net.liftweb"             %% "lift-webkit"      % ver                     % "compile",
    "net.liftmodules"         %% ("lift-jquery-module_"+ed) % "2.9"           % "compile",
    "org.asciidoctor"         %  "asciidoctorj"     % "1.5.2"                 % "compile",
    "com.papertrailapp"       %  "logback-syslog4j" % "1.0.0"                 % Runtime,
    "org.eclipse.jetty"       %  "jetty-webapp"     % "9.2.7.v20150116"       % "compile",
    "org.eclipse.jetty"       %  "jetty-plus"       % "9.2.7.v20150116"       % "container,test", // For Jetty Config
    "org.eclipse.jetty.orbit" %  "javax.servlet"    % "3.0.0.v201112011016"   % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"          %  "logback-classic"  % "1.0.6"
  )
}

packageArchetype.java_application

bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts")

