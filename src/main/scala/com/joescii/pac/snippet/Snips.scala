package com.joescii.pac
package snippet

import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

object WordPress {
  import net.liftweb.util.ClearNodes

  def render(page:NodeSeq):NodeSeq = {
    val extractPost = ".post ^^" #> "noop"
    val extractComments = ".commentlist ^^" #> "noop"
    val cleanPostMeta = ".post-meta *" #> {
      ".byline" #> ClearNodes &
        ".author" #> ClearNodes &
        ".comments-link" #> ClearNodes
    }
    val stripAds  = "#wordads-preview-parent" #> ClearNodes
    val stripShareLikeRelated = "#jp-post-flair" #> ClearNodes
    val stripPostData = ".post-data" #> ClearNodes
    val stripPostEdit = ".post-edit" #> ClearNodes
    val stripNavigation = ".navigation" #> ClearNodes
    val surround:NodeSeq => NodeSeq = { ns => <lift:surround with="foundation" at="content">{ns}</lift:surround> }

    val post = (extractPost &
      cleanPostMeta &
      stripAds &
      stripShareLikeRelated &
      stripPostData &
      stripPostEdit &
      stripNavigation).apply(page)

    val commentHeader = <hr></hr> ++ <h5 class="olde">Olde Comments</h5>
    val comments = extractComments.apply(page)

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
