package com.joescii.pac

import java.text.DecimalFormat
import java.util.{Calendar, GregorianCalendar}

package object model {
  case class Post(year:Int, month:Int, day:Int, title:String, tags:List[String]) {
    val twoDigits = new DecimalFormat("00")
    val date = {
      val cal = new GregorianCalendar()
      cal.set(Calendar.YEAR, year)
      cal.set(Calendar.MONTH, month - 1)
      cal.set(Calendar.DAY_OF_MONTH, day)
      cal.getTime
    }
    val yearStr = year.toString
    val monthStr = twoDigits.format(month)
    val dayStr = twoDigits.format(day)
    val url = s"/$yearStr/$monthStr/$dayStr/$title/"
    val shortPath = s"$yearStr-$monthStr-$dayStr-$title"
    val fullTitle = {
      import net.liftweb.util.Helpers._
      val html = lib.Posts.forPost(this)
      val h2s = html.map("h2 ^*" #> "noop")
      val firstH2 = h2s.flatMap(_.headOption)
      val firstH2Text = firstH2.map(_.text)
      firstH2Text openOr title
    }
  }

  object Post{
    val regex = """(\d{4})-(\d{2})-(\d{2})-(.*)""".r
    def apply(filename:String, tags:List[String]):Post = {
      val regex(year, month, day, title) = filename
      Post(year.toInt, month.toInt, day.toInt, title, tags)
    }
  }

  val posts = Seq(
    // Modern
    Post("2014-12-26-article", List()),
    Post("2014-12-24-first-post", List()),

    // Vintage
    Post("2014-12-15-scala-the-language-of-agility", List("agility", "grails", "groovy", "liftweb", "scala")),
    Post("2014-12-09-the-jvm-bytes-pilot-post", List("bytecode", "java", "tutorial")),
    Post("2014-11-24-favoring-expressions-over-statements", List("coffeescript", "conferences", "functional-programming", "java", "javascript", "nfjs", "scala")),
    Post("2014-09-24-video-15-minute-chat-with-lift-ng", List("functional-programming", "liftweb", "video", "web-development")),
    Post("2014-09-17-why-a-jvm-on-a-vm", List("java", "polyglot", "scala", "web-development")),
    Post("2014-08-11-javascript-is-not-a-functional-language", List("coffeescript", "functional-programming", "haskell", "java", "javascript", "scala")),
    Post("2014-08-04-if-i-were-writing-a-language", List("clojure", "functional-programming", "haskell", "javascript", "liftweb", "polyglot", "purescript", "scala", "static-typing", "web-development")),
    Post("2014-07-28-on-naming", List("functional-programming", "java", "mathematics", "scala", "software-development")),
    Post("2014-06-29-uberconf-days-3-4-hits-and-misses", List("agility", "clojure", "cloud", "conferences", "functional-programming", "haskell", "java", "uberconf")),
    Post("2014-06-26-uberconf-day-2-second-expojure-to-clojure", List("clojure", "conferences", "functional-programming", "polyglot", "uberconf")),
    Post("2014-06-24-uberconf-day-1-web-application-security-workshop-with-ken-sipe", List("conferences", "uberconf", "web-development", "web-security")),
    Post("2014-06-21-picking-uberconf-sessions-sucks", List("agility", "clojure", "conferences", "functional-programming", "java", "javascript", "polyglot", "reactive", "scala", "static-typing", "uberconf", "web-development")),
    Post("2014-06-16-fear-of-plagiarism-is-killing-collaboration", List("agility")),
    Post("2014-06-03-type-level-programming-the-subspace-of-scala", List("functional-programming", "scala", "static-typing", "type-programming", "video")),
    Post("2014-06-01-this-agile-life-has-gone-country", List("agility", "podcast")),
    Post("2014-03-31-scala-may-not-be-right-for-you-if", List("agility", "functional-programming", "scala")),
    Post("2014-03-24-type-programming-constraining-values-with-equivalent-types", List("functional-programming", "scala", "static-typing", "type-programming")),
    Post("2014-03-17-type-programming-constraining-values", List("functional-programming", "scala", "static-typing", "type-programming")),
    Post("2014-03-10-type-programming-recursive-types", List("functional-programming", "scala", "static-typing", "type-programming")),
    Post("2014-02-24-oss-scala-starter-kit", List("giter8", "open-source", "sbt", "scala", "tutorial")),
    Post("2014-02-20-i-spammed-amos", List("agility", "web-development")),
    Post("2014-02-17-type-programming-shifting-from-values-to-types", List("scala", "snmp4s", "static-typing", "type-programming")),
    Post("2014-02-09-akka-and-dependency-injection-child-actors", List("akka", "dependency-injection", "scala", "test-driven-development", "unit-testing")),
    Post("2014-02-03-java-properties-made-elegant-in-scala", List("dsl", "functional-programming", "java", "scala", "static-typing")),
    Post("2014-01-29-and-then-there-was-code", List("c", "mathematics", "polyglot")),
    Post("2014-01-27-the-learning-curve-of-scala", List("functional-programming", "java", "scala")),
    Post("2014-01-20-integrating-dropbox-into-a-lift-app", List("dropbox", "functional-programming", "liftweb", "scala", "tutorial", "web-development")),
    Post("2014-01-13-i-once-built-a-crappy-actor-library-on-j2ee", List("akka", "concurrency", "j2ee", "java", "reactive", "scala", "soa")),
    Post("2013-12-02-find-a-bandwagon-and-jump-on", List("grails", "groovy", "java", "scala")),
    Post("2013-11-11-deploying-lift-to-cloudbees", List("cloud", "cloudbees", "liftweb", "scala", "tutorial", "web-development")),
    Post("2013-10-24-or-java-if-youre-not-into-the-whole-brevity-thing", List("functional-programming", "java", "mathematics", "scala")),
    Post("2013-10-12-video-intro-to-functional-programming-in-scala", List("functional-programming", "java", "scala", "software-development", "technology", "video")),
    Post("2013-09-23-use-a-computer-to-do-your-testing-too", List("test-driven-development", "unit-testing")),
    Post("2013-09-15-scala-imports", List("java", "scala")),
    Post("2013-09-09-map-reduce-and-fold-for-the-programmatically-imperative", List("java", "scala")),
    Post("2013-09-03-five-things-i-miss-most-when-returning-to-java-from-scala", List("java", "scala")),
    Post("2013-08-28-a-brush-with-dependency-injection", List("dependency-injection", "java", "spring", "unit-testing")),
    Post("2013-08-13-a-sip-of-coffeescript", List("coffeescript", "huntfunc", "javascript", "polyglot", "scala", "web-development")),
    Post("2013-08-06-explosive-verbosity-java-and-the-mvp-pattern", List("gwt", "java", "mvp", "scala", "web-development")),
    Post("2013-06-23-static-typing-doesnt-have-to-suck-lambdas", List("functional-programming", "scala", "static-typing")),
    Post("2013-06-20-its-my-last-day-at-omicron", List()),
    Post("2013-06-19-my-advice-on-resumes", List("resume")),
    Post("2013-06-18-readonlylist", List("functional-programming", "java", "scala")),
    Post("2013-06-15-a-dab-of-recursion-will-do", List("functional-programming", "scala", "snmp4s", "static-typing")),
    Post("2013-06-13-is-database-schema-static-typing-for-persistence", List("databases", "static-typing")),
    Post("2013-06-06-well-mr-static-typing-this-really-sucks", List("functional-programming", "scala", "snmp4s", "static-typing")),
    Post("2013-06-06-its-my-last-day-at-adtran", List()),
    Post("2013-06-05-optiont-null", List("functional-programming", "scala")),
    Post("2013-05-19-static-typing-doesnt-have-to-suck-structural-typing", List("java", "scala", "static-typing")),
    Post("2013-05-16-pakyow-view-oriented-functional-goodness-for-ruby", List("huntfunc", "liftweb", "pakyow", "ruby", "scala", "web-development")),
    Post("2013-05-12-static-typing-doesnt-have-to-suck-implicit-conversions", List("java", "javascript", "scala", "static-typing")),
    Post("2013-05-11-yeah-its-a-yin-yang", List()),
    Post("2013-05-08-static-typing-doesnt-have-to-suck-inference", List("scala", "static-typing")),
    Post("2013-05-06-next-stop-clojure", List("clojure", "functional-programming", "polyglot", "web-development")),
    Post("2013-05-04-maybe-type-checking-is-a-good-idea", List("scala", "static-typing", "tdd")),
    Post("2013-05-03-more-to-come", List()),
    Post("2013-04-29-your-instructors-got-it-wrong", List("software-development")),
    Post("2013-01-26-to-blog-or-to-code", List("blogging", "software-development")),
    Post("2013-01-26-noclassdeffounderror-wrong-name", List("java", "liftweb", "osgi", "scala", "scalatra")),
    Post("2012-08-26-i-accidentally-did-something-cool-on-twitter", List()),
    Post("2012-07-25-who-produces-and-consumes-your-db", List()),
    Post("2012-01-22-technology-to-make-your-resume-lead-with-the-awesome", List("dropbox", "google-docs", "qr-codes", "resume", "technology", "tutorial")),
    Post("2012-01-15-so-i-finally-created-a-blog", List("first-blog"))
  )

  val year2month2post:Map[Int, Map[Int, Seq[Post]]] = {
    val years = posts.map(_.year).distinct
    val monthByYear = years.map(y =>
      y -> posts.filter(_.year == y).map(_.month).distinct).toMap
    monthByYear.keySet.map { y =>
      y -> monthByYear(y).map { m =>
        m -> posts.filter(p => p.year == y && p.month == m)
      }.toMap
    }.toMap
  }

  val tags:Map[String, Seq[Post]] = {
    val ts = posts.flatMap(_.tags).distinct
    ts.map(tag => tag -> posts.filter(_.tags contains tag)).toMap
  }
}
