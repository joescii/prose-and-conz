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
    Post("2014-11-24-favoring-expressions-over-statements"),
    Post("2014-09-24-video-15-minute-chat-with-lift-ng"),
    Post("2014-09-17-why-a-jvm-on-a-vm"),
    Post("2014-08-11-javascript-is-not-a-functional-language"),
    Post("2014-08-04-if-i-were-writing-a-language"),
    Post("2014-07-28-on-naming"),
    Post("2014-06-29-uberconf-days-3-4-hits-and-misses"),
    Post("2014-06-26-uberconf-day-2-second-expojure-to-clojure"),
    Post("2014-06-24-uberconf-day-1-web-application-security-workshop-with-ken-sipe"),
    Post("2014-06-21-picking-uberconf-sessions-sucks"),
    Post("2014-06-16-fear-of-plagiarism-is-killing-collaboration"),
    Post("2014-06-03-type-level-programming-the-subspace-of-scala"),
    Post("2014-06-01-this-agile-life-has-gone-country"),
    Post("2014-03-31-scala-may-not-be-right-for-you-if"),
    Post("2014-03-24-type-programming-constraining-values-with-equivalent-types"),
    Post("2014-03-17-type-programming-constraining-values"),
    Post("2014-03-10-type-programming-recursive-types"),
    Post("2014-02-24-oss-scala-starter-kit"),
    Post("2014-02-20-i-spammed-amos"),
    Post("2014-02-17-type-programming-shifting-from-values-to-types"),
    Post("2014-02-09-akka-and-dependency-injection-child-actors"),
    Post("2014-02-03-java-properties-made-elegant-in-scala"),
    Post("2014-01-29-and-then-there-was-code"),
    Post("2014-01-27-the-learning-curve-of-scala"),
    Post("2014-01-20-integrating-dropbox-into-a-lift-app"),
    Post("2014-01-13-i-once-built-a-crappy-actor-library-on-j2ee"),
    Post("2013-12-02-find-a-bandwagon-and-jump-on"),
    Post("2013-11-11-deploying-lift-to-cloudbees"),
    Post("2013-10-24-or-java-if-youre-not-into-the-whole-brevity-thing"),
    Post("2013-10-12-video-intro-to-functional-programming-in-scala"),
    Post("2013-09-23-use-a-computer-to-do-your-testing-too"),
    Post("2013-09-15-scala-imports"),
    Post("2013-09-09-map-reduce-and-fold-for-the-programmatically-imperative"),
    Post("2013-09-03-five-things-i-miss-most-when-returning-to-java-from-scala"),
    Post("2013-08-28-a-brush-with-dependency-injection"),
    Post("2013-08-13-a-sip-of-coffeescript"),
    Post("2013-08-06-explosive-verbosity-java-and-the-mvp-pattern"),
    Post("2013-06-23-static-typing-doesnt-have-to-suck-lambdas"),
    Post("2013-06-20-its-my-last-day-at-omicron"),
    Post("2013-06-19-my-advice-on-resumes"),
    Post("2013-06-18-readonlylist"),
    Post("2013-06-13-is-database-schema-static-typing-for-persistence"),
    Post("2013-06-06-well-mr-static-typing-this-really-sucks"),
    Post("2013-06-06-its-my-last-day-at-adtran"),
    Post("2013-06-05-optiont-null"),
    Post("2013-05-19-static-typing-doesnt-have-to-suck-structural-typing"),
    Post("2013-05-16-pakyow-view-oriented-functional-goodness-for-ruby"),
    Post("2013-05-12-static-typing-doesnt-have-to-suck-implicit-conversions"),
    Post("2013-05-11-yeah-its-a-yin-yang"),
    Post("2013-05-08-static-typing-doesnt-have-to-suck-inference"),
    Post("2013-05-06-next-stop-clojure"),
    Post("2013-05-04-maybe-type-checking-is-a-good-idea"),
    Post("2013-05-03-more-to-come"),
    Post("2013-04-29-your-instructors-got-it-wrong"),
    Post("2013-01-26-to-blog-or-to-code"),
    Post("2013-01-26-noclassdeffounderror-wrong-name"),
    Post("2012-08-26-i-accidentally-did-something-cool-on-twitter"),
    Post("2012-07-25-who-produces-and-consumes-your-db"),
    Post("2012-01-22-technology-to-make-your-resume-lead-with-the-awesome"),
    Post("2012-01-15-so-i-finally-created-a-blog")
  )
}
