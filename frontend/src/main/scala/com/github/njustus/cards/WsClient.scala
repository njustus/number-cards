package com.github.njustus.cards

import io.circe.{Decoder, Encoder, parser}
import io.circe.syntax._
import org.scalajs.dom.{Event, MessageEvent, WebSocket, console}
object WsClient {

  private def deserialize[T](str: String)(implicit decoder: Decoder[T]): Option[T] = {
    val decoded = parser.parse(str).flatMap(js => decoder.decodeJson(js))
    decoded match {
    case Left(err) =>
      console.error(s"Couldn't deserialize message: $str - errors: ${err}")
      None
    case Right(v) => Some(v)
    }
  }

  def create[In:Encoder, Out:Decoder](endpoint: String)(onMessage: Out => Unit): In => Unit = {
    val ws = new WebSocket(endpoint)

    ws.onopen = (ev:Event) => println(s"WsClient connected")
    ws.onclose = (ev: Event) =>println(s"WsClient disconnected")

    ws.onmessage = (msg:MessageEvent) => {
      println(s"WsClient - received msg: $msg")
      deserialize(msg.data.toString).foreach(onMessage)
    }

    (payload:In) => {
      println(s"sending: $payload")
      ws.send(payload.asJson.noSpaces)
    }
  }
}
