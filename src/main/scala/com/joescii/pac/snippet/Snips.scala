package com.joescii.pac
package snippet

import net.liftweb.http.S
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

object WordPress {
  import net.liftweb.util.ClearNodes

  def render(page:NodeSeq):NodeSeq = {
    val extractPost = ".post ^^" #> "noop"
    val cleanPostMeta = ".post-meta *" #> {
      ".byline" #> ClearNodes &
        ".author" #> ClearNodes &
        ".comments-link" #> ClearNodes
    }
    val extractTitle = ".post-title ^*" #> "noop"
    val insertTitle = ".post-title" #> <h2 class="post-title">{extractTitle(page)}</h2>
    val stripAds  = "#wordads-preview-parent" #> ClearNodes
    val stripShareLikeRelated = "#jp-post-flair" #> ClearNodes
    val stripPostData = ".post-data" #> ClearNodes
    val stripPostEdit = ".post-edit" #> ClearNodes
    val stripNavigation = ".navigation" #> ClearNodes

    val post = (extractPost &
      cleanPostMeta &
      insertTitle &
      stripAds &
      stripShareLikeRelated &
      stripPostData &
      stripPostEdit &
      stripNavigation).apply(page)

    val extractComments = ".commentlist ^^" #> "noop"
    val stripReplies = ".reply" #> ClearNodes
    val stripEdits = ".comment-edit-link" #> ClearNodes

    val commentHeader = <hr></hr> ++ <h5 class="olde">Olde Comments</h5>
    val comments = (extractComments &
      stripReplies &
      stripEdits).apply(page)

    if(comments.isEmpty) post else post ++ commentHeader ++ comments
  }
}

object Copyright {
  import java.util.{GregorianCalendar, Calendar}
  def year = new GregorianCalendar().get(Calendar.YEAR)
  def copyright(sym:String) = s"Joe Barnes. Copyright $sym 2012 - $year"
  def meta = <meta name="copyright" content={copyright("(c)")}/>
  def footer = "p *" #> copyright("©")
}

object AsciiDoctor {
  def render(page:NodeSeq):NodeSeq = {
    val liftCode:NodeSeq => NodeSeq = { ns =>
      val code = (".language-scala ^*" #> "noop").apply(ns)
      ("pre *" #> code).apply(ns)
    }
    val convertReferences = ".conum" #> { ns:NodeSeq =>
      val ref = ns.text
      s"// See $ref below"
    }
    val highlightScala = ".listingblock *" #> {
      liftCode andThen
      "pre [class+]" #> "brush: scala; title: ; notranslate"
    }

    (convertReferences andThen
      highlightScala).apply(page)
  }

}

object Posts {
  def render(in:NodeSeq):NodeSeq = {
    import lib.Posts._
    val count = S.attr("count", _.toInt).openOr(5)
    model.posts.take(count).flatMap(forPost).reduceRight(_ ++ <hr></hr><hr></hr><hr></hr> ++ _)
  }
}