package com.github.njustus.cards.shared.events

import com.github.njustus.cards.shared.dtos.{Card, Player}

import scala.util.Random

sealed trait GameEvent {
}
//TODO group in session, game events for multiple engines to handle
case class PlayerJoined(player: Player) extends GameEvent
case object StartGame extends GameEvent
case class GameStarted(availableCards: List[Card],
                       cardsPerPlayer: Map[String, List[Card]]) extends GameEvent
case object DrawCard extends GameEvent
case class CardPlayed(card: Card) extends GameEvent
case class NumberEvent(n: Int) extends GameEvent

object GameEvent {

  def gameStarted(maxCards:Int, playerNames: Seq[Player]): GameStarted = {
    val allCards = Random.shuffle(Card.availableCards).toList
    playerNames.foldLeft(GameStarted(allCards, Map.empty)) { case (gameStarted, Player(username)) =>
      val (userCards, remainingCards) = gameStarted.availableCards.splitAt(maxCards)
      GameStarted(
        remainingCards,
        gameStarted.cardsPerPlayer.updated(username, userCards)
      )
    }
  }
}
