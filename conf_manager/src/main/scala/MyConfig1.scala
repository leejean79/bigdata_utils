import com.typesafe.config.{Config, ConfigFactory}


/**
  *
  * 在resources中创建application.conf文件，名称严格一致
  */
object MyConfig1 {

  val config: Config = ConfigFactory.load()
  def main(args: Array[String]): Unit = {

    val url = config.getString("jdbc.basic_tag.url")
    val table = config.getString("jdbc.basic_tag.table")


  }

}
