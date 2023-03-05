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

  def readStdIn(sessionId: String, sessionStorage: SessionStorage): IO[Unit] = {
    var console = cats.effect.std.Console[IO]
    for {
      _ <- console.println("enter line to send..")
      line <- console.readLine
      _ <- console.println(s"sending $line")
      _ <- sessionStorage.publish(sessionId, line)
      _ <- readStdIn(sessionId, sessionStorage)
    } yield ()
  }

  def routes(wsb: WebSocketBuilder2[IO], sessionStorage: SessionStorage): HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "test" => Ok(s"test")
    case GET -> Root / "ws" / sessionId / username =>
      logger.info(s"new subscriber for session: $sessionId")


      val numbers = fs2.Stream.repeatEval(IO.pure(50))
        .debounce[IO](5.seconds)
        .map(NumberEvent.apply)
        .through(encode[GameEvent])

      val reader = decode[GameEvent].andThen(_.map { ev =>
        println(s"received event: $ev")
      })

      for {
        //TODO somehow prevent timeouts with ping/pong?
        queue <- Queue.unbounded[IO, String]
        _ <- sessionStorage.create(sessionId)(username, queue)
        numbers = fs2.Stream.fromQueueUnterminated[IO, String](queue, 1024)
          .through(encode[String])
        ws <- wsb.build(numbers, reader)
        _ <- readStdIn(sessionId, sessionStorage).start
      } yield ws
  }

}
