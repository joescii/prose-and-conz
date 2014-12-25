package bootstrap.liftweb

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

    LiftRules.externalTemplateResolver.default.set(() => (() => {
      case (locale, "blog" :: rest) =>
        Templates.findRawTemplate("vintage" :: rest, locale).map(com.joescii.pac.snippet.WordPress.render).or(
          Templates.findRawTemplate("modern" :: rest, locale).map(template => <lift:surround with="default" at="content">{template}</lift:surround>)
        )
    }))

    LiftRules.statelessRewrite.prepend(NamedPF("BlogRewrite") {
      case RewriteRequest(
      ParsePath(year :: month :: day :: title :: Nil, _, _,_), _, _) =>
        RewriteResponse(
          "blog" :: s"$year-$month-$day-$title" :: Nil
        )
    })
  }

  val adoc = org.asciidoctor.Asciidoctor.Factory.create()
  val parseAdoc:String => Box[NodeSeq] = { in =>
    val html = adoc.convert(in, new java.util.HashMap[String, Object])
    Full(scala.xml.Unparsed(html))
  }
}
