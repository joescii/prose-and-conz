package com.joescii.pac

import java.text.{DecimalFormat, SimpleDateFormat}
import java.util.{Date, Locale, Calendar, GregorianCalendar}

import com.joescii.pac.snippet.{WordPress, AsciiDoctor}
import net.liftweb.common.Box

import scala.xml.NodeSeq

import net.liftweb.http.{LiftRules, Templates}
import net.liftweb.util.Helpers._

package object model {
  object Post {
    import com.joescii.pac.snippet.WordPress

    val pathPrefixPattern = """(\d{4})-(\d{2})-(\d{2})-"""
    val vintageFilenamePattern = pathPrefixPattern+"(.*)"
    val vintageFilenameRegex = vintageFilenamePattern.r

    def forTitle(title:String) = forPath(List(title))
    def forPath(path:List[String]) = path.headOption.flatMap { filename =>
      val title = vintageFilenameRegex.findFirstMatchIn(filename).map(_.group(4)).getOrElse(filename)
      posts.find(_.title == title)
    }

  }

  trait Post {
    def uid:String
    def tags:List[String]
    def root:String
    def descriptionProp:String
    def published:Date
    def updated:Date
    def filename:String

    def meta(prop:String) = {
      val node = rawHtml.map(s"$prop ^^" #> "noop")
      node.map(_ \ "@content").map(_.toString)
    }

    lazy val shortPath = s"$yearStr-$monthStr-$dayStr-$title"
    lazy val description:String = meta(descriptionProp).openOr("Description/Summary unavailable")

    val regex = Post.vintageFilenameRegex
    lazy val regex(
      yearStr,
      monthStr,
      dayStr,
      title) = filename
    lazy val year = yearStr.toInt
    lazy val month = monthStr.toInt
    lazy val day = dayStr.toInt

    protected lazy val date = {
      val cal = new GregorianCalendar()
      cal.set(Calendar.YEAR, year)
      cal.set(Calendar.MONTH, month - 1)
      cal.set(Calendar.DAY_OF_MONTH, day)
      cal.set(Calendar.HOUR_OF_DAY, 5)
      cal.set(Calendar.MINUTE, 0)
      cal.set(Calendar.SECOND, 0)
      cal.set(Calendar.MILLISECOND, 0)
      cal.getTime
    }
    lazy val url = s"/$yearStr/$monthStr/$dayStr/$title/"
    lazy val fullTitle = {
      val h2s = html.map("h2 ^*" #> "noop")
      val firstH2 = h2s.headOption
      val firstH2Text = firstH2.map(_.text)
      firstH2Text getOrElse title
    }
    lazy val taggedWith = <lift:TaggedWith post={title}><span class="tagged-with">Tagged with:</span> <span class="tags"></span></lift:TaggedWith>
    lazy val rawHtml = Templates.findRawTemplate(root :: shortPath :: Nil, Locale.getDefault)
    lazy val html:NodeSeq = {
      Templates.findRawTemplate("vintage" :: shortPath :: Nil, Locale.getDefault).map(WordPress.render).or(
        Templates.findRawTemplate("modern" :: shortPath :: Nil,
          Locale.getDefault).map(AsciiDoctor.render)
      ).map(_ ++ taggedWith).openOr(<div>Post not found! {this}</div>)
    }
  }

  case class VintagePost(uid:String, filename:String, tags:List[String]) extends Post {
    // Meta stuff
    val root = "vintage"
    val descriptionProp = "property=og:description"
    val timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX")


    val published = meta("property=article:published_time").map(timestamp.parse).openOr(date)
    val updated   = meta("property=article:modified_time").map(timestamp.parse).openOr(published)
  }

  case class ModernPost(uid:String, filename:String) extends Post {
    // Meta stuff
    val root = "modern"
    val descriptionProp = "name=description"
    val timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
    val twoDigits = new DecimalFormat("00")

    val tags:List[String] = meta("name=keywords").map { keywords =>
      keywords.split(',').map(_.trim.toLowerCase.replace(' ', '-')).filter(_.length > 0).toList
    }.openOr(List())
    val published = meta("name=published").map(timestamp.parse).openOr(date)
    val updated   = meta("name=updated").map(timestamp.parse).openOr(published)

    val cal = {
      val c = new GregorianCalendar()
      c.setTime(published)
      c
    }
  }

  val modernPosts = {
    val lines = LiftRules.loadResourceAsString("/posts.csv").toList.flatMap(_.split("""(?s)\s*\n\s*"""))
    lines.map { line =>
      val split = line.split("""\s*,\s*""")
      ModernPost(split(0), split(1))
    }.reverse
  }

  val vintagePosts = Seq(
    VintagePost("acf0ff8d-1838-48cc-8bf2-cbd70110c012", "2014-12-15-scala-the-language-of-agility", List("agility", "grails", "groovy", "liftweb", "scala")),
    VintagePost("41e10994-99cf-4d64-a6fa-35352be8db36", "2014-12-09-the-jvm-bytes-pilot-post", List("bytecode", "java", "tutorial")),
    VintagePost("66c271a7-29d9-4f35-8442-13a101f4ae5e", "2014-11-24-favoring-expressions-over-statements", List("coffeescript", "conferences", "functional-programming", "java", "javascript", "nfjs", "scala")),
    VintagePost("577d9087-4788-478f-b896-19c734139354", "2014-09-24-video-15-minute-chat-with-lift-ng", List("functional-programming", "liftweb", "video", "web-development")),
    VintagePost("8d7f4648-341c-4dea-a9e0-c3f2ebefeb4b", "2014-09-17-why-a-jvm-on-a-vm", List("java", "polyglot", "scala", "web-development")),
    VintagePost("3dbce4dc-a222-425f-baa7-19a793c384e0", "2014-08-11-javascript-is-not-a-functional-language", List("coffeescript", "functional-programming", "haskell", "java", "javascript", "scala")),
    VintagePost("6cf23b90-b8f9-4216-ab55-8d2f72a28efc", "2014-08-04-if-i-were-writing-a-language", List("clojure", "functional-programming", "haskell", "javascript", "liftweb", "polyglot", "purescript", "scala", "static-typing", "web-development")),
    VintagePost("e30adb29-f1fa-49da-acf0-428081154132", "2014-07-28-on-naming", List("functional-programming", "java", "mathematics", "scala", "software-development")),
    VintagePost("8ae75d5f-add1-4905-9b45-b85f5bed7d93", "2014-06-29-uberconf-days-3-4-hits-and-misses", List("agility", "clojure", "cloud", "conferences", "functional-programming", "haskell", "java", "uberconf")),
    VintagePost("22d5c7dd-9e2b-4cfd-895d-e1089ddc6ebb", "2014-06-26-uberconf-day-2-second-expojure-to-clojure", List("clojure", "conferences", "functional-programming", "polyglot", "uberconf")),
    VintagePost("83781748-9d37-4b52-beb9-61cf2a5c4a8b", "2014-06-24-uberconf-day-1-web-application-security-workshop-with-ken-sipe", List("conferences", "uberconf", "web-development", "web-security")),
    VintagePost("accfd760-f06e-4c1a-a636-ed74dd16f5c0", "2014-06-21-picking-uberconf-sessions-sucks", List("agility", "clojure", "conferences", "functional-programming", "java", "javascript", "polyglot", "reactive", "scala", "static-typing", "uberconf", "web-development")),
    VintagePost("252f2453-0ea0-459a-85a6-52f01265641c", "2014-06-16-fear-of-plagiarism-is-killing-collaboration", List("agility")),
    VintagePost("a226891e-12d4-4862-bf0c-b4ec209e26bf", "2014-06-03-type-level-programming-the-subspace-of-scala", List("functional-programming", "scala", "static-typing", "type-programming", "video")),
    VintagePost("a59fd9e2-c89e-4862-aa67-664df6687648", "2014-06-01-this-agile-life-has-gone-country", List("agility", "podcast")),
    VintagePost("cb2008a7-10e7-4988-9f17-3bd13dab4df1", "2014-03-31-scala-may-not-be-right-for-you-if", List("agility", "functional-programming", "scala")),
    VintagePost("80863655-0b05-450c-a2a1-72914904d9b4", "2014-03-24-type-programming-constraining-values-with-equivalent-types", List("functional-programming", "scala", "static-typing", "type-programming")),
    VintagePost("ae120607-7a36-4f60-bbb2-7d7dc8f02c85", "2014-03-17-type-programming-constraining-values", List("functional-programming", "scala", "static-typing", "type-programming")),
    VintagePost("3e51bb75-ca12-4d5e-8d4f-d5b3a42f46a0", "2014-03-10-type-programming-recursive-types", List("functional-programming", "scala", "static-typing", "type-programming")),
    VintagePost("94b24da4-d5cf-49bd-a5ce-15736d2b385e", "2014-02-24-oss-scala-starter-kit", List("giter8", "open-source", "sbt", "scala", "tutorial")),
    VintagePost("7118eaa7-2505-48a9-889e-a607696e621c", "2014-02-20-i-spammed-amos", List("agility", "web-development")),
    VintagePost("809755d8-6bda-451a-b664-abe26020b35a", "2014-02-17-type-programming-shifting-from-values-to-types", List("scala", "snmp4s", "static-typing", "type-programming")),
    VintagePost("03dd796a-65e9-4d37-b423-ec868eb9a6e8", "2014-02-09-akka-and-dependency-injection-child-actors", List("akka", "dependency-injection", "scala", "test-driven-development", "unit-testing")),
    VintagePost("7ff69c0c-c2a0-41cf-b578-51fc6bb8f4c5", "2014-02-03-java-properties-made-elegant-in-scala", List("dsl", "functional-programming", "java", "scala", "static-typing")),
    VintagePost("8d109fff-10d8-4303-aadf-37d47826f295", "2014-01-29-and-then-there-was-code", List("c", "mathematics", "polyglot")),
    VintagePost("e11ecde6-51e4-4109-9ec1-411ba2f3e1d4", "2014-01-27-the-learning-curve-of-scala", List("functional-programming", "java", "scala")),
    VintagePost("d2e41697-4a9e-4027-a24c-62f249dffd8c", "2014-01-20-integrating-dropbox-into-a-lift-app", List("dropbox", "functional-programming", "liftweb", "scala", "tutorial", "web-development")),
    VintagePost("d02b0928-eee8-4516-bca3-a8060015d163", "2014-01-13-i-once-built-a-crappy-actor-library-on-j2ee", List("akka", "concurrency", "j2ee", "java", "reactive", "scala", "soa")),
    VintagePost("f80a3818-3e84-4134-94bd-d5ed35d94509", "2013-12-02-find-a-bandwagon-and-jump-on", List("grails", "groovy", "java", "scala")),
    VintagePost("7dd81d53-6235-4e9a-8d96-36b0f40d29a7", "2013-11-11-deploying-lift-to-cloudbees", List("cloud", "cloudbees", "liftweb", "scala", "tutorial", "web-development")),
    VintagePost("478112cc-153a-4db9-9b9a-f93fa4441fa2", "2013-10-24-or-java-if-youre-not-into-the-whole-brevity-thing", List("functional-programming", "java", "mathematics", "scala")),
    VintagePost("72f6fd91-7fa4-46b8-b14b-d2c8864abe22", "2013-10-12-video-intro-to-functional-programming-in-scala", List("functional-programming", "java", "scala", "software-development", "technology", "video")),
    VintagePost("f95864d4-5665-4afb-8a39-99346f4897d0", "2013-09-23-use-a-computer-to-do-your-testing-too", List("test-driven-development", "unit-testing")),
    VintagePost("a8b6f79b-3081-4664-8885-f308a245bee0", "2013-09-15-scala-imports", List("java", "scala")),
    VintagePost("db242105-2287-45cf-89e6-87b4b7aa8969", "2013-09-09-map-reduce-and-fold-for-the-programmatically-imperative", List("java", "scala")),
    VintagePost("b248cd4a-b12b-4b54-a847-273519c0ebd1", "2013-09-03-five-things-i-miss-most-when-returning-to-java-from-scala", List("java", "scala")),
    VintagePost("1bdbaa00-c8d4-4c0a-b42e-2812741ec1b2", "2013-08-28-a-brush-with-dependency-injection", List("dependency-injection", "java", "spring", "unit-testing")),
    VintagePost("27e08216-8870-4076-9954-d280aeb86e87", "2013-08-13-a-sip-of-coffeescript", List("coffeescript", "huntfunc", "javascript", "polyglot", "scala", "web-development")),
    VintagePost("dd86296b-d81c-43a3-b3d5-bac70a04d610", "2013-08-06-explosive-verbosity-java-and-the-mvp-pattern", List("gwt", "java", "mvp", "scala", "web-development")),
    VintagePost("a2cb655a-c8b3-486e-90dd-d645836797ed", "2013-06-23-static-typing-doesnt-have-to-suck-lambdas", List("functional-programming", "scala", "static-typing")),
    VintagePost("c00ac414-680c-49b3-bdbf-222dd7b423f2", "2013-06-20-its-my-last-day-at-omicron", List()),
    VintagePost("9a26452e-09fb-4c7c-a8f5-eef467908904", "2013-06-19-my-advice-on-resumes", List("resume")),
    VintagePost("bff00311-1859-4a08-81f5-f78b588a6240", "2013-06-18-readonlylist", List("functional-programming", "java", "scala")),
    VintagePost("594ea6c1-27f6-4488-90de-d7546398e33a", "2013-06-15-a-dab-of-recursion-will-do", List("functional-programming", "scala", "snmp4s", "static-typing")),
    VintagePost("583f2ae1-8c2c-46a5-9e85-67fbe114647e", "2013-06-13-is-database-schema-static-typing-for-persistence", List("databases", "static-typing")),
    VintagePost("0cc1f4cc-9673-4898-a9c6-2b6f950a0c6c", "2013-06-06-well-mr-static-typing-this-really-sucks", List("functional-programming", "scala", "snmp4s", "static-typing")),
    VintagePost("bac49bbb-3dcc-4b4a-8ad6-b4fb6580244b", "2013-06-06-its-my-last-day-at-adtran", List()),
    VintagePost("d95532b8-e7b3-4bd4-8837-04d60b6356af", "2013-06-05-optiont-null", List("functional-programming", "scala")),
    VintagePost("e9c7abe5-e0e6-4948-b8aa-5ea3567f5bcf", "2013-05-19-static-typing-doesnt-have-to-suck-structural-typing", List("java", "scala", "static-typing")),
    VintagePost("e32bbf10-adab-4a29-8c5b-5981ed258047", "2013-05-16-pakyow-view-oriented-functional-goodness-for-ruby", List("huntfunc", "liftweb", "pakyow", "ruby", "scala", "web-development")),
    VintagePost("53b59811-d0a4-42a4-b1e6-f3ab4eb44c83", "2013-05-12-static-typing-doesnt-have-to-suck-implicit-conversions", List("java", "javascript", "scala", "static-typing")),
    VintagePost("e2cf2523-eaa2-452e-ade2-11f7577fe0c1", "2013-05-11-yeah-its-a-yin-yang", List()),
    VintagePost("bcbf56cc-24c0-4543-8a0c-073ba2a2d142", "2013-05-08-static-typing-doesnt-have-to-suck-inference", List("scala", "static-typing")),
    VintagePost("f6fc9673-0af1-42a9-90ed-8c3e113870b8", "2013-05-06-next-stop-clojure", List("clojure", "functional-programming", "polyglot", "web-development")),
    VintagePost("1474dba2-bdfa-47d1-a5dc-196c0a2e6937", "2013-05-04-maybe-type-checking-is-a-good-idea", List("scala", "static-typing", "tdd")),
    VintagePost("a4d68418-0a6d-4cf5-83d9-87c608fc4214", "2013-05-03-more-to-come", List()),
    VintagePost("16acb24b-2150-4841-a378-31085c3a0963", "2013-04-29-your-instructors-got-it-wrong", List("software-development")),
    VintagePost("b1f81bee-6630-4530-b9c9-2baef3d43610", "2013-01-26-to-blog-or-to-code", List("blogging", "software-development")),
    VintagePost("8b3e4127-a2e1-4967-a77a-cdf7441fd292", "2013-01-26-noclassdeffounderror-wrong-name", List("java", "liftweb", "osgi", "scala", "scalatra")),
    VintagePost("f046f4bf-8fb7-4551-9ae7-d59a4a50527f", "2012-08-26-i-accidentally-did-something-cool-on-twitter", List()),
    VintagePost("b0e8d6b9-5efc-4a67-9af5-92c9d91a44ba", "2012-07-25-who-produces-and-consumes-your-db", List()),
    VintagePost("c805afb7-086f-43ca-9491-ffaf756a04e4", "2012-01-22-technology-to-make-your-resume-lead-with-the-awesome", List("dropbox", "google-docs", "qr-codes", "resume", "technology", "tutorial")),
    VintagePost("498689fd-1b03-4a4a-afea-c6463290af14", "2012-01-15-so-i-finally-created-a-blog", List("first-blog"))
  )

  val posts:Seq[Post] = modernPosts ++ vintagePosts

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
