package com.github.njustus.cards.backend

import cats.effect._
import com.comcast.ip4s._
import com.github.njustus.cards.shared.events._
import com.github.njustus.cards.shared.dtos._
import com.typesafe.scalalogging.LazyLogging
import fs2.Pipe
import io.circe._
import io.circe.parser
import org.http4s.{HttpApp, HttpRoutes}
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.ember.server._
import org.http4s.server.websocket.WebSocketBuilder2
import org.http4s.websocket.WebSocketFrame

import scala.concurrent.duration._
object WsRouter extends LazyLogging {

  private def encode[A:Encoder]: Pipe[IO, A, WebSocketFrame] = _.map { obj =>
    val json = implicitly[Encoder[A]].apply(obj).toString()
    logger.debug(s"encoded $json")
    WebSocketFrame.Text(json)
  }

  private def decode[A:Decoder]: Pipe[IO, WebSocketFrame, A] = _.map {
    case WebSocketFrame.Text(txt, bool) =>
      logger.debug(s"trying to decode $txt")
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

  def routes(wsb: WebSocketBuilder2[IO]): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "test" => Ok(s"test")
    case GET -> Root / "ws" / sessionId =>
      logger.info(s"new subscriber for session: $sessionId")
      val numbers = fs2.Stream.repeatEval(IO.pure(50))
        .debounce[IO](5.seconds)
        .map(NumberEvent.apply)
        .through(encode[GameEvent])

      val reader = decode[GameEvent].andThen(_.map { ev =>
        println(s"received event: $ev")
      })
      wsb.build(numbers, reader)
  }

}
