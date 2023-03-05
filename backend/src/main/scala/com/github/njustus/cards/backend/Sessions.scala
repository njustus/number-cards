package com.github.njustus.cards.backend

import cats.effect.IO
import cats.effect.std.Queue
case class Session(id: String,
                   users: Map[String, Queue[IO, String]]) {
  def add(user:String, queue: Queue[IO, String]) =
    this.copy(users = this.users + (user -> queue))
}


case class Sessions(sessions: Map[String, Session]) {
  def get(id: String) = sessions.get(id)

  def create(id: String)(user: String, queue: Queue[IO, String]) = {
    get(id)
    ???
  }
}
