package com.github.njustus.cards

import com.github.njustus.cards.shared.dtos.Card
import japgolly.scalajs.react._
import japgolly.scalajs.react.component.ScalaFn.Unmounted
import japgolly.scalajs.react.vdom.html_<^._

object CardComponent {
  case class Props(card:Option[Card])

  private def renderFn(props: Props): VdomNode = props.card match {
    case Some(card) =>
      <.div(
        card.symbol.display,
        ^.className := s"card ${card.color.className}"
      )
    case None =>
      <.div(
        "<empty>",
        ^.className := s"card empty-card"
      )
  }

  private val comp = ScalaFnComponent.withHooks[Props]
    .render(renderFn)

  def component(card:Card): Unmounted[Props] = component(Some(card))
  def component(cardOpt:Option[Card]): Unmounted[Props] = comp(Props(cardOpt))

}
