package com.joescii.pac
package lib

import net.liftweb.http.JavaScriptResponse
import net.liftweb.http.js.JE.JsRaw
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.rest.RestHelper

object LazyLoadedPosts extends RestHelper {
  import model.posts
  def responseForPost(id:String) = {
    val post =
      posts.find(_.uid == id)
      .map { p =>
        SetHtml(id, p.html)
      }

    post getOrElse Noop
  }

  serve {
    case "lazy-post" :: id :: Nil Get _ => new JavaScriptResponse(responseForPost(id), List(), List(), 200)
  }
}
