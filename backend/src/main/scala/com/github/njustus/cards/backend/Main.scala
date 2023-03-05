package com.github.njustus.cards.backend

import cats.effect._
import com.comcast.ip4s._
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._
import org.http4s.server.Router
import org.http4s.server.websocket.WebSocketBuilder2
object Main extends IOApp {

  val helloWorldService = HttpRoutes.of[IO] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }.orNotFound

  def router(wsb: WebSocketBuilder2[IO]): HttpApp[IO] =
    Router("/api" -> WsRouter.routes(wsb)).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpWebSocketApp(wsb => router(wsb))
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}

