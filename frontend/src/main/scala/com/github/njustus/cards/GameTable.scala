package com.github.njustus.cards

import cats.effect.{IO, SyncIO}
import com.github.njustus.cards.CardComponent.Props
import com.github.njustus.cards.player.HandComponent
import com.github.njustus.cards.shared.dtos.{Card, Player}
import com.github.njustus.cards.shared.events.{CardPlayed, DrawCard, GameEvent}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

object GameTable {

  case class Props(currentPlayer: Player,
                    gameState: GameState,
                  publishMessage: GameEvent => IO[Unit]) {
    def playersHand = gameState.cardsPerPlayer.get(currentPlayer.name)
  }

  case class GameState(
                  closedCards: List[Card],
                  playedCards: List[Card],
                  cardsPerPlayer: Map[String, List[Card]]
                  ) {
    def playedCardCount = playedCards.size
    def remainingCardCount = closedCards.size

    def openCard = playedCards.headOption
  }


  private def renderClosedCards(props: Props): VdomNode = {
    <.div(
      <.span(s"x${props.gameState.closedCards.size}"),
      ^.className := "card closed-card",
      ^.onClick --> props.publishMessage(DrawCard)
    )
  }

  def renderOpenCard(value: GameState): VdomNode =
    CardComponent.component(value.openCard)

  private def renderFn(props: Props): VdomNode = {
    val state = props.gameState

    val hand = HandComponent.component(
      props.playersHand.getOrElse(List.empty),
      props.publishMessage)

    <.div(
      <.div(
        renderClosedCards(props),
        renderOpenCard(state),
        ^.className:="game-table-center"
      ),
      <.button("click me", ^.onClick --> IO { println("clicked UNHANDLED");  }),
      hand
    )
  }


  val component = ScalaFnComponent.withHooks[Props]
    .render { (props) =>
      renderFn(props)
    }
}
