(function(Global, $, undefined) {

    // Public Functions

    /**
     * Events and actions that trigger on every page load
     */
    Global.initialize = function() {
        // Update the locale that has been selected
        HC.updateLocaleSelection();

        // Update any existing product options
        $('.js-productOptions').each(function(i, el) {
            HC.setExistingProductOptions($(el));
        });

        // Swap in the actual banner ads
        $('.js-rightHandBannerAdsPlaceholder').replaceWith($('.js-rightHandBannerAds').removeClass('is-hidden'));

        // Hide the menu by default on mobile
        if ($(document).width() < 768 ) {
            $('#left-nav').css('height', 0);
        }

        var ellipsis = $('.js-dotdotdot');
        // dotdotdot complains if called on empty jQ selection
        if(ellipsis.length > 0) {
            ellipsis.dotdotdot({ watch: 'true'});
        }

        // Make the main product image zoom-able
        $('.product-card img.js-main-product-img').each(function(i, el) {
            $(el)
                .parent()
                .zoom({magnify: 2, on: "grab"});
        })
    };
})(window.Global = window.Global || {}, jQuery);
