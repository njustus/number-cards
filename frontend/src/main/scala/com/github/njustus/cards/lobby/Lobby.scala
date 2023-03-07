package com.github.njustus.cards.lobby

import cats.effect.IO
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._

object Lobby {
  case class State(sessionSelected: Option[SessionSelector.SelectedSession]=None)

  private def renderFn(state: Hooks.UseState[State]): VdomNode = {
    def onLobbySelected(lobby: SessionSelector.SelectedSession): IO[Unit] =
      state.setState(State(Some(lobby))).to[IO]

    state.value.sessionSelected match {
      case Some(lobby) =>
        //TODO render empty table with WebSocket, wait for others join, start game
        val head = <.div(<.p(s"SessionId: ${lobby.sessionId} - Username: ${lobby.sessionId}"))
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
