package com.joescii.pac
package lib

import net.liftweb.http.JavaScriptResponse
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.rest.RestHelper

object LazyLoadedPosts extends RestHelper {
  def responseForPost(id:String) =
    model.posts.find(_.uid == id)
      .map(p => SetHtml(id, p.html))
      .getOrElse(Noop)

  serve {
    case "lazy-post" :: id :: Nil Get _ => new JavaScriptResponse(responseForPost(id), List(), List(), 200)
  }
}
