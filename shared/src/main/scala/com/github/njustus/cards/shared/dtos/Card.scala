package com.github.njustus.cards.shared.dtos

case class Card(symbol:Card.Symbol,
                color: Card.Color)

object Card {
  val RED: Color = Color("red")
  val BLUE: Color = Color("blue")
  val YELLOW = Color("yellow")
  val GREEN = Color("green")

  def availableCards: Seq[Card] =
    for {
      color <- Seq(RED, BLUE, YELLOW, GREEN)
      number <- (1 to 12)
    }  yield Card(Symbol(number), color)

  case class Color(value: String) extends AnyVal {
    def className: String = s"card-$value"
  }

  case class Symbol(value: String) extends AnyVal {
    def display: String = value
  }

  object Symbol {
    def apply(n: Int): Symbol = Symbol(n.toString)
  }
}
