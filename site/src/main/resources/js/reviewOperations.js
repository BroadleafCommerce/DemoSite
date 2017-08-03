/**
 * Operations that deal with the reviewing a product, leveraging the jQuery Star Rating Plugin v4.11.
 */
(function(Review, $, undefined) {

    // Public Properties

    /**
     * The options used for the review modal
     */
    Review.modalAccountOptions = {
        maxWidth    : 600,
        maxHeight   : 560,  
        fitToView   : false,
        width       : '100%',
        height      : '100%',
        autoSize    : true,
        closeClick  : false,
        topRatio    : 0,
        openEffect  : 'none',
        closeEffect : 'none'
    };

    // Public Methods

    /**
     * This will initialize all review/rating sections leveraging the jQuery Star Rating Plugin v4.11.
     */
    Review.initialize = function() {
        var $communityRating = $('#community-rating');
        var $customerRating = $('.customer-rating');
        var $communityRatingWidget = $communityRating.find('.star');

        var communityRating = $communityRating.data('community-rating') * 4 - 1;

        communityRating = Math.round(communityRating); //round to nearest whole number to be compatible with star widgit

        $communityRatingWidget.rating('select', communityRating).rating('disable');

        $customerRating.each(function() {
            var customerRating = $(this).data('customer-rating') - 1;
            $(this).find('input.star').rating('select', customerRating).rating('disable');
        });

    };

})(window.Review = window.Review || {}, jQuery);