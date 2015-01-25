package com.joescii.pac
package lib

import net.liftweb.http.JavaScriptResponse
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.rest.RestHelper
import net.liftweb.http.S

object LazyLoadedPosts extends RestHelper {
  def responseForPost(id:String) =
    model.posts.find(_.uid == id)
      .map(p => SetHtml(id, p.html))
      .getOrElse(Noop)

  def rightPanel =
    S.runTemplate(List("templates-hidden", "right-panel"))
      .map(html => SetHtml("right-panel", html))
      .getOrElse(Noop)

  serve {
    case "lazy-post" :: id :: Nil Get _ => new JavaScriptResponse(responseForPost(id), List(), List(), 200)
    case "right-panel"     :: Nil Get _ => new JavaScriptResponse(rightPanel, List(), List(), 200)
  }
}
