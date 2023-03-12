package com.github.njustus.cards.backend

import cats.instances.all._
import cats.syntax.all._
import cats.effect.IO
import cats.effect.std.Queue
import com.github.njustus.cards.shared.events._
import com.typesafe.scalalogging.LazyLogging

import scala.collection.mutable

class SessionStorage(sessions: mutable.Map[String, Session]) extends LazyLogging {
  //TODO drop closed connections
  // cleanup sessions without connections
  def create(id: String)(user: String, queue: Queue[IO, GameEventEnvelope]): IO[Unit] = {
    val updateIO = sessions.get(id) match {
      case None =>
        IO.delay {
          logger.info(s"create new session $id")
          Session(id, List.empty, Map(user -> queue))
        }
      case Some(session) =>
        IO.delay {
          logger.info(s"existing session: $id")
          val udpatedUsers = session.users.updated(user, queue)
          session.copy(users = udpatedUsers)
        }
    }

    for {
      session <- updateIO
      _ <- IO {
        sessions.update(id, session)
        logger.info(s"updated sessions: $sessions")
      }
      msgToRePublish = session.receivedMessagesStack.reverse
      _ <- msgToRePublish.traverse_(queue.offer)
    } yield ()
  }

  def publish(id: String, msg: GameEventEnvelope): IO[Unit] = {
    def publishAll(queues: List[Queue[IO, GameEventEnvelope]]) =
      queues.traverse_ { queue => queue.offer(msg) }

    for {
      session <- sessions.get(id).map(IO.pure).getOrElse(IO.never)
      queues = session.users.values.toList
      _ <- publishAll(queues)
      _ <- IO { sessions.update(id, session.addMessage(msg)) }
    } yield ()
  }
}

object SessionStorage {
  def empty: SessionStorage = new SessionStorage(mutable.Map.empty)
}
