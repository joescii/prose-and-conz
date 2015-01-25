package com.joescii.pac
package snippet

import net.liftweb.common.{Full, Empty, Box}
import net.liftweb.http.{RequestVar, S}
import net.liftweb.util.{PassThru, Props, ClearNodes, ClearClearable}
import net.liftweb.util.Helpers._
import scala.util.Random
import scala.xml.NodeSeq

object CurrentPost extends RequestVar[Box[model.Post]](Empty)

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
    val extractPost = "#content ^^" #> "noop"
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

    (extractPost andThen
      convertReferences andThen
      highlightScala).apply(page)
  }

}

object Posts {
  def render(posts:Seq[model.Post]):NodeSeq = {
    val contents = (posts.head.html ++ <hr></hr><hr></hr><hr></hr>) +: (posts.tail.map(p => <div id={p.uid} class="post-content"></div>))
    val html:NodeSeq = contents.foldRight(NodeSeq.Empty)(_ ++ _)
    val lazyPosts = posts.tail.map("'"+_.uid+"'").mkString(",")  // Array of posts to lazy-load

    html ++
      <img class="spinner" src="images/ajax-loader.gif"></img> ++
      <script>window.lazyPosts=[{lazyPosts}];</script>
  }

  def render(in:NodeSeq):NodeSeq = render(model.posts)
}

object PreviousNext {
  import model.posts

  def render = {
    val index = CurrentPost.map(posts.indexOf)
    val prev = index flatMap (i => if(i+1 < posts.length) Full(posts(i+1)) else Empty)
    val next = index flatMap (i => if(i-1 >= 0) Full(posts(i-1)) else Empty)

    prev.map(post => ".prev [href]" #> post.url & ".prev-title" #> post.fullTitle).openOr(".prev" #> ClearNodes) &
    next.map(post => ".next [href]" #> post.url & ".next-title" #> post.fullTitle).openOr(".next" #> ClearNodes)
  }
}

object Preview {
  import model.{ Post, posts }
  private def snip(post:Post) =
    ".description *" #> post.description &
    ".title *" #> post.fullTitle &
    "href=# [href]" #> post.url

  def latest = snip(posts.head)
  def random = snip(posts(Random.nextInt(posts.length)))
}

object Archive {
  import model.year2month2post
  import java.text.DateFormatSymbols

  def render(in:NodeSeq):NodeSeq = {
    val months = new DateFormatSymbols(S.locale).getMonths
    year2month2post.keySet.toList.sorted.reverse.map { y =>
      <h2>{y}</h2> ++
        year2month2post(y).keySet.toList.sorted.reverse.map{m => <h3>{months(m-1)}</h3> ++ <ul>{
          year2month2post(y)(m).map { p =>
            <li><a href={p.url}>{p.fullTitle}</a></li>
          }
          }</ul>
        }.reduceRight(_ ++ _)
    }.reduceRight(_ ++ <hr></hr> ++ _)
  }
}

object Tags {
  import model.tags

  def render = {
    val count = S.attr("count", _.toInt)
    val anchors = tags.keySet.toList
      .sortWith { case (l, r) => tags(l).length > tags(r).length }
      .map { tag =>
        val count = tags(tag).length
        val href = s"/tag/$tag"
        <a href={href}>{s"$tag ($count)"}</a>
      }
    val trimmed = count.map(anchors.take).openOr(anchors)

    ".tag *" #> trimmed & ClearClearable
  }
}

object TaggedWith {
  import model.tags

  def render = {
    val anchors = S.attr("post", _.toString).flatMap(model.Post.forTitle).flatMap(_.tags
      .sortWith { case (l, r) => tags(l).length > tags(r).length }
      .map { tag =>
        val count = tags(tag).length
        val href = s"/tag/$tag"
        <a href={href}>{s"$tag ($count)"}</a>:NodeSeq
      }
      .reduceRightOption(_ ++ <span>, </span> ++ _)
    )
    anchors.map(".tags *" #> _).openOr(ClearNodes)
  }
}

object Tag {
  def render(in:NodeSeq):NodeSeq = {
    S.param("tag").map { tag =>
      Posts.render(model.tags(tag))
    }.openOr(NodeSeq.Empty)
  }
}

object ProductionOnly {
  def render = Props.mode match {
    case Props.RunModes.Production => PassThru
    case _ => ClearNodes
  }
}

object Disqus {
  def render = CurrentPost.map { post =>
    import post._

    val script = s"""
      var disqus_shortname = 'proseandconz';
      var disqus_identifier = '$uid';
      var disqus_title = '$fullTitle';
      var disqus_url = 'http://prosean.co.nz$url';

      (function() {
      var dsq = document.createElement('script'); dsq.type = 'text/javascript'; dsq.async = true;
      dsq.src = '//' + disqus_shortname + '.disqus.com/embed.js';
      (document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(dsq);
      })();
    """
    <script type="text/javascript">{script}</script><div id="disqus_thread"></div>
  }.openOr(NodeSeq.Empty)
}