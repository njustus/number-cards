package com.github.njustus.cards

import cats.effect.{IO, SyncIO}
import com.github.njustus.cards.CardComponent.Props
import com.github.njustus.cards.player.HandComponent
import com.github.njustus.cards.shared.dtos.{Card, Player}
import com.github.njustus.cards.shared.events.{CardPlayed, DrawCard, GameEvent, GameEventEnvelope}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.window

object GameTable {

  case class Props(currentPlayer: Player,
                  gameState: GameState,
                  publishMessage: GameEvent => IO[Unit]) {
    def playersHand: Option[List[Card]] = gameState.cardsPerPlayer.get(currentPlayer.name)

    def isCurrentsPlayerTurn: Boolean = currentPlayer == gameState.currentlyPlaying

    def displayOtherPlayers: String = {
      val players = for {
        (username, cards) <- gameState.cardsPerPlayer
        if username != currentPlayer.name
        cardCount = cards.size
      } yield s"$username: x$cardCount"

      players.mkString(" - ")
    }
  }

    case class GameState(
                          closedCards: List[Card],
                          playedCards: List[Card],
                          currentlyPlaying: Player,
                          cardsPerPlayer: Map[String, List[Card]]
                        ) {
      val players: Seq[Player] = cardsPerPlayer.keys.map(Player.apply).toSeq
      def playedCardCount: Int = playedCards.size

      def remainingCardCount: Int = closedCards.size

      def openCard: Option[Card] = playedCards.headOption

      //TODO geht nicht da jeder Teilnehmer eine unterschiedlich-sortierte Liste an Playern hat
      // auswahl muss backend treffen und als event propagieren
      def nextPlayer: Player = {
        val idx = players.indexOf(currentlyPlaying)
        if (idx < 0)
          throw new IllegalStateException(s"currentPlayer: $currentlyPlaying is not part of this session.")
        else if (idx == players.length-1)
          players.head
        else
          players(idx + 1)
      }
    }

  private def renderClosedCards(props: Props): VdomNode = {
    val onClickHandler: IO[Unit] =
      if(!props.isCurrentsPlayerTurn) IO.unit
      else props.publishMessage(DrawCard)

    <.div(
      <.span(s"x${props.gameState.closedCards.size}"),
      ^.className := "card closed-card",
      ^.onClick --> onClickHandler
    )
  }

  def renderOpenCard(value: GameState): VdomNode =
    CardComponent.component(value.openCard)

  private def renderFn(props: Props): VdomNode = {
    val state = props.gameState

    val hand = HandComponent.component(
      props.playersHand.getOrElse(List.empty),
      props.publishMessage,
      !props.isCurrentsPlayerTurn)

    <.div(
      <.p(s"Other Players: ${props.displayOtherPlayers}"),
      <.p(s"Currently playing: ${props.currentPlayer.name}"),
      <.div(
        renderClosedCards(props),
        renderOpenCard(props.gameState),
        ^.className:="game-table-center"
      ),
      <.p(s"Player: ${props.currentPlayer.name}"),
      hand
    )
  }


  val component = ScalaFnComponent.withHooks[Props]
    .render { (props) =>
      renderFn(props)
    }
}
