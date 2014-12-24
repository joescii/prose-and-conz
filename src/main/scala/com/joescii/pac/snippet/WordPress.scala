package com.joescii.pac
package snippet

import net.liftweb.util.Helpers._

import scala.xml.NodeSeq

object WordPress {
  def render(post:NodeSeq):NodeSeq = {
    val extractor = ".post ^^" #> "noop"
    <lift:surround with="default" at="content">{extractor(post)}</lift:surround>
  }
}
