package com.joescii.pac
package lib

import java.text.DecimalFormat

import model.Post

import java.util.Locale

import com.joescii.pac.snippet.WordPress
import net.liftweb.http.Templates

import scala.xml.NodeSeq

object Posts {
  def forPost(post:Post) = forPath(List(post.shortPath), Locale.getDefault)

  def forPath(path:List[String], locale:Locale) = {
    Templates.findRawTemplate("vintage" :: path, locale).map(WordPress.render).or(
      Templates.findRawTemplate("modern" :: path, locale)
    )
  }
  def surround(ns:NodeSeq) = <lift:surround with="foundation" at="content">{ns}</lift:surround>
}
