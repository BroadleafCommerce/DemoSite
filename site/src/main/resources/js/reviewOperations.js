/* Operations that deal with the reviewing a product */
$(function(){
    // The options used for the review modal
    var modalAccountOptions = {
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
    
    var $communityRating = $('#community-rating');
    var $customerRating = $('.customer-rating');
    var $communityRatingWidget = $communityRating.find('input.star');
    
    var communityRating = $communityRating.data('community-rating') * 4 - 1;
    
    communityRating = Math.round(communityRating); //round to nearest whole number to be compatible with star widgit

    $communityRatingWidget.rating('select', communityRating).rating('disable');
    
    $customerRating.each(function() {
        var customerRating = $(this).data('customer-rating') - 1;
        $(this).find('input.star').rating('select', customerRating).rating('disable');
    });
    
    
    
    // Bind all links with class 'create-review' to open the review modal
    $('body').on('click', 'a.create-review', function() {
        BLC.ajax({url: $(this).attr('href')}, function(data) {
            var extendedOptions = $.extend({
                afterShow: function() {
                    $('.simplemodal-wrap').find('form:first').find('input:first').focus();
                    $('input.star').rating();
                    return true;
                }
            }, modalAccountOptions);
            $.modal(data, extendedOptions);
        });
        return false;
    });
    
    // All form actions will be submitted via AJAX
    $('body').on('click','.simplemodal-wrap input.review_button', function() {
        var $form = $(this).closest("form");
        BLC.ajax({url: $form.attr('action'), 
                type: "POST",
                data: $form.serialize()
            }, function(responseData) {
                $('.simplemodal-wrap').html(responseData);
            }
        );
        return false;
    });     
});