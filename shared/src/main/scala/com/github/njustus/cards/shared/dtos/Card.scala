package com.github.njustus.cards.shared.dtos

case class Card(symbol:Card.Symbol,
                color: Card.Color)

object Card {
  case class Color(value: String) extends AnyVal {
    def className: String = s"card-$value"
  }

  val RED: Color = Color("red")
  val BLUE: Color = Color("blue")

  case class Symbol(value: String) extends AnyVal {
    def display: String = value
  }

  object Symbol {
    def apply(n: Int): Symbol = Symbol(n.toString)
  }
}
