package com.github.njustus.cards

import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document

import scala.scalajs.js.annotation.JSExportTopLevel

object Main {

  @JSExportTopLevel("click")
  def click() =
    println("button clicked")

  def main(args: Array[String]): Unit = {
    println("main loaded")

    val NoArgs = ScalaComponent.static("test")(<.div("Hello!"))

    val root = document.getElementById("parent-root")
    NoArgs().renderIntoDOM(root)
  }
}
