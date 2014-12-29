package com.joescii.pac
package lib

import java.text.DateFormat

import model.Post

import net.liftweb.http.XmlResponse
import net.liftweb.http.rest.RestHelper

import scala.xml.{Elem, NodeSeq}

object RssFeed extends RestHelper {
  def feed(items:NodeSeq):Elem =
    <xml version="1.0">
      <rss version="2.0">
        <channel>
          <title>prose :: and :: conz by joescii</title>
          <link>http://proseand.co.nz</link>
          <description>prose :: and :: conz by joescii</description>
          <language>en</language>
          <image>
            <title>prose :: and :: conz by joescii</title>
            <link>http://proseand.co.nz</link>
            <url>http://proseand.co.nz/images/pacyang.png</url>
          </image>
          {items}
        </channel>
      </rss>
    </xml>

  def items(posts:Seq[Post]):NodeSeq = {
    val format = DateFormat.getDateInstance

    posts.map { post =>
      <item>
        <title>{post.fullTitle}</title>
        <link>http://proseand.co.nz{post.url}</link>
        <description>blah</description>
        <pubDate>{format.format(post.date)}</pubDate>
      </item>
    }
  }

  serve {
    case "feed" :: Nil Get _ => new XmlResponse(feed(items(model.posts.take(10))), 200, "application/rss+xml", List())
  }
}
