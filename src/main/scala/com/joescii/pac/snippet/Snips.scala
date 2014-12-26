package com.joescii.pac
package snippet

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
    val surround:NodeSeq => NodeSeq = { ns => <lift:surround with="foundation" at="content">{ns}</lift:surround> }

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

    surround(post ++ commentHeader ++ comments)
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
    val surround:NodeSeq => NodeSeq = { ns => <lift:surround with="foundation" at="content">{ns}</lift:surround> }
    val highlightScala = ".listingblock *" #> { "pre [class+]" #> "brush: scala; title: ; notranslate" }

    (surround andThen
      highlightScala).apply(page)
  }

}