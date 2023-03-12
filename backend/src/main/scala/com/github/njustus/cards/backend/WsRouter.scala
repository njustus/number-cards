package com.github.njustus.cards.backend

import cats.data.StateT
import cats.effect._
import cats.effect.std.{Queue, QueueSource}
import com.comcast.ip4s._
import com.github.njustus.cards.shared.events._
import com.github.njustus.cards.shared.dtos._
import com.typesafe.scalalogging.LazyLogging
import fs2.Pipe
import io.circe._
import io.circe.parser
import org.http4s.{HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame

object WsRouter extends LazyLogging {

  private def encode[A:Encoder]: Pipe[IO, A, WebSocketFrame] = _.map { obj =>
    val json = implicitly[Encoder[A]].apply(obj).toString()
    logger.trace(s"encoded $json")
    WebSocketFrame.Text(json)
  }

  private def decode[A:Decoder]: Pipe[IO, WebSocketFrame, A] = _.map {
    case WebSocketFrame.Text(txt, bool) =>
      logger.trace(s"trying to decode $txt")
      parser.decode[A](txt) match {
        case Left(err) =>
          logger.warn(s"couldn't deserialize $txt. Errors: $err")
          None
        case Right(v) =>
          Some(v)
      }
    case frame =>
      logger.warn(s"can not handle WebSocketFrame: $frame")
      None
  }.collect {
    case Some(x) => x
  }

  def routes(wsb: WebSocketBuilder2[IO], sessionStorage: SessionStorage): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "test" => Ok(s"test")
    case GET -> Root / "ws" / sessionId / username =>
      logger.info(s"new subscriber for session: $sessionId: $username")

      val broadcaster = decode[GameEventEnvelope].andThen(_.evalMap { ev =>
        logger.debug(s"sessionId: $sessionId, username: $username - received event: $ev")
        sessionStorage.publish(sessionId, ev)
      })

      for {
        queue <- Queue.unbounded[IO, GameEventEnvelope]
        _ <- sessionStorage.create(sessionId)(username, queue)
        eventsOutput = fs2.Stream.fromQueueUnterminated[IO, GameEventEnvelope](queue, 1024)
          .through(encode[GameEventEnvelope])
        ws <- wsb.build(eventsOutput, broadcaster)
        _ <- sessionStorage.publish(sessionId, gameEventEnvelope(username, PlayerJoined(Player(username))))
      } yield ws
  }

}
