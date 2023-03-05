package com.github.njustus.cards

import io.circe.Encoder
import io.circe.syntax._
import org.scalajs.dom.{Event, MessageEvent, WebSocket, document, window}
object WsClient {
  def create[T:Encoder](endpoint: String)(onMessage: String => Unit): T => Unit = {
    val ws = new WebSocket(endpoint)

    ws.onopen = (ev:Event) => println(s"WsClient connected")
    ws.onclose = (ev: Event) =>println(s"WsClient disconnected")

    ws.onmessage = (msg:MessageEvent) => {
      println(s"WsClient - received msg: $msg")
      onMessage(msg.data.toString)
    }

    (payload:T) => {
      println(s"sending: $payload")
      ws.send(payload.asJson.noSpaces)
    }
  }
}
