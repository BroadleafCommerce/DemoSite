var $body = $('body');

/**
 * Handle the click event for product option boxes, and trigger the change
 * product option event
 */
$body.on('click', '.js-productOptionGroup .js-productOption', function() {
    HC.changeProductOption($(this));
    return false;
});

/**
 * Handle the click event for product option select inputs, and trigger the change
 * product option event
 */
$body.on('change', '.js-productOptionGroup select', function() {
    HC.changeProductOption($(this));
    return false;
});

/**
 * Initialize the page
 */
$(function() {
    Global.initialize();
    $(window).scroll(function(){
        if($(document).scrollTop() > 100) {
            $('.navbar').addClass('small-header');
        } else {
            $('.navbar').removeClass('small-header');
        }
    });
});