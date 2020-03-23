version := "0.1"

ThisBuild / scalaVersion := "2.13.1"

ThisBuild / organization := "dexpress"

val catsVersion     = "2.0.0"
val circeVersion    = "0.12.3"
val cirisVersion    = "1.0.0"
val doobieVersion   = "0.8.6"
val fs2Version      = "2.1.0"
val http4sVersion   = "0.21.0-M5"
val log4CatsVersion = "1.0.1"
val refinedVersion  = "0.9.10"

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .settings(
    name := "dexpress-api-scala",
    
    // scalafmtOnCompile := true,
    
    // required for specs2
    scalacOptions in Test ++= Seq("-Yrangepos"),
    
    /*
    assembly settings --------------------------------------------------------------------------------------------------
     */
    test in assembly := {},
    logLevel in assembly := Level.Info,
    mainClass in assembly := Some("dexpress.Main"),
    assemblyJarName := "dexpress-api-scala-assembly.jar",
    
    /*
    ammonite repl ------------------------------------------------------------------------------------------------------
     
    note: run `root/test:run` to start ammonite
    note: tests will not run if ammonite code is uncommented 
     */
//    sourceGenerators in Test += Def.task {
//      val file = (sourceManaged in Test).value / "amm.scala"
//      IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
//      Seq(file)
//    }.taskValue,
//
//    (fullClasspath in Test) ++= {
//      (updateClassifiers in Test).value
//        .configurations
//        .find(_.configuration == Test.name)
//        .get
//        .modules
//        .flatMap(_.artifacts)
//        .collect{ case (a, f) if a.classifier == Some("sources") => f }
//    },
      
    /*
    library dependencies -----------------------------------------------------------------------------------------------
     */
    libraryDependencies ++= Seq(
      "ch.qos.logback"     % "logback-classic"     % "1.2.3",
      "eu.timepit"        %% "refined"             % refinedVersion,
      "eu.timepit"        %% "refined-cats"        % refinedVersion,
      "ch.qos.logback"     % "logback-classic"     % "1.2.3",
      "ch.qos.logback"     % "logback-core"        % "1.2.3",
      "co.fs2"            %% "fs2-core"            % fs2Version,
      "co.fs2"            %% "fs2-io"              % fs2Version,
      "com.beachape"      %% "enumeratum"          % "1.5.13",
      "com.github.cb372"  %% "cats-retry"          % "1.1.0",
      "com.lihaoyi"        % "ammonite"            % "2.0.4"         % "test" cross CrossVersion.full,
      "io.chrisdavenport" %% "log4cats-slf4j"      % "1.0.1",
      "io.circe"          %% "circe-core"          % circeVersion,
      "io.circe"          %% "circe-generic"       % circeVersion,
      "io.circe"          %% "circe-parser"        % circeVersion,
      "io.circe"          %% "circe-optics"        % "0.12.0",
      "io.estatico"       %% "newtype"             % "0.4.3",
      "is.cir"            %% "ciris"               % "1.0.0",
      "org.flywaydb"       % "flyway-core"         % "6.1.0",
      "org.http4s"        %% "http4s-dsl"          % http4sVersion,
      "org.http4s"        %% "http4s-blaze-server" % http4sVersion,
      "org.http4s"        %% "http4s-blaze-client" % http4sVersion,
      "org.http4s"        %% "http4s-circe"        % http4sVersion,
      "org.scalatest"     %% "scalatest"           % "3.0.8"         % "test",
      "org.tpolecat"      %% "doobie-core"         % doobieVersion,
      "org.tpolecat"      %% "doobie-postgres"     % doobieVersion,
      "org.tpolecat"      %% "doobie-scalatest"    % "0.8.6"         % "test",
      "org.tpolecat"      %% "doobie-specs2"       % "0.8.6"         % "test",
      "org.typelevel"     %% "cats-core"           % catsVersion,
      "org.typelevel"     %% "cats-effect"         % catsVersion,
      "org.typelevel"     %% "mouse"               % "0.24",
      "org.scalacheck"    %% "scalacheck"          % "1.14.1"        % "test",
      "org.specs2"        %% "specs2-core"         % "4.6.0"         % "test"
    ),
    
    /*
    compiler options ---------------------------------------------------------------------------------------------------
     */
    scalacOptions ++= Seq(
      "-deprecation", // Emit warning and location for usages of deprecated APIs.
      "-explaintypes", // Explain type errors in more detail.
      "-feature", // Emit warning and location for usages of features that should be imported explicitly.
      "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
      "-language:experimental.macros", // Allow macro definition (besides implementation and application)
      "-language:higherKinds", // Allow higher-kinded types
      "-language:implicitConversions", // Allow definition of implicit functions called views
      "-unchecked", // Enable additional warnings where generated code depends on assumptions.
      "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
      //"-Xfatal-warnings", // Fail the compilation if there are any warnings.
      "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
      "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
      "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
      "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
      "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
      "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
      "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
      "-Xlint:nullary-override", // Warn when non-nullary `def f()' overrides nullary `def f'.
      "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
      "-Xlint:option-implicit", // Option.apply used implicit view.
      //"-Xlint:package-object-classes", // Class or object defined in package object.
      "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
      "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
      "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
      "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
      "-Ywarn-dead-code", // Warn when dead code is identified.
      "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
      "-Ywarn-numeric-widen", // Warn when numerics are widened.
      "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
      //"-Ywarn-unused:imports", // Warn if an import selector is not referenced.
      "-Ywarn-unused:locals", // Warn if a local definition is unused.
      "-Ywarn-unused:params", // Warn if a value parameter is unused.
      "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
      "-Ywarn-unused:privates", // Warn if a private member is unused.
      "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
      "-Ybackend-parallelism", "8", // Enable paralellisation — change to desired number!
      "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
      "-Ycache-macro-class-loader:last-modified", // and macro definitions. This can lead to performance improvements.
      "-Ymacro-annotations"
    ),
    
    /*
    console ------------------------------------------------------------------------------------------------------------
     */
    initialCommands in console := 
      """
        |import cats._
        |import cats.data._
        |import cats.implicits._
        |import cats.effect._
        |import doobie._
        |import doobie.implicits._
        |import doobie.util.ExecutionContexts
        |import doobie.postgres._
        |import doobie.postgres.implicits._
        |import eu.timepit.refined._
        |import eu.timepit.refined.api._
        |import eu.timepit.refined.auto._
        |import eu.timepit.refined.boolean._
        |import eu.timepit.refined.numeric._
        |import eu.timepit.refined.types.string._
        |import fs2.Stream
        |import org.http4s.circe._
        |import io.circe._
        |import io.circe.parser._
        |import io.circe.syntax._
        |import io.circe.generic.semiauto._
        |import org.http4s.{Request, Uri}
        |import org.flywaydb.core.Flyway
        |
        |import dexpress.clients.postgres._
        |import dexpress.clients.postgres.Statements._
        |import dexpress.codecs._
        |import dexpress.enums._
        |import dexpress.show._
        |import dexpress.types._
        |
        |import java.util.UUID
        |
        |implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
        |val xa = Transactor.fromDriverManager[IO](
        |  "org.postgresql.Driver",     // driver classname
        |  "jdbc:postgresql:inventory", // connect URL (driver-specific)
        |  "postgres",                  // user
        |  "password",                  // password
        |  Blocker.liftExecutionContext(ExecutionContexts.synchronous) // just for testing
        |)
        |
        |val flyway: Flyway = Flyway.configure().dataSource("jdbc:postgresql://0.0.0.0:5432/inventory", "postgres", "password").load()
        |val clientFlyway = new ClientFlyway(flyway)
  """.stripMargin
  )
