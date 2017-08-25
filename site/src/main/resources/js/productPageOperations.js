(function (ProductPage, $) {

    ProductPage.$productMainImg = $('.js-productMainImage .active img').first();

    ProductPage.prevParentSize = 0;
    ProductPage.prevChildSize = 0;
    ProductPage.prevParentMobileSize = 0;
    ProductPage.prevChildMobileSize = 0;

    /**
     * Scrolls the product page thumbnails
     * @param $parent the outer container of the thumbnail list
     * @param $child the inner container (<code>ul</code>) of the thumbnail list
     * @param delta the amount to scroll by (positive to scroll images up, negative to scroll down)
     * @param animationTime {number} [100] the length of time the animation should play
     * @param animation {string} [linear] the type of animation to play
     * @returns {boolean} Whether the event should continue to be bubbled up
     */
    ProductPage.thumbnailScroll = function ($parent, $child, delta, animationTime, animation) {
        //Do nothing if the page doesn't have scrollers
        if($parent.length === 0 || $child.length === 0) {
            return true;
        }

        var isMobile = $parent.hasClass('js-horizontalThumbnails');
        var childSize = isMobile ? $child.width() : $child.height();
        var parentSize = isMobile ? $parent.width() : $parent.height();

        //Do nothing if scrolling is unnecessary
        if (childSize < parentSize) {
            return true;
        }

        var offset = isMobile ? $child.position().left : $child.position().top;

        if (delta >= 0) {
            //Scrolling up
            offset = Math.min(0, offset + delta);
            $parent.toggleClass('thumbnail-bottom-fade', parentSize < childSize);
        }
        if (delta <= 0) {
            //Scrolling down
            if (childSize + offset + delta <= parentSize) {
                offset = parentSize - childSize;
                $parent.toggleClass('thumbnail-bottom-fade', false);
            } else {
                offset += delta;
            }
        }

        if(animationTime === 0) {
            //Perform immediately with no animation (improves performance)
            $child.stop().css(isMobile ? 'left' : 'top', offset + "px");
        } else {
            var animateProperties = isMobile ? {left: offset + "px"} : {top: offset + "px"};

            //Set the defaults if not set already
            animationTime = animationTime || 100;
            animation = animation || "linear";

            $child.stop().animate(animateProperties, animationTime, animation);
        }

        $parent.toggleClass('thumbnail-top-fade', offset !== 0);

        return false;
    };

    /**
     * Scales the thumbnails to match the height of the image
     * @param $parent the outer div of the thumbnails
     */
    ProductPage.scaleThumbnails = function ($parent) {
        $parent.css('height', ProductPage.$productMainImg.height());
    };


    /**
     * Handles adding and removing the scroll buttons and fade-away when resized
     * @param $buttonBack the 'scroll backwards' button - this would be top for vertical, left for horizontal
     * @param $buttonForward the 'scroll forwards' button - this would be bottom for vertical, right for horizontal
     * @param $parent the outer container of the thumbnail list
     * @param $child the inner container (<code>ul</code>) of the thumbnail list
     */
    ProductPage.resizeAdapt = function ($buttonBack, $buttonForward, $parent, $child) {
        var isMobile = $parent.hasClass('js-horizontalThumbnails');
        var childSize = isMobile ? $child.width() : $child.height();
        var parentSize = isMobile ? $parent.width() : $parent.height();

        //Don't do anything if the sizes haven't changed
        if(parentSize === isMobile ? ProductPage.prevParentMobileSize : ProductPage.prevParentSize
            && childSize === isMobile ? ProductPage.prevParentMobileSize : ProductPage.prevChildSize) {
            return;
        }

        if(parentSize < childSize && $buttonBack.hasClass('hidden')) {
            //if scrolling necessary and scrolling elements not there
            $parent.toggleClass('thumbnail-bottom-fade', true);
            $buttonBack.add($buttonForward).toggleClass('hidden', false);
            if(!isMobile) {
                $parent.css('margin-top', '0');
            }
        }  else if(parentSize >= childSize && !$buttonBack.hasClass('hidden')) {
            //scrolling is not necessary but scrolling elements are there
            $parent
                .toggleClass('thumbnail-bottom-fade', false)
                .toggleClass('thumbnail-top-fade', false);
            $buttonBack.add($buttonForward).toggleClass('hidden', true);
            if(!isMobile) {
                $parent.css('margin-top', '');
                $child.css('top', '0');
            } else {
                $child.css('left', '0');
            }
        }
    }

})(window.ProductPage = window.ProductPage || {}, jQuery);