package com.github.njustus.cards.player

import com.github.njustus.cards.CardComponent
import com.github.njustus.cards.shared.dtos.Card
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object HandComponent {
  case class Props(cards: List[Card])

  private def renderFn(props: Props): VdomNode = {
    val cards: List[VdomNode] = props.cards.map(CardComponent.component(_))
    <.div(
      ^.className:="player-hand")(
      cards:_*
    )
  }

  private val comp = ScalaFnComponent.withHooks[Props]
    .render(renderFn)

  def component(cards: List[Card]) = comp(Props(cards))
}
