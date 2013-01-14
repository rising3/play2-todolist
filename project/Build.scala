import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

  val appName = "play2-todolist"
  val appVersion = "1.0"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "org.scalaz" %% "scalaz-core" % "6.0.4",
    "se.radley" %% "play-plugins-salat" % "1.0.9",
    "org.specs2" %% "specs2" % "1.12" % "test",
    "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
    "org.mockito" % "mockito-all" % "1.9.5" % "test")

  val main = PlayProject(appName, appVersion, appDependencies, mainLang = SCALA).settings(
    // Add your own project settings here
    testOptions in Test := Nil,
    routesImport += "se.radley.plugin.salat.Binders._",
 	templatesImport += "org.bson.types.ObjectId",
    resolvers ++= Seq(
      "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "Sonatype OSS Releases" at "http://oss.sonatype.org/content/repositories/releases/",
      "Sonatype OSS groups" at "https://oss.sonatype.org/content/groups/scala-tools/",
      "Scala Tools Snapshots" at "http://scala-tools.org/repo-snapshots/",
      "repo.novus snaps" at "http://repo.novus.com/snapshots/"))
}
