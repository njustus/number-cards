package com.github.njustus.cards.lobby

import cats.effect.IO
import japgolly.scalajs.react.*
import japgolly.scalajs.react.vdom.html_<^.*
import org.scalajs.dom.window

object Lobby {
  case class State(sessionSelected: Option[SessionSelector.SelectedSession]=None)

  private def renderFn(state: Hooks.UseState[State]): VdomNode = {
    def onLobbySelected(lobby: SessionSelector.SelectedSession): IO[Unit] =
      state.setState(State(Some(lobby))).to[IO]

    state.value.sessionSelected match {
      case Some(lobby) =>
        window.document.title = s"Cards - ${lobby.username} (${lobby.sessionId})"
        //TODO render empty table with WebSocket, wait for others join, start game
        val head = <.div(^.className:="level has-background-info has-text-white",
          <.div(^.className:="level-item", s"SessionId: ${lobby.sessionId}"),
          <.div(^.className:="level-item",  s"Username: ${lobby.username}")
        )

        val content = SessionRoom.component(SessionRoom.Props(lobby))
        <.div(
          head,
          content
        )
      case None =>
        SessionSelector.component(SessionSelector.Props(onLobbySelected))
    }

  }

  val component = ScalaFnComponent.withHooks[Unit]
    .useState(State())
    .render((_, state) => renderFn(state))
}
