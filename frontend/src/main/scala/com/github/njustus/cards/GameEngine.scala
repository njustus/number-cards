package com.github.njustus.cards

import com.github.njustus.cards.GameTable.GameState
import com.github.njustus.cards.shared.dtos.Player
import com.github.njustus.cards.shared.events._
import org.scalajs.dom.console

object GameEngine {
  def applyEvent(currentPlayer: Player, event: GameEventEnvelope)(state: GameState): GameState = event.payload match {
    case DrawCard if state.closedCards.nonEmpty =>
      //TODO add to hand if event from currentPlayer
      val (topCard :: tail) = state.closedCards

      state.copy(
        tail,
        topCard :: state.playedCards,
        state.cardsPerPlayer.updatedWith(currentPlayer.name) {
          case Some(cards) =>
            Some(topCard::cards)
          case None => None
        }
      )
    case CardPlayed(card) =>
      //TODO check for incorrect moves
      //TODO remove from hand
      state.copy(playedCards = card :: state.playedCards)
    case ev =>
      console.warn(s"Unknown event: $ev")
      state
  }
}
