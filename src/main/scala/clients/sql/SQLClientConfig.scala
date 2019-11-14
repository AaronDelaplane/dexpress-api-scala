package clients.sql

import cats.implicits._
import ciris._
import eu.timepit.refined.types.net.UserPortNumber
import org.http4s.Uri
import common._

final case class SQLClientConfig(
    host: Uri,
    port: UserPortNumber,
    user: String,
    password: Secret[String],
    database: String
) {
  def driver: String = "org.postgresql.Driver"
  def url: String = s"jdbc:postgresql://$host:$port/$database"
}

object SQLClientConfig {
  val DEFAULT_POSTGRES_HOST: Uri = Uri.unsafeFromString("0.0.0.0")
  val DEFAULT_POSTGRES_PORT: UserPortNumber = UserPortNumber.unsafeFrom(5432)
  val DEFAULT_POSTGRES_USER: String = "postgres"
  val DEFAULT_POSTGRES_PASSWORD: Secret[String] = Secret("password")
  val DEFAULT_POSTGRES_DATABASE: String = "postgres"

  val configValue: ConfigValue[SQLClientConfig] =
    (
      env("POSTGRES_HOST").as[Uri].default(DEFAULT_POSTGRES_HOST),
      env("POSTGRES_PORT").as[UserPortNumber].default(DEFAULT_POSTGRES_PORT),
      env("POSTGRES_USER").as[String].default(DEFAULT_POSTGRES_USER),
      env("POSTGRES_PASSWORD")
        .as[Secret[String]]
        .default(DEFAULT_POSTGRES_PASSWORD),
      env("POSTGRES_DATABASE").as[String].default(DEFAULT_POSTGRES_DATABASE)
    ).parMapN(SQLClientConfig.apply)
}
