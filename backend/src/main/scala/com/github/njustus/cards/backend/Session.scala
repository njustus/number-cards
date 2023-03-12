package com.github.njustus.cards.backend

import cats.effect.IO
import cats.effect.std.Queue
import com.github.njustus.cards.shared.events.GameEventEnvelope
case class Session(id: String,
                   receivedMessagesStack: List[GameEventEnvelope],
                   users: Map[String, Queue[IO, GameEventEnvelope]]) {
  def add(user:String, queue: Queue[IO, GameEventEnvelope]): Session =
    this.copy(users = this.users + (user -> queue))

  def addMessage(msg: GameEventEnvelope): Session =
    this.copy(receivedMessagesStack = msg :: this.receivedMessagesStack)
}
