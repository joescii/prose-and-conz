name := "prose-and-conz"

organization := "com.joescii"

version := "0.0.1"

scalaVersion := "2.11.4"

liftVersion <<= liftVersion ?? "3.0-M3"

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
    "net.liftweb"             %% "lift-webkit"     % ver                     % "compile",
    "net.liftmodules"         %% ("lift-jquery-module_"+ed) % "2.9-SNAPSHOT" % "compile",
    "org.asciidoctor"         %  "asciidoctorj"    % "1.5.2"                 % "compile",
    "org.eclipse.jetty"       %  "jetty-webapp"    % "8.1.7.v20120910"       % "container,test",
    "org.eclipse.jetty.orbit" %  "javax.servlet"   % "3.0.0.v201112011016"   % "container,test" artifacts Artifact("javax.servlet", "jar", "jar"),
    "ch.qos.logback"          %  "logback-classic" % "1.0.6"
  )
}