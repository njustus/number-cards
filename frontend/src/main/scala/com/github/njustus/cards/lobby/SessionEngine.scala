package com.github.njustus.cards.lobby

import com.github.njustus.cards.shared.events.{GameEvent, PlayerJoined}
import org.scalajs.dom.console
object SessionEngine {
  def applyEvent(event: GameEvent): SessionRoom.State => SessionRoom.State = {
    val eventUpdate = event match {
    case PlayerJoined(player) =>
      SessionRoom.State.addPlayer(player)
    case ev =>
      console.warn(s"Unknown event: $ev")
      identity[SessionRoom.State] _
    }

    eventUpdate.andThen(SessionRoom.State.addMessage(event))
  }
}
