package bootstrap.liftweb

import com.joescii.pac.lib.RssFeed
import net.liftweb._
import net.liftweb.common.Box
import net.liftweb.common.Full
import net.liftweb.http.ContentParser
import net.liftweb.http.LiftRules
import scala.xml.NodeSeq
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._
import net.liftmodules.JQueryModule
import net.liftweb.http.js.jquery._
import java.util.ResourceBundle
import java.util
import net.liftweb.util


/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    // where to search snippet
    LiftRules.addToPackages("com.joescii.pac")

    // Build SiteMap
    val entries = List(
      Menu.i("Home") / "index", // the simple way to declare a menu
      Menu.i("Template") / "template",
      Menu.i("Archive") / "archive",
      Menu.i("Tags") / "tags",
      Menu.i("Tag") / "tag",
      Menu(Loc("blog", Link(List("blog"), true, "/blog"), "Blog"))
    )

    // set the sitemap.  Note if you don't want access control for
    // each page, just comment this line out.
    LiftRules.setSiteMap(SiteMap(entries:_*))

    //Show the spinny image when an Ajax call starts
    LiftRules.ajaxStart =
      Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    
    // Make the spinny image go away when it ends
    LiftRules.ajaxEnd =
      Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    // Force the request to be UTF-8
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    // Use HTML5 for rendering
    LiftRules.htmlProperties.default.set((r: Req) =>
      new Html5Properties(r.userAgent))

    //Init the jQuery module, see http://liftweb.net/jquery for more information.
    LiftRules.jsArtifacts = JQueryArtifacts
    JQueryModule.InitParam.JQuery=JQueryModule.JQuery172
    JQueryModule.init()

    LiftRules.contentParsers :+= ContentParser.basic("adoc", parseAdoc)

    blogResolver()
    requestRewrites()
    LiftRules.statelessDispatch.append(RssFeed)
  }

  val adoc = org.asciidoctor.Asciidoctor.Factory.create()
  val parseAdoc:String => Box[NodeSeq] = { in =>
    val html = adoc.convert(in, new java.util.HashMap[String, Object])
    Html5.parse("""<div data-lift="AsciiDoctor">"""+html+"</div>")
  }

  def blogResolver() = {
    import com.joescii.pac.lib.Posts._

    LiftRules.externalTemplateResolver.default.set(() => (() => {
      case (locale, "blog" :: rest) => forPath(rest, locale).map(surround)
    }))
  }

  def requestRewrites() = {
    LiftRules.statelessRewrite.prepend(NamedPF("BlogRewrite") {
      case RewriteRequest(
      ParsePath(year :: month :: day :: title :: Nil, _, _,_), _, _) =>
        RewriteResponse(
          "blog" :: s"$year-$month-$day-$title" :: Nil
        )
      case RewriteRequest(
      ParsePath(year :: month :: day :: title :: "index" :: Nil, _, _,_), _, _) =>
        RewriteResponse(
          "blog" :: s"$year-$month-$day-$title" :: Nil
        )
      case RewriteRequest(
      ParsePath("tags" :: "index" :: Nil, _, _,_), _, _) =>
        RewriteResponse(
          "tags" :: Nil
        )
      case RewriteRequest(
      ParsePath("tag" :: tag :: "index" :: Nil, _, _,_), _, _) =>
        RewriteResponse(
          "tag" :: tag :: Nil
        )
      case RewriteRequest(
      ParsePath("feed" :: "index" :: Nil, _, _,_), _, _) =>
        RewriteResponse(
          "feed" :: Nil
        )
      case RewriteRequest(
      ParsePath("tag" :: tag :: Nil, _, _,_), _, _) =>
        RewriteResponse(ParsePath("tag" :: Nil, "", true, false), Map("tag" -> tag), true)
    })
  }
}
