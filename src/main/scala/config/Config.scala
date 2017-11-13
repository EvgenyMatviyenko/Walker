package config

import com.typesafe.config.ConfigFactory

/**
  * Created by evgenymatviyenko on 11/12/17.
  */
object Config {
  private lazy val config = ConfigFactory.load("application.conf")
  private def walkerConfig = config.getConfig("walker")

  def realmlist = walkerConfig.getString("realmlist")
}
