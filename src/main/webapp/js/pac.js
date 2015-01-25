/**
 * Created by jbarnes on 1/24/2015.
 */

(function ($) {
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

  /**
   * Adds the next trigger for lazy-loading.  This should be called whenever content is lazily-loaded to
   * get the page ready to fetch the next post.
   * @param id the ID of the last-added post.
   */
  function addNextTrigger(id) {
    if(elementIsInViewport(id)) {
      lazyLoad()
    } else {
      $('#' + id).one('inview', function (event, visible) {
        lazyLoad();
      });
    }
  }

  /**
   * Evaluates whether or not the element with the given ID is currently visible in the viewport.
   * http://stackoverflow.com/questions/123999/how-to-tell-if-a-dom-element-is-visible-in-the-current-viewport/7557433#7557433
   * @param id The element ID to test
   * @returns {boolean} true if the element is in the viewport, false otherwise
   */
  function elementIsInViewport (id) {
    var el = $('#'+id)[0];
    var rect = el.getBoundingClientRect();

    return (
      rect.top >= 0 &&
      rect.left >= 0 &&
      rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && /*or $(window).height() */
      rect.right <= (window.innerWidth || document.documentElement.clientWidth) /*or $(window).width() */
    );
  }

  /**
   * Call when it's time to lazy load the next post.
   */
  function lazyLoad() {
    if(window.lazyPosts && window.lazyPosts.length > 0) {
      var post = window.lazyPosts.shift();
      console.log('Loading '+post);
      httpGet('/lazy-post/'+post,
        function(js){
          // The js from the server will set the HTML for the post in its place
          eval(js);

          // If there are more posts, then throw up a divider.
          if(window.lazyPosts.length > 0) {
            $('#'+post).append('<hr><hr><hr>');
          }

          // Set up a trigger to fetch the next post once this new on is in view.
          addNextTrigger(post);
        },
        function(code){
          console.log("Sorry, couldn't load post "+post+" due to a "+code+" http status code!");
        }
      )
    } else {
      $('.spinner').hide();
    }
  }

  // Call lazyLoad immediately to go ahead and fetch the second post as we render the page
  lazyLoad();
})(jQuery);