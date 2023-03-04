package com.github.njustus.cards

import com.github.njustus.cards.Counter.CounterProps
import com.github.njustus.cards.shared.events.Increment
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document

import scala.scalajs.js.annotation.JSExportTopLevel
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

object Main {

  @JSExportTopLevel("click")
  def click() =
    println("button clicked")

  def main(args: Array[String]): Unit = {
    println("main loaded")

    val NoArgs = ScalaComponent.static("test")(<.div("Hello!"))
    val incre = Increment(50)
    val root = document.getElementById("parent-root")
    <.div(
      NoArgs(),
      <.p(s"test: ${incre.asJson}"),
      Counter.component(CounterProps(incre.number)),
      GameTable.component()
    ).renderIntoDOM(root)
  }
}
