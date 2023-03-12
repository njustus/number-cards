package com.github.njustus.cards.shared

import io.circe._
import io.circe.generic.semiauto._

import java.time.{Instant}
package object events extends CirceConfig {
  import events._

  implicit val gameEventFormat: Codec.AsObject[GameEvent] = deriveCodec[GameEvent]
  implicit def envelopeFormat[A:Encoder:Decoder]: Codec.AsObject[EventEnvelope[A]] = deriveCodec[EventEnvelope[A]]

  type GameEventEnvelope = EventEnvelope[GameEvent]

  def gameEventEnvelope(sender:String, payload:GameEvent): EventEnvelope[GameEvent] =
    EventEnvelope(Instant.now,sender, payload)

}
