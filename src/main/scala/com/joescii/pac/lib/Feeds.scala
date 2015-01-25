package com.joescii.pac
package lib

import java.text.SimpleDateFormat

import model.Post

import net.liftweb.http.XmlResponse
import net.liftweb.http.rest.RestHelper

import scala.xml.{Elem, NodeSeq}

object RssFeed extends RestHelper {
  private def feed(items:NodeSeq):Elem =
    <rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
      <channel>
        <title>prose :: and :: conz by joescii</title>
        <link>http://proseand.co.nz</link>
        <description>Code, the Universe and Everything</description>
        <language>en</language>
        <image>
          <title>prose :: and :: conz by joescii</title>
          <link>http://proseand.co.nz</link>
          <url>http://proseand.co.nz/images/pacyang.png</url>
        </image>
        <atom:link href="http://proseand.co.nz/feed" rel="self" type="application/rss+xml" />
        {items}
      </channel>
    </rss>

  private def items(posts:Seq[Post]):NodeSeq = {
    val format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz")

    posts.map { post =>
      <item>
        <title>{post.fullTitle}</title>
        <link>http://proseand.co.nz{post.url}</link>
        <description>{post.description}</description>
        <pubDate>{format.format(post.published)}</pubDate>
        <guid isPermaLink="false">tag:proseand.co.nz,2014-12-29:{post.uid}</guid>
      </item>
    }
  }

  serve {
    case "feed" :: Nil Get _ => new XmlResponse(feed(items(model.posts take 10)), 200, "application/rss+xml", List())
  }
}

object AtomFeed extends RestHelper {
  private def time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")

  private def feed(posts:Seq[Post]):Elem = {
    val latest = posts.map(_.published).sorted.last
      <feed xmlns="http://www.w3.org/2005/Atom">
        <title>prose :: and :: conz by joescii</title>
        <subtitle>Code, the Universe and Everything</subtitle>
        <link href="http://proseand.co.nz"/>
        <id>http://proseand.co.nz/</id>
        <link href="http://proseand.co.nz/atom" rel="self" type="application/rss+xml" />
        <updated>{time.format(latest)}</updated>
        <author>
          <name>Joe Barnes</name>
          <email>barnesjd@gmail.com</email>
        </author>
        {entries(posts)}
      </feed>
  }

  private def entries(posts:Seq[Post]):NodeSeq = {

    posts.map { post =>
      val url = s"http://proseand.co.nz${post.url}"

      <entry>
        <title>{post.fullTitle}</title>
        <link href={url}/>
        <id>tag:proseand.co.nz,2014-12-29:{post.uid}</id>
        <updated>{time.format(post.updated)}</updated>
        <summary>{post.description}</summary>
      </entry>
    }

  }

  serve {
    case "atom" :: Nil Get _ => new XmlResponse(feed(model.posts take 10), 200, "application/atom+xml", List())
  }

}