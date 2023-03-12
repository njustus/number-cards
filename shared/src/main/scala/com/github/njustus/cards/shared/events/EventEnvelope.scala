package com.github.njustus.cards.shared.events

import io.circe._

import java.time.LocalDateTime

case class EventEnvelope[A: Encoder : Decoder](timestamp: LocalDateTime,
                                               sender: String,
                                               payload: A) {
  def isSender(name: String): Boolean = name == sender
}
