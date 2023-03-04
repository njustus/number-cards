package com.github.njustus.cards.shared.dtos

case class Card(symbol:Card.Symbol,
                color: Card.Color)

object Card {
  sealed trait Color {
    def className: String = this match {
      case RED => "card-red"
      case BLUE => "card-blue"
    }
  }
  case object RED extends Color
  case object BLUE extends Color

  sealed trait Symbol {
    def display: String
  }
  case class Number(n:Int) extends Symbol {
    override def display: String = n.toString
  }
}
