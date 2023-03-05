package com.github.njustus.cards.shared

import io.circe.generic.extras.Configuration

trait CirceConfig {
  implicit val customConfig: Configuration =
    Configuration.default.withDefaults.withDiscriminator("type")
}

object CirceConfig extends CirceConfig {}
