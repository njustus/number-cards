package com.github.njustus.cards.lobby

import com.github.njustus.cards.GameTable
import com.github.njustus.cards.shared.events.{GameEvent, GameStarted, PlayerJoined}
import org.scalajs.dom.console
object SessionEngine {
  def applyEvent(event: GameEvent): SessionRoom.State => SessionRoom.State = {
    val eventUpdate = event match {
    case PlayerJoined(player) =>
      SessionRoom.State.addPlayer(player)
    case GameStarted(availableCards, cardsPerPlayer) =>
      val gs = GameTable.GameState(availableCards.tail, List(availableCards.head), cardsPerPlayer)
      SessionRoom.State.setGameState(gs)
    case ev =>
      console.warn(s"Unknown event: $ev")
      identity[SessionRoom.State] _
    }

    eventUpdate.andThen(SessionRoom.State.addMessage(event))
  }
}
