package com.joescii.pac.lib

import net.liftweb.http.XmlResponse
import net.liftweb.http.rest.RestHelper

object RssFeed extends RestHelper {
  serve {
    case "feed" :: Nil Get _ => new XmlResponse(<xml></xml>, 200, "application/xml", List())
  }
}
