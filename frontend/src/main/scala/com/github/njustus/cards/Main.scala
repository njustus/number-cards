package com.github.njustus.cards

import com.github.njustus.cards.lobby.Lobby
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.{document}

object Main {

  def main(args: Array[String]): Unit = {
    println("main loaded")

    val root = document.getElementById("parent-root")
    <.div(
      Lobby.component(),
    ).renderIntoDOM(root)
  }
}
