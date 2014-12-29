package com.joescii.pac
package lib

import java.text.SimpleDateFormat

import model.Post

import net.liftweb.http.XmlResponse
import net.liftweb.http.rest.RestHelper

import scala.xml.{Elem, NodeSeq}

object RssFeed extends RestHelper {
  private def feed(items:NodeSeq):Elem =
    <rss version="2.0">
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
        {items}
      </channel>
    </rss>

  private def items(posts:Seq[Post]):NodeSeq = {
    val format = new SimpleDateFormat("dd mm yy hh:mm:ss zzz")

    posts.map { post =>
      <item>
        <title>{post.fullTitle}</title>
        <link>http://proseand.co.nz{post.url}</link>
        <description>{post.description}</description>
        <pubDate>{format.format(post.published)}</pubDate>
      </item>
    }
  }

  serve {
    case "feed" :: Nil Get _ => new XmlResponse(feed(items(model.posts take 10)), 200, "application/rss+xml", List())
  }
}

object AtomFeed extends RestHelper {
  private val time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")

  private def feed(posts:Seq[Post]):Elem = {
    val latest = posts.map(_.date).sorted.last
      <feed xmlns="http://www.w3.org/2005/Atom">
        <title>prose :: and :: conz by joescii</title>
        <subtitle>Code, the Universe and Everything</subtitle>
        <link href="http://proseand.co.nz"/>
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
        <id>{post.uid}</id>
        <updated>{time.format(post.updated)}</updated>
        <summary>{post.description}</summary>
      </entry>
    }

  }

  serve {
    case "atom" :: Nil Get _ => new XmlResponse(feed(model.posts take 10), 200, "application/atom+xml", List())
  }

}