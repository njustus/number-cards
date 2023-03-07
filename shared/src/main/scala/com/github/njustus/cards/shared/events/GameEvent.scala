package com.github.njustus.cards.shared.events

import com.github.njustus.cards.shared.dtos.{Card, Player}

sealed trait GameEvent {
}
//TODO group in session, game events for multiple engines to handle
case class PlayerJoined(player: Player) extends GameEvent
case object StartGame extends GameEvent
case object DrawCard extends GameEvent
case class CardPlayed(card: Card) extends GameEvent
case class NumberEvent(n: Int) extends GameEvent
