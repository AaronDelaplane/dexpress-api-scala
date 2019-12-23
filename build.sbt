import com.typesafe.sbt.packager.docker.{Cmd, ExecCmd}

version := "0.1"

ThisBuild / scalaVersion := "2.13.1"

//scalacOptions += "-Ypartial-unification" // remove if updated to Scala v2.13

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
  .enablePlugins(JavaAppPackaging, DockerPlugin)
  .settings(
    name := "dexpress-inventory-service",
    
    // scalafmtOnCompile := true,
    
    // required for specs2
    scalacOptions in Test ++= Seq("-Yrangepos"),
    
    /*
    library dependencies
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
      "com.lihaoyi"        % "ammonite"            % "1.9.3"         % "test" cross CrossVersion.full,
      "io.chrisdavenport" %% "log4cats-slf4j"      % "1.0.1",
      "io.circe"          %% "circe-core"          % circeVersion,
      "io.circe"          %% "circe-generic"       % circeVersion,
      "io.circe"          %% "circe-parser"        % circeVersion,
      "io.circe"          %% "circe-optics"        % "0.12.0",
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
      "org.specs2"        %% "specs2-core"         % "4.6.0"         % "test"
    ),
    
    /*
    docker
     */
    dockerBaseImage := "adoptopenjdk/openjdk10:jdk-10.0.2.13-alpine-slim",
    dockerExposedPorts += 10000,
    dockerUpdateLatest := true,
    dockerAliases ++= Seq(dockerAlias.value.withTag(Option("dexpress-inventory-service"))),
    dockerCommands := Seq(
      Cmd("FROM", dockerBaseImage.value),
      Cmd("RUN", "apk add --no-cache bash"),
      Cmd("WORKDIR", "/opt/docker"),
      Cmd("ADD", "--chown=daemon:daemon opt /opt"),
      Cmd("USER", "daemon"),
      ExecCmd("ENTRYPOINT", "/opt/docker/bin/dexpress-inventory-service"),
      Cmd("CMD", "[]")
    ),
    
    /*
    console
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
        |import clients.postgres._
        |import codecs._
        |import datamaps._
        |import datatypes._
        |import enums._
        |import show._
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
