package com.github.njustus.cards.shared

package object events extends CirceConfig {
  import events._

  implicit val gameEventFormat = io.circe.generic.semiauto.deriveCodec[GameEvent]
}
