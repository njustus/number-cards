package com.github.njustus.cards.backend

import cats.Traverse
import cats.instances.all._
import cats.syntax.all._
import cats.effect.IO
import cats.effect.std.Queue
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

class SessionStorage(sessions: mutable.Map[String, Session]) extends LazyLogging {
  def create(id: String)(user: String, queue: Queue[IO, String]) = {
    val updateIO = sessions.get(id) match {
      case None =>
        IO.delay {
          logger.info(s"create new session $id")
          Session(id, Map(user -> queue))
        }
      case Some(session) =>
        IO.delay {
          logger.info(s"existing session: $id")
          val udpatedUsers = session.users.updated(user, queue)
          session.copy(users = udpatedUsers)
        }
    }

    updateIO.map { session =>
      sessions.update(id, session)
      logger.info(s"updated sessions: $sessions")
    }
  }

  def publish(id: String, msg: String) =
    for {
      session <- this.sessions.get(id).map(IO.pure).getOrElse(IO.never)
      queues = session.users.values.toList
      _ <- queues.traverse { queue =>
      queue.offer(msg)
    }
    } yield ()
}
