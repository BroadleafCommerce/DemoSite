/**
 * Created by ReggieCole on 6/6/17.
 */
$(function() {
     var replaceSearchResultsByQueryURL= function(queryURL) {
         BLC.ajax({
             url: queryURL,
             type: "GET"
         }, function(data) {
             var $search = $('#search');
             var $category = $('#category');

             if ($search.length) {
                 $search.replaceWith(data);
             } else if ($category.length) {
                 $category.replaceWith(data);

                 // Featured products are now overflowing their containers
                 var $featuredProducts = $('.js-dotdotdot');
                 if ($featuredProducts.length) {
                    Global.initialize()
                 }
             }
         });
     };

     $body.on('change', '.js-filter-panel :checkbox', function () {
         var filterBoxes = $('.js-filter-panel :checked');
         var queryString = "?";
         filterBoxes.each(function () {
             var facetValue = $(this).attr('name');
             facetValue = facetValue.replace(' ','+');
             queryString = queryString.concat(facetValue);
             queryString = queryString.concat('&');
         });

         $(this).parents('.js-filter-panel').children('input').each(function () {
             if($(this).attr('name') === 'facetField') {
                 return;
             }

             queryString = queryString.concat($(this).attr('name'));
             queryString = queryString.concat('=');
             queryString = queryString.concat($(this).attr('value'));
             queryString = queryString.concat('&');
             queryString = queryString.replace(/ /g, '+');
         });

         if(queryString.length > 1) {
             queryString = queryString.substr(0, queryString.length - 1);
         }

         var queryURL = (window.location.pathname).concat(queryString);

         history.pushState({}, "", queryURL);

         replaceSearchResultsByQueryURL(queryURL);
    });

     // this handler is used to catch the event when a user clicks back, or forward, while on the search/category page
    $(window).bind('popstate', function() {
        // 0. verify that the #search or #category elements exist on the page, or else this page is not a search results page
        if ( $('#search').length || $('#category').length ) {
            // 1. get the query url for the NEW location, `location`

            var location = window.location;
            var pathname = location.pathname;
            var href = location.href;
            var queryIndex = location.href.indexOf('?');
            var queryUrl = pathname;

            if (queryIndex != -1) {
                href = href.substring(queryIndex);
                queryUrl = queryUrl + href;
            }

            // 2. replace search results using the new query url
            if (queryUrl !== undefined && queryUrl !== null) {
                replaceSearchResultsByQueryURL(queryUrl)
            }
        }
    });
});
