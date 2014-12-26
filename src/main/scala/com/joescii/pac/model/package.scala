package com.joescii.pac

package object model {
  case class Post(year:Int, month:Int, day:Int, title:String)
  object Post{
    val regex = """(\d{4})-(\d{2})-(\d{2})-(.*)""".r
    def apply(filename:String):Post = {
      val regex(year, month, day, title) = filename
      Post(year.toInt, month.toInt, day.toInt, title)
    }
  }
  val posts = Seq(
    Post("2014-12-26-article"),
    Post("2014-12-24-first-post"),
    Post("2014-12-15-scala-the-language-of-agility"),
    Post("2014-12-09-the-jvm-bytes-pilot-post"),
    Post("2014-11-24-favoring-expressions-over-statements")
  )
}
