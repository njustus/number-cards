package com.github.njustus.cards

import com.github.njustus.cards.GameTable.GameState
import com.github.njustus.cards.shared.events._

object GameEngine {
  def applyEvent(event: GameEvent)(state: GameState): GameState = event match {
    case DrawCard if state.closedCards.nonEmpty =>
      //TODO add to hand
      val (topCard :: tail) = state.closedCards
      state.copy(
        tail,
        topCard :: state.playedCards
      )
    case CardPlayed(card) =>
      //TODO check for incorrect moves
      //TODO remove from hand
      state.copy(playedCards = card :: state.playedCards)
    case ev => throw new IllegalArgumentException(s"Don't know how to handle $ev")
  }
}
