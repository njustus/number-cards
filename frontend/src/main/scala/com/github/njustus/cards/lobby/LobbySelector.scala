package com.github.njustus.cards.lobby

import cats.Traverse
import cats.effect.std.Random
import japgolly.scalajs.react._
import japgolly.scalajs.react.facade.SyntheticFormEvent
import japgolly.scalajs.react.vdom.html_<^._
import org.scalajs.dom.Event
import cats.effect.{IO, SyncIO}
import cats.implicits._
object LobbySelector {
  case class SelectedLobby(sessionId:String, username: String)
  case class Props(onLobbySelected: SelectedLobby => IO[Unit])

  case class State(sessionIdInput:String="",
                   usernameInput:String="") {
    def selectedLobby = SelectedLobby(sessionIdInput, usernameInput)
    def randomLobby =
      for {
        random <- Random.scalaUtilRandom[IO]
        randomNumbers = List.fill(5)(random.nextIntBounded(10))
        randomId <- Traverse[List].sequence(randomNumbers)
      } yield SelectedLobby(randomId.mkString, usernameInput)
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
    } >> props.onLobbySelected(state.value.selectedLobby)

    def onNewLobbyClicked(ev:ReactEvent) = {
      ev.preventDefault()
      state.value.randomLobby.flatMap(props.onLobbySelected)
    }

    <.form(
      <.div(
        <.label("Username"),
        <.input(
          ^.value := state.value.usernameInput,
          ^.onChange ==> usernameChange)
      ),
      <.div(
        <.label("SessionId"),
        <.input(
          ^.value := state.value.sessionIdInput,
          ^.onChange ==> sessionIdChange)
      ),
      <.button(
        "Join",
        ^.onClick ==> onJoinClicked
      ),
      <.button(
        "New Lobby",
        ^.onClick ==> onNewLobbyClicked
      )
    )
  }

  val component = ScalaFnComponent.withHooks[Props]
    .useState(State())
    .render((props, state) => renderFn(props, state))
}
