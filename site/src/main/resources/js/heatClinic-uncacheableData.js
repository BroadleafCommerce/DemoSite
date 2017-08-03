(function(HCUncacheableData, $, undefined) {

    // Public Methods

    /**
     * Function to update uncacheable data like cart quantity, user first name, in-cart indicators, and inventory indicators
     * @param {object} params - data used to update all uncacheable data
     */
    HCUncacheableData.updateUncacheableData = function(params) {
        HCUncacheableData.updateCustomerData(params);
        HCUncacheableData.updateOutOfStockData(params);
        HCUncacheableData.updateInCartData(params);
        HCUncacheableData.updateXSRFToken(params);
    };

    /**
     * Updates the XSRF Token
     * @param {object} params - data used to update the XSRF Token
     */
    HCUncacheableData.updateXSRFToken = function(params) {
        $('input[name="csrfToken"]').val(params['csrfToken']);
    };

    /**
     * Updates the Customer's first name display, if logged in
     * @param {object} params - data used to update the Customer name display
     */
    HCUncacheableData.updateCustomerData = function(params) {
        var anonymous = params['anonymous'];
        if (anonymous) {
            $('#anonymous-customer-header').removeClass('is-hidden');
            $('#registered-customer-header').addClass('is-hidden');
        } else {
            $('#registered-customer-header').removeClass('is-hidden');
            $('#anonymous-customer-header').addClass('is-hidden');
            $('#welcome-first-name').text(params['firstName']);
        }
    };

    /**
     * Updates stock/inventory indicators for various Products on the page
     * @param {object} params - data used to update all stock/inventory indicators
     */
    HCUncacheableData.updateOutOfStockData = function(params) {
        var outOfStockProducts = params['outOfStockProducts'];
        for (var i=0; i < outOfStockProducts.length; i++) {
            var elements = $('[name$="productId"], .product-quickview').filter(function(idx, e) { 
                var $element = $(e);
                var id = $element.data('id');
                
                if (id === undefined) {
                    return $element.val() == outOfStockProducts[i];
                } else  {
                    return id == outOfStockProducts[i];
                }
            });
            
            $(elements).each(function(idx, el) {
                var $containerElement = $(el).closest('.js-configureRow');
                if ($containerElement.length) {
                    $containerElement.addClass('outOfStock');
                    $containerElement.find('.js-outOfStock').removeClass('is-hidden');

                    var $quantityInput = $containerElement.find('.js-quantityInput');
                    $quantityInput.val(0);
                    $quantityInput.parent().hide();
                } else {
                    $containerElement = $(el).closest('.js-productContainer:not(.js-productConfigure)');
                    $containerElement.find('.js-outOfStockDisable')
                        .attr('data-original-title', $containerElement.find('input[name=outOfStockText]').val())
                        .addClass('js-outOfStockProduct')
                        .addClass('wishlist-not-ready');
                    $containerElement.find('.js-image').addClass('is-out-of-stock');
                    $containerElement.find('.js-outOfStock').removeClass('is-hidden');
                    $containerElement.find('.js-addToCartContainer').addClass('is-hidden');
                    $containerElement.find('.js-addToWishlistContainer').addClass('is-hidden');
                }
            });
        }
    };

    /**
     * Updates cart indicators based on the current state of the cart
     * @param {object} params - data used to update the cart indicators
     */
    HCUncacheableData.updateInCartData = function(params) {
        var cartItemIds = params['cartItemIdsWithoutOptions'];
        for (var i = 0; i < cartItemIds.length; i++) {
            var containerElement = $('.js-productActions' + cartItemIds[i]);

            // If this is an update request, we don't want to hide the add to cart button
            if (containerElement.find('.js-addToCart.js-updateRequest').length) {
                continue;
            }

            containerElement.find('.js-inCartLinkContainer').removeClass('is-hidden');
            containerElement.find('.js-addToCartContainer').addClass('is-hidden');
            containerElement.find('.js-outOfStock').addClass('is-hidden');
        }
    };

})(window.HCUncacheableData = window.HCUncacheableData || {}, jQuery);

/**
 * This is called by the `UncacheableDataProcessor`
 * @param params
 */
function updateUncacheableData(params) {
    HCUncacheableData.updateUncacheableData(params);
}