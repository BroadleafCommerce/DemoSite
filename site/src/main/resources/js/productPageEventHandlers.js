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
$(document).ready(function () {
    //Force a resize to display scroll elements if necessary
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
});

/**
 * Listens for resizes to rescale the thumbnail containers
 */
$(window).resize(function () {
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

    ProductPage.scaleThumbnails(ProductPageHandler.$parent);

    //Trigger a scroll to make sure thumbnails are fully in view
    ProductPage.thumbnailScroll(ProductPageHandler.$parent, ProductPageHandler.$child, 0, 100, "linear");
    ProductPage.thumbnailScroll(ProductPageHandler.$parentMobile, ProductPageHandler.$childMobile, 0, 100, "linear");
});

/**
 * Changes the shown image when a thumbnail is selected in the product zoom modal
 */
$('body').on('click', '.js-thumbnail', function () {
    //Swap images
    var $img = $(this).find('img');
    var $primaryImgs = $('.js-primary-img img, .zoomImg');

    $primaryImgs.attr('alt', $img.attr('alt'));
    $primaryImgs.attr('src', $img.attr('src'));

    $('.js-thumbnail').toggleClass('active', false);
    $(this).toggleClass('active', true);

    return false;
});

/**
 * Opens and sizes the product zoom modal when image is clicked
 */
$('body').on('click', '.js-openZoomGallery', function () {
    var $modal = $('#img-gallery-modal');

    //Do nothing on mobile
    if ($(document).width() <= 768) {
        return false;
    }

    //Show modal
    $modal.modal('show');

    //Set image to currently selected
    var src = $(this).find("img").attr('src');
    $('.js-primary-img img, .zoomImg')
        .attr('src', src)
        .attr('alt', $(this).find("img").attr('alt'));
    $('.js-thumbnails').find("img").each(function() {
        if($(this).attr('src') === src) {
            $('.js-thumbnails .active').toggleClass('active', false);
            $(this).parent().addClass('active');
        }
    });

    //Don't recalculate width if already opened
    if ($modal.hasClass('js-opened')) {
        return false;
    } else {
        $modal.toggleClass('js-opened');
    }

    //Delay, otherwise jQuery.zoom starts with wrong image size
    setTimeout(function () {

        //Match thumbnails height to image height
        var preZoomScale = $(".js-primary-img img").width() / document.querySelector('.js-primary-img img').naturalWidth;
        $(".js-primary-img")
            .wrap('<span style="display:inline-block; width: 100%"></span>')
            .css('display', 'block')
            .parent()
            .zoom({magnify: preZoomScale * 1.75, on: "grab"});
    }, 500);



    return false;
});

/**
 * Close product zoom modal if screen becomes too small
 */
$(window).resize(function() {
    var $modal = $('#img-gallery-modal');
    if ($(window).width() <= 768 && $modal.css('display') === 'block') {
        $modal.modal('hide');
    }
});

/**
 * Populate product zoom modal thumbnails.
 * The template and JS is set up this way because it would be difficult
 * to keep the primary image the first while keeping the bootstrap carousel
 * format with the limitations of Thymeleaf.
 */
$(document).ready(function() {
    var $thumbnail = $('.js-thumbnail');
    $('.js-media img').each(function(i) {
        $($thumbnail[i]).find('img')
            .attr('src', $(this).attr('src'))
            .attr('alt', $(this).attr('alt'));
    });

    $('.js-media').remove();
});