package com.github.njustus.cards.shared

package object dtos extends CirceConfig {
  implicit val symbolFormat = io.circe.generic.semiauto.deriveCodec[Card.Symbol]
  implicit val colorFormat = io.circe.generic.semiauto.deriveCodec[Card.Color]
  implicit val cardFormat = io.circe.generic.semiauto.deriveCodec[Card]
  implicit val playerFormat = io.circe.generic.semiauto.deriveCodec[Player]
}
