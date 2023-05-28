package com.github.njustus.cards.shared

import io.circe.Codec

package object dtos extends CirceConfig {
  implicit val symbolFormat: Codec.AsObject[Card.Symbol] = io.circe.generic.semiauto.deriveCodec[Card.Symbol]
  implicit val colorFormat: Codec.AsObject[Card.Color] = io.circe.generic.semiauto.deriveCodec[Card.Color]
  implicit val cardFormat: Codec.AsObject[Card] = io.circe.generic.semiauto.deriveCodec[Card]
  implicit val playerFormat: Codec.AsObject[Player] = io.circe.generic.semiauto.deriveCodec[Player]
}
