package com.github.njustus.cards.shared.events

sealed trait GameEvent {
}

case object DrawCard extends GameEvent
