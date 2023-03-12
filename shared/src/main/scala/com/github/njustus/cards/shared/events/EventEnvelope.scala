package com.github.njustus.cards.shared.events

import io.circe._

import java.time.{Instant}

case class EventEnvelope[A: Encoder : Decoder](timestamp: Instant,
                                               sender: String,
                                               payload: A) {
  def isSender(name: String): Boolean = name == sender
}
