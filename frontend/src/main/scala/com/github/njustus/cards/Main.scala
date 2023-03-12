package com.github.njustus.cards

import com.github.njustus.cards.lobby.Lobby
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.document

import java.time.{Instant, LocalDateTime, ZoneOffset}

object Main {

  def main(args: Array[String]): Unit = {
    println("main loaded")
    println(s"date ${Instant.now()}")

    val root = document.getElementById("parent-root")
    <.div(
      Lobby.component(),
    ).renderIntoDOM(root)
  }
}
