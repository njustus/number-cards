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

    def allPlayers: List[Player] = gameState.cardsPerPlayer.keys.map(Player).toList
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

  def renderPlayerState(props: Props): VdomNode = {
    def playerClassName(p:Player) = if(p == props.gameState.currentlyPlaying) "has-text-weight-bold" else ""

    <.div(
      <.h4(^.className:="is-size-4", "Players"),
      <.ol(
        (props.gameState.players.map(p => <.li(^.className:=playerClassName(p), p.name)) :+
        (^.className:="column")
        ):_*
      )
    )
  }

  private def renderFn(props: Props): VdomNode = {
    val hand = HandComponent.component(
      props.playersHand.getOrElse(List.empty),
      props.publishMessage,
      !props.isCurrentsPlayerTurn)

    <.div(^.className:="section",
      <.div(^.className:="columns",
          <.div(^.className:="column", renderClosedCards(props)),
          <.div(^.className:="column", renderOpenCard(props.gameState)),
        <.div(^.className:="column", renderPlayerState(props))
      ),
      hand
    )
  }


  val component = ScalaFnComponent.withHooks[Props]
    .render { (props) =>
      renderFn(props)
    }
}
