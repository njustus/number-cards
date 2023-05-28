package com.github.njustus.cards.backend

import cats.effect._
import com.comcast.ip4s._
import org.http4s.{HttpApp}
import org.http4s.implicits._
import org.http4s.ember.server._
import org.http4s.server.Router
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.server.staticcontent._
import scala.concurrent.duration._
object Main extends IOApp {

  def router(wsb: WebSocketBuilder2[IO]): HttpApp[IO] = {
    val state = SessionStorage.empty
    val fileConfig = FileService.Config[IO]("./frontend")

    Router(
      "/api" -> WsRouter.routes(wsb, state),
      "" -> fileService(fileConfig)
    ).orNotFound
  }

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpWebSocketApp(wsb => router(wsb))
      .withIdleTimeout(10.minutes)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}

