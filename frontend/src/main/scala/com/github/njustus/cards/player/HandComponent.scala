package com.github.njustus.cards.player

import cats.effect.{IO, SyncIO}
import com.github.njustus.cards.CardComponent
import com.github.njustus.cards.shared.dtos.Card
import com.github.njustus.cards.shared.events.{CardPlayed, GameEvent}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object HandComponent {
  case class Props(cards: List[Card],
                  gameEventHandler: GameEvent => SyncIO[Unit])

  private def renderFn(props: Props): VdomNode = {
    println("render hand")
    val cards: List[VdomNode] = props.cards.map { card =>
      <.div(
        CardComponent.component(card),
        ^.onClick --> props.gameEventHandler(CardPlayed(card)).to[IO]
      )
    }
    <.div(
      ^.className:="player-hand")(
      cards:_*
    )
  }

  private val comp = ScalaFnComponent.withHooks[Props]
    .render(renderFn)

  def component(cards: List[Card],
                gameEventHandler: GameEvent => SyncIO[Unit]) = comp(Props(cards, gameEventHandler))
}
