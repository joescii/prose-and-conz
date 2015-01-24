/**
 * Created by jbarnes on 1/24/2015.
 */

(function ($) {
  //console.log(window.lazyPosts);
  var httpGet = function (url, success, fail) {
    var request = new XMLHttpRequest();
    request.onreadystatechange = function() {
      if (request.readyState === 4) {
        if (request.status === 200) {
          success(request.responseText);
        } else {
          if(fail instanceof Function) fail(request.status);
        }
      }
    };
    request.open("GET", url , true);
    request.send(null);
  };

  window.addPost = function(id) {
    $('#'+id).one('inview', function (event, visible) {
      if (visible == true) {
        lazyLoad();
      }
    });

  };

  /**
   * Call when it's time to lazy load the next post.
   */
  var lazyLoad = function () {
    if(window.lazyPosts && window.lazyPosts.length > 0) {
      var post = window.lazyPosts.shift();
      console.log('Loading '+post);
      httpGet('/lazy-post/'+post,
        function(js){
          eval(js);
        },
        function(code){
          console.log("Sorry, couldn't load post "+post+" due to a "+code+" http status code!");
        }
      )
    } else {
      $('.spinner').hide();
    }
  };

  // Call lazyLoad immediately to go ahead and fetch the second post as we render the page
  lazyLoad();
})(jQuery);