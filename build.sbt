val ScalaJsReactVer = "2.1.1"
val circeVersion = "0.14.1"

lazy val commonSettings = Seq(
  scalaVersion := "2.13.9"
)

lazy val shared = (crossProject(JSPlatform, JVMPlatform).crossType(CrossType.Pure) in file("shared"))
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
  "io.circe" %%% "circe-core" % circeVersion,
  "io.circe" %%% "circe-generic" % circeVersion,
  "io.circe" %%% "circe-parser" % circeVersion
    )
  )
 .jvmSettings()
 .jsSettings()

lazy val frontend = (project in file("frontend"))
  .enablePlugins(ScalaJSPlugin)
  .dependsOn(shared.js)
  .settings(commonSettings)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    libraryDependencies ++=Seq(
        //"com.github.japgolly.scalajs-react" %%% "core" % ScalaJsReactVer,
        // Mandatory
        "com.github.japgolly.scalajs-react" %%% "core-bundle-cats_effect"  % ScalaJsReactVer,
        // Optional utils exclusive to scalajs-react
        "com.github.japgolly.scalajs-react" %%% "extra"                    % ScalaJsReactVer,
        // Optional extensions to `core` & `extra` for Monocle
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle2"       % ScalaJsReactVer,
        "com.github.japgolly.scalajs-react" %%% "extra-ext-monocle3"       % ScalaJsReactVer,
        "org.scala-js" %%% "scalajs-dom" % "2.1.0"
      )
  )

lazy val backend = (project in file("backend"))
  .dependsOn(shared.jvm)
  .settings(commonSettings)
