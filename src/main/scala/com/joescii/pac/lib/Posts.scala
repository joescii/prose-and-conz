package com.joescii.pac
package lib

import java.text.DecimalFormat

import model.Post

import java.util.Locale

import com.joescii.pac.snippet.WordPress
import net.liftweb.http.Templates

import scala.xml.NodeSeq

object Posts {
  val twoDigits = new DecimalFormat("00")
  def forPost(post:Post) = {
    import post._
    val m = twoDigits.format(month)
    val d = twoDigits.format(day)

    forPath(List(s"$year-$m-$d-$title"), Locale.getDefault)
  }

  def forPath(path:List[String], locale:Locale) = {
    Templates.findRawTemplate("vintage" :: path, locale).map(WordPress.render).or(
      Templates.findRawTemplate("modern" :: path, locale)
    )
  }
  def surround(ns:NodeSeq) = <lift:surround with="foundation" at="content">{ns}</lift:surround>
}
