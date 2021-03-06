import com.redis.RedisClient
import com.typesafe.config.{Config, ConfigFactory}
import net.ceedubs.ficus.Ficus._
import net.ceedubs.ficus.FicusConfig
import net.ceedubs.ficus.readers.ValueReader

import scala.util.matching.Regex

package object params {
  val applicationConf: Config = ConfigFactory.load()
  lazy implicit val config: ApiParams = applicationConf.as[ApiParams]("app")

  case class RedisParams(host: String, port: Int)

  case class ApiParams(userName: String,
                       token: String,
                       channelsToSubscribers: RedisClient,
                       subscribersToChannels: RedisClient)

  implicit lazy val apiParamsReader: ValueReader[ApiParams] =
    (config: Config, path: String) => {
      val subConfig = config.as[FicusConfig](path)

      ApiParams(
        userName = subConfig.as[String]("username"),
        token = subConfig.as[String]("token"),
        channelsToSubscribers = new RedisClient(
          host = subConfig.as[String]("channelsToSubscribers.host"),
          port = subConfig.as[Int]("channelsToSubscribers.port")
        ),
        subscribersToChannels = new RedisClient(
          host = subConfig.as[String]("subscribersToChannels.host"),
          port = subConfig.as[Int]("subscribersToChannels.port")
        )
      )
    }
}

package object regex {
  val addSubscribeCommand: Regex = "/add\\shttps://t.me/([a-zA-Z0-9-_]+)".r
  val deleteChannelCommand: Regex = "/delete\\shttps://t.me/([a-zA-Z0-9-_]+)".r
  val deleteAllSubscribesCommand: Regex = "/deleteall".r
  val showSubscribesCommand: Regex = "/show".r
  val infoCommand: Regex = "info".r
}