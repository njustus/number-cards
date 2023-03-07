package com.github.njustus.cards.backend

import cats.effect.IO
import cats.effect.std.Queue
import com.github.njustus.cards.shared.events.GameEvent
case class Session(id: String,
                   users: Map[String, Queue[IO, GameEvent]]) {
  def add(user:String, queue: Queue[IO, GameEvent]): Session =
    this.copy(users = this.users + (user -> queue))
}