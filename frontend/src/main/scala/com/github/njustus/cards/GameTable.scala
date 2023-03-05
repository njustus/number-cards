package com.github.njustus.cards

import cats.effect.{IO, SyncIO}
import com.github.njustus.cards.CardComponent.Props
import com.github.njustus.cards.player.HandComponent
import com.github.njustus.cards.shared.dtos.Card
import com.github.njustus.cards.shared.events.{CardPlayed, DrawCard, GameEvent}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

object GameTable {

  val zero = GameState(
    List(Card(Card.Symbol(5), Card.BLUE), Card(Card.Symbol(8), Card.BLUE), Card(Card.Symbol(2), Card.RED)),
    List(),
    s => {println(s"WARNING UNUSED $s")}
  )
  case class GameState(
                  closedCards: List[Card],
                  playedCards: List[Card],
                  var publishMessage: String => Unit,
                  text: String = ""
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

  private def renderFn(state: Hooks.UseState[GameState], pubMsg: Hooks.UseState[String => Unit]): VdomNode = {
    val cards = (for {
      n <- 1 until 5
      c <- Seq(Card.BLUE, Card.RED)
    } yield Card(Card.Symbol(n), c)).toList


    val gameEventHandler = (ev:GameEvent) => state.modState(GameEngine.applyEvent(ev))
    val hand = HandComponent.component(cards, gameEventHandler)

    <.div(
      <.p("text "+state.value.text),
      <.div(
        renderClosedCards(state),
        renderOpenCard(state.value),
        ^.className:="game-table-center"
      ),
      <.button("click me", ^.onClick --> IO { println("clicked"); pubMsg.value("clicked") }),
      hand
    )
  }


  val component = ScalaFnComponent.withHooks[Unit]
    .useState(GameTable.zero)
    .useState((s: String) => {println(s"UNUSED initial: $s")})
    .useEffectOnMountBy { (_, gameStateHook, fnState) =>
      val gameState = gameStateHook.raw._1
      val setGameState = gameStateHook.raw._2
      val setFnState = fnState.raw._2

      SyncIO {
        val publishMessage: String => Unit = WsClient.create[String](s"ws://${window.location.host}/api/ws/1111/nico")(
          str => {
            setGameState(gameState.copy(text = gameState.text + "  " + str))
          })

        setFnState(publishMessage)
      }
    }
    .render { (props, state, pubMsg) =>
      renderFn(state, pubMsg)
    }
}
