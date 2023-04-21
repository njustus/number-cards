package com.github.njustus.cards.lobby

import cats.Traverse
import cats.effect.std.Random
import japgolly.scalajs.react._
import japgolly.scalajs.react.facade.SyntheticFormEvent
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.Event
import cats.effect.{IO, SyncIO}
import cats.implicits._
object SessionSelector {
  case class SelectedSession(sessionId:String, username: String)
  case class Props(onSessionSelected: SelectedSession => IO[Unit])

  case class State(sessionIdInput:String="",
                   usernameInput:String="") {
    def selectedSession: SelectedSession = SelectedSession(sessionIdInput, usernameInput)
    def randomSession: IO[SelectedSession] =
      for {
        random <- Random.scalaUtilRandom[IO]
        randomNumbers = List.fill(5)(random.nextIntBounded(10))
        randomId <- Traverse[List].sequence(randomNumbers)
      } yield SelectedSession(randomId.mkString, usernameInput)
  }

  private def renderFn(props: Props, state: Hooks.UseState[State]): VdomNode = {
    def usernameChange (ev:ReactEventFromInput) = {
      val value = ev.target.value
      state.modState(s => s.copy(usernameInput=value))
    }

    def sessionIdChange (ev: ReactEventFromInput) = {
      val value = ev.target.value
      state.modState(s => s.copy(sessionIdInput = value))
    }

    def onJoinClicked(ev:ReactEvent) = IO {
      ev.preventDefault()
    } >> props.onSessionSelected(state.value.selectedSession)

    def onNewLobbyClicked(ev:ReactEvent) = {
      ev.preventDefault()
      state.value.randomSession.flatMap(props.onSessionSelected)
    }

    <.div(^.className:="section",
    <.form(
      <.div(^.className:="columns",
        <.label(^.className:="column is-one-quarter", "Username"),
        <.input(^.className:="column input",
          ^.value := state.value.usernameInput,
          ^.onChange ==> usernameChange)
      ),
      <.div(^.className:="columns",
        <.label(^.className:="column is-one-quarter", "SessionId"),
        <.input(^.className:="column input",
          ^.value := state.value.sessionIdInput,
          ^.onChange ==> sessionIdChange)
      ),
      <.div(^.className:="columns",
        <.div(^.className:="column is-one-quarter"),
          <.div(^.className:="buttons",
            <.button(^.className:="button is-primary",
        "Join",
        ^.onClick ==> onJoinClicked
      ),
      <.button(^.className:="button is-info",
        "New Lobby",
        ^.onClick ==> onNewLobbyClicked
      )
          )
        )
      )

    )
  }

  val component = ScalaFnComponent.withHooks[Props]
    .useState(State())
    .render((props, state) => renderFn(props, state))
}
