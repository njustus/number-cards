package com.github.njustus.cards

import cats.effect._
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Counter {
  case class CounterProps(initialCounter: Int)

//  private def render(props:CounterProps, state:Int)

  val component = ScalaFnComponent.withHooks[CounterProps]
    .useStateBy(props => props.initialCounter)
    .render { (props, state) =>

      val increment = state.modState(_+1)

      <.div(
        <.div("Counter: ", state.value),
        <.button("Increment",
          ^.onClick --> increment
        )
      )
    }
}
