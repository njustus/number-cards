package com.github.njustus.cards.lobby

import cats.effect.{IO, SyncIO}
import com.github.njustus.cards.WsClient
import com.github.njustus.cards.lobby.SessionSelector.SelectedSession
import com.github.njustus.cards.shared.dtos.Player
import com.github.njustus.cards.shared.events.{GameEvent, StartGame}
import japgolly.scalajs.react._
import japgolly.scalajs.react.vdom.html_<^._
import monocle.Focus
import org.scalajs.dom.{Location, console, window}
object SessionRoom {
  case class Props(session: SelectedSession) {
    def wsUrl(location: Location):String =
      s"ws://${location.host}/api/ws/${session.sessionId}/${session.username}"
  }

  case class State(players: List[Player],
                    receivedMessages: List[GameEvent])

  case object State {
    def zero = State(List.empty, List.empty)

    def fromProps(props:Props) = addPlayer(Player(props.session.username))(zero)
    def addMessage(msg: GameEvent) =
      Focus[State](_.receivedMessages).modify(xs => msg::xs)

    def addPlayer(player:Player) =
      Focus[State](_.players).modify(xs => player::xs)
  }

  private def renderFn(props: Props,
                       state: Hooks.UseState[State],
                       publishMessage: GameEvent => IO[Unit]): VdomNode = {
    <.div(
      <.h6("Joined Players"),
      <.p(state.value.players.map(_.name).mkString(", ")),
      <.button(
        "Start game",
        ^.onClick --> publishMessage(StartGame)
      ),
      <.div(s"events: ${state.value.receivedMessages}")
    )
  }

  val component = ScalaFnComponent.withHooks[Props]
    .useStateBy(_ => State.zero)
    .useState((s: GameEvent) => IO {console.warn(s"UNUSED publisher: $s")})
    .useEffectOnMountBy { (props, roomStateHook, fnStateHook) =>
      SyncIO {
        val setFnState = fnStateHook.raw._2

        val publishMessage: GameEvent => Unit = WsClient.create[GameEvent, GameEvent](props.wsUrl(window.location)) { ev =>
          val newState = SessionEngine.applyEvent(ev)
          roomStateHook.modState(newState).unsafeRunSync()
        }

        setFnState((ev: GameEvent) => IO { publishMessage(ev) })
      }
    }
    .render { (props, stateHook, fnHook) =>
      renderFn(props, stateHook, fnHook.value)
    }
}
