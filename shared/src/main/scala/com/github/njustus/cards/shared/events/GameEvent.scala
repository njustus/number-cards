package com.github.njustus.cards.shared.events

import com.github.njustus.cards.shared.dtos.Card

sealed trait GameEvent {
}

case object DrawCard extends GameEvent
case class CardPlayed(card: Card) extends GameEvent
case class NumberEvent(n: Int) extends GameEvent
