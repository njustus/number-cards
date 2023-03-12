package com.github.njustus.cards.lobby

import com.github.njustus.cards.shared.dtos.Player
import com.github.njustus.cards.{GameEngine, GameTable}
import com.github.njustus.cards.shared.events.{GameEventEnvelope, GameStarted, PlayerJoined}
import org.scalajs.dom.console
object SessionEngine {
  def applyEvent(currentPlayer: Player, event: GameEventEnvelope): SessionRoom.State => SessionRoom.State = {
    val eventUpdate = event.payload match {
    case PlayerJoined(player) =>
      SessionRoom.State.addPlayer(player)
    case GameStarted(availableCards, cardsPerPlayer) =>
      val gs = GameTable.GameState(availableCards.tail, List(availableCards.head), cardsPerPlayer)
      SessionRoom.State.setGameState(gs)
    case ev =>
      (state: SessionRoom.State) => state match {
        case SessionRoom.State(_,_,Some(gameState)) =>
          val newGs = GameEngine.applyEvent(currentPlayer, event)(gameState)
          SessionRoom.State.setGameState(newGs)(state)
        case _ =>
          console.warn(s"Unknown event: $ev")
          state
      }
    }

    eventUpdate.andThen(SessionRoom.State.addMessage(event))
  }
}
