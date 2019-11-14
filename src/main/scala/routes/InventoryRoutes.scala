package routes

import cats.effect.IO
import clients.sql.SQLClient
import clients.steam.SteamClient
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl

class InventoryRoutes(sqlClient: SQLClient, steamClient: SteamClient) extends Http4sDsl[IO] {
  
  def routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "inventory" / "refresh" / "steam" / "non-validated" / LongVar(steamId) =>
      Ok(steamClient.getSteamInventory(steamId))
  }
  
}
