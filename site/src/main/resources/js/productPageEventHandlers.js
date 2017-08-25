(function (ProductPageHandler, $) {
    //These are saved for the sake of performance
    ProductPageHandler.$parent = $('.js-verticalThumbnails');
    ProductPageHandler.$child = ProductPageHandler.$parent.find('.js-verticalThumbnailsList');
    ProductPageHandler.$parentMobile = $('.js-horizontalThumbnails');
    ProductPageHandler.$childMobile = ProductPageHandler.$parentMobile.find('.js-horizontalThumbnailsList');
    ProductPageHandler.$bothParents = $('.js-verticalThumbnails, .js-horizontalThumbnails');
    ProductPageHandler.$buttonLeft = $('.js-thumbnailLeft');
    ProductPageHandler.$buttonRight = $('.js-thumbnailRight');
    ProductPageHandler.$buttonUp = $('.js-thumbnailUp');
    ProductPageHandler.$buttonDown = $('.js-thumbnailDown');

    /**
     * Forces the thumbnails to recalculate their size and display/hide scrolls as necessary
     */
    ProductPageHandler.handleReadyOrResize = function () {
        ProductPage.scaleThumbnails(ProductPageHandler.$parent);

        ProductPage.resizeAdapt(
            ProductPageHandler.$buttonLeft,
            ProductPageHandler.$buttonRight,
            ProductPageHandler.$parentMobile,
            ProductPageHandler.$childMobile);

        ProductPage.resizeAdapt(
            ProductPageHandler.$buttonUp,
            ProductPageHandler.$buttonDown,
            ProductPageHandler.$parent,
            ProductPageHandler.$child);

        //Trigger a scroll to make sure thumbnails are fully in view
        ProductPage.thumbnailScroll(ProductPageHandler.$parent, ProductPageHandler.$child, 0, 100, "linear");
        ProductPage.thumbnailScroll(ProductPageHandler.$parentMobile, ProductPageHandler.$childMobile, 0, 100, "linear");
    }
})(window.ProductPageHandler = window.ProductPageHandler || {}, jQuery);


/**
 * Listens for mouse scroll events on product thumbnails to emulate scrolling
 */
ProductPageHandler.$bothParents.bind('mousewheel DOMMouseScroll', function (e) {
    var delta = (e.originalEvent.wheelDelta || -e.originalEvent.detail);

    var $parent = $(this);
    var $child = $parent.find('.js-verticalThumbnailsList, .js-horizontalThumbnailsList');

    return ProductPage.thumbnailScroll($parent, $child, delta * 20, 100, "linear");
});

/**
 * Tracks starting position for touch drag on thumbnails
 */
ProductPageHandler.$bothParents.bind('touchstart', function (e) {
    ProductPageHandler.xPos = e.originalEvent.touches[0].pageX;
    ProductPageHandler.yPos = e.originalEvent.touches[0].pageY;
});

/**
 * Tracks drags on product thumbnails to scroll
 */
ProductPageHandler.$bothParents.bind('touchmove', function (e) {
    var x = e.originalEvent.touches[0].pageX;
    var y = e.originalEvent.touches[0].pageY;

    var isMobile = $(this).hasClass('js-horizontalThumbnails');
    var delta = isMobile ? x - ProductPageHandler.xPos : y - ProductPageHandler.yPos;
    var $parent = isMobile ? ProductPageHandler.$parentMobile : ProductPageHandler.$parent;
    var $child = isMobile ? ProductPageHandler.$childMobile : ProductPageHandler.$child;

    var result = ProductPage.thumbnailScroll($parent, $child, delta, 0, "linear");

    ProductPageHandler.xPos = x;
    ProductPageHandler.yPos = y;
    return result;
});

/**
 * Scrolls thumbnails up when button is pressed
 */
$('.js-thumbnailUp, .js-thumbnailLeft').click(function () {
    var isMobile = $(this).hasClass('js-thumbnailLeft');
    var $parent = isMobile ? ProductPageHandler.$parentMobile : ProductPageHandler.$parent;
    var $child = isMobile ? ProductPageHandler.$childMobile : ProductPageHandler.$child;

    return ProductPage.thumbnailScroll($parent, $child, 200, 500, "swing");
});

/**
 * Scrolls thumbnails down when button is pressed
 */
$('.js-thumbnailDown, .js-thumbnailRight').click(function () {
    var isMobile = $(this).hasClass('js-thumbnailRight');
    var $parent = isMobile ? ProductPageHandler.$parentMobile : ProductPageHandler.$parent;
    var $child = isMobile ? ProductPageHandler.$childMobile : ProductPageHandler.$child;

    return ProductPage.thumbnailScroll($parent, $child, -200, 500, "swing");
});

/**
 * Prepares the product page thumbnails
 */
$(document).ready(ProductPageHandler.handleReadyOrResize);

/**
 * Listens for resizes to rescale the thumbnail containers
 */
$(window).resize(ProductPageHandler.handleReadyOrResize);
