package com.github.njustus.cards.lobby

import cats.effect.IO
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Lobby {
  case class State(lobbySelected: Option[LobbySelector.SelectedLobby]=None)

  private def renderFn(state: Hooks.UseState[State]): VdomNode = {
    def onLobbySelected(lobby: LobbySelector.SelectedLobby): IO[Unit] =
      state.setState(State(Some(lobby))).to[IO]

    state.value.lobbySelected match {
      case Some(lobby) =>
        //TODO render empty table with WebSocket, wait for others join, start game
        <.div(
      s"selected: $lobby"
    )
      case None =>
        LobbySelector.component(LobbySelector.Props(onLobbySelected))
    }

  }

  val component = ScalaFnComponent.withHooks[Unit]
    .useState(State())
    .render((_, state) => renderFn(state))
}
