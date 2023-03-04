package com.github.njustus.cards

import cats.effect.SyncIO
import com.github.njustus.cards.CardComponent.Props
import com.github.njustus.cards.player.HandComponent
import com.github.njustus.cards.shared.dtos.Card
import com.github.njustus.cards.shared.events.{CardPlayed, DrawCard, GameEvent}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object GameTable {

  val zero = GameState(
    List(Card(Card.Number(5), Card.BLUE), Card(Card.Number(8), Card.BLUE), Card(Card.Number(2), Card.RED)),
    List()
  )
  case class GameState(
                  closedCards: List[Card],
                  playedCards: List[Card]
                  ) {
    def playedCardCount = playedCards.size
    def remainingCardCount = closedCards.size

    def openCard = playedCards.headOption
  }


  private def renderClosedCards(state:Hooks.UseState[GameState]): VdomNode = {
    <.div(
      <.span(s"x${state.value.closedCards.size}"),
      ^.className := "card closed-card",
      ^.onClick --> state.modState(GameEngine.applyEvent(DrawCard))
    )
  }

  def renderOpenCard(value: GameState): VdomNode =
    CardComponent.component(value.openCard)

  private def renderFn(state: Hooks.UseState[GameState]): VdomNode = {
    val cards = (for {
      n <- 1 until 5
      c <- Seq(Card.BLUE, Card.RED)
    } yield Card(Card.Number(n), c)).toList


    val gameEventHandler = (ev:GameEvent) => state.modState(GameEngine.applyEvent(ev))
    val hand = HandComponent.component(cards, gameEventHandler)

    <.div(
      renderClosedCards(state),
      renderOpenCard(state.value),
      hand
    )
  }

  val component = ScalaFnComponent.withHooks[Unit]
    .useState(GameTable.zero)
    .render { (props, state) =>
      renderFn(state)
    }
}
