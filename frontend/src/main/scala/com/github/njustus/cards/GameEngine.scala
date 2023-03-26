package com.github.njustus.cards

import com.github.njustus.cards.GameTable.GameState
import com.github.njustus.cards.shared.dtos._
import com.github.njustus.cards.shared.events._
import org.scalajs.dom.console

object GameEngine {
  def applyEvent(currentPlayer: Player, event: GameEventEnvelope)(state: GameState): GameState = event.payload match {
    case DrawCard if state.closedCards.nonEmpty =>
      val (topCard :: tail) = state.closedCards

      state.copy(
        closedCards = tail,
        currentlyPlaying = state.nextPlayer,
        cardsPerPlayer = updatePlayer(state, event.sender) { cards =>
          topCard::cards
        }
      )
    case CardPlayed(card) =>
      //TODO check for incorrect moves
      state.copy(
        playedCards = card :: state.playedCards,
        currentlyPlaying = state.nextPlayer,
        cardsPerPlayer = updatePlayer(state, event.sender) { cards =>
          cards.filter(card2 => card2 != card)
        }
      )
    case ev =>
      console.warn(s"Unknown event: $ev")
      state
  }

  private def updatePlayer(state: GameState, playerName: String)(fn: List[Card] => List[Card]): Map[String, List[Card]] =
    state.cardsPerPlayer.updatedWith(playerName) {
      case Some(cards) =>
        Some(fn(cards))
      case None => None
    }
}
