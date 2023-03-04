package com.github.njustus.cards.shared.events

case class Increment(number:Int)

object Increment {
  val format = io.circe.generic.semiauto.deriveCodec[Increment]
}
