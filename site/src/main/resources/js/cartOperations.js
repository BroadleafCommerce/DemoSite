(function(Cart, $, undefined) {

    /// Public Properties

    /**
     * These options define the modal cart view
     */
    Cart.modalCartOptions = {
        minHeight   : 355,
        maxHeight   : 570,
        position    : ['30px']
    };

    /**
     * These options define the modal product option configuration view
     */
    Cart.modalProductOptionsOptions = {
        minWidth    : 190,
        maxWidth    : 190,
        minHeight   : 355,
        position    : ['30px']
    };

    /// Public Methods

    /**
     * This will update the mini cart from the server, containing an up to date representation of the cart.
     */
    Cart.updateMiniCart = function(shouldOpen) {
        BLC.ajax({
            url: '/cart/mini',
            type: "GET"
        }, function(data) {
            $('.js-miniCart:visible').replaceWith(data);

            if (shouldOpen) {
                $('.js-miniCart:visible > a').trigger('click');
            }
        });
    };

    /**
     * Hides the in cart/in wishlist button and shows the add to cart/add to wishlist button
     * orderType can either be 'cart' or 'wishlist'
     * @param {number} productId - the target product id
     * @param {string} orderType - either 'cart' or 'wishlist'
     */
    Cart.showAddToCartButton = function (productId, orderType) {
        toggleCartButton(productId, orderType, false);
    };

    /**
     * This is called after an item is successfully added to the cart or to a wishlist.
     * It is responsible for changing the products "Add to Cart" button to "In Cart" or "In Wishlist"
     * depending on the action taken.
     * @param {object} data - data associated with the item that was added
     * @param {boolean} modalClick - did this get called from a modal
     * @param {boolean} wishlistAdd - whether or not this was the result of a wishlist add
     */
    Cart.addToCartSuccess = function(data, modalClick, wishlistAdd) {

        var itemId = getItemIdFromData(data);
        if (wishlistAdd) {
            showInCartButton(itemId, 'wishlist');
            HC.showNotificationSuccess(data.productName + " has been added to your wishlist!", 2000);
        } else {
            showInCartButton(itemId, 'cart');
            Cart.updateMiniCart(true);

            // Update the CSR header in the edge case where the CSR created the cart for the Customer
            if ($('body').hasClass('csr-mode')) {
                BLC.csr.refreshCsrSelector();
            }
        }
    };

    /**
     * Builds out the item attributes associated with the product options for a given request.
     * Product options are defined in elements with the `js-optionValue` class.
     * @param {object} itemRequest
     * @param {element} $container
     */
    Cart.buildProductOptionsForRequest = function(itemRequest, $container) {
        var $options = $container.find('span.js-optionValue');
        var $childOrderItems = $container.find('.js-configureRow');

        // Find top level order item product options
        $options.each(function (index, element) {
            if ($(element).closest('.js-itemWrapper').length) {
                return;
            }

            var value = getProductOptionValue(element);
            itemRequest['itemAttributes[' + $(element).attr('id') + ']'] = value;
        });

        // Find any child order item product options
        $childOrderItems.each(function (index, element) {
            var $childOptions = $(element).find('.js-optionValue');
            var itemIndex = $(element).find('.js-productOptionData').data('item-index');
            $childOptions.each(function (i, el) {
                var value = getProductOptionValue(el);
                itemRequest['childOrderItems[' + itemIndex + '].itemAttributes[' + $(el).attr('id') + ']'] = value;
            });
        });
    };

    /**
     * Responsible for parsing any errors returned from an attempted add to cart.
     * @param {object} data
     * @param {object} $container
     */
    Cart.handleAddToCartError = function(data, $container) {
        var $errorSpan = $container.find('span.error');
        var $productOptionsSpan = $container.find('span.js-productOptionsSpan');

        // Hide any old errors
        $errorSpan.hide();
        $productOptionsSpan.hide();
        $('.js-itemErrorSpan').hide();

        if (data.error == 'allOptionsRequired') {
            var item = $($container).find('[name$="productId"]').filter(function (i, e) {
                return $(e).val() == data.productId;
            });
            var $wrapper = $(item).closest('.js-itemWrapper');
            var $itemErrorSpan = $wrapper.find('.js-itemPoErrorSpan, .error');
            $itemErrorSpan.show();
            $errorSpan.show();
        } else if (data.error == 'productOptionValidationError') {
            // find the product option that failed validation with jquery
            // put a message next to that text box with value = data.message
            $productOptionsSpan.text('Product Validation Failed: ' + data.errorCode + ' ' + data.errorMessage);
            $productOptionsSpan.show();
        } else if (data.error == 'illegalCartOperation') {
            alert(data.exception);
        } else if (data.error == 'inventoryUnavailable') {
            HC.showNotificationWarning("This item is no longer in stock. We apologize for the inconvenience.", 7000);
        } else if (data.error == 'addOnValidationError') {
            displayConfigurationErrors(data);
        } else {
            HC.showNotificationError("Error adding to cart");
        }
    };

    /**
     * Responsible for updating the quantity for an order item.  This method only submits the form and is not responsible for changing
     * quantity field.
     * @param {object} element
     * @returns {boolean}
     */
    Cart.updateItemQuantity = function(element) {
        if (BLC.hasOwnProperty("theme") && BLC.theme.vars.ajaxCart === "false") {
            return true;
        }

        var $form = $(element).closest('form');
        BLC.ajax({
            url: $form.attr('action'),
            type: "POST",
            data: $form.serialize()
        }, function(data, extraData) {
            if (data.error && data.error == 'illegalCartOperation') {
                alert(data.exception);
            } else {
                if (extraData) {
                    if ($form.children('.js-quantityInput').val() === 0) {
                        if(extraData.skuId != null) {
                            Cart.showAddToCartButton(extraData.skuId, 'cart');
                        } else {
                            Cart.showAddToCartButton(extraData.productId, 'cart');
                        }
                    }
                }

                if ($(data).hasClass('js-wishlist')) {
                    $('.js-wishlist').replaceWith(data);
                } else {
                    $('.js-cart').replaceWith(data);
                    Cart.updateMiniCart();
                }
            }
        });
        return false;
    };

    /**
     * Responsible for building an Add or Remove promo url with a parameter specifying whether or not the request is coming
     *  from the checkout context.
     *
     * @param {element} the Add or Remove promo link
     * @returns {String} the promotion action url
     */
    Cart.buildPromoUrl = function($element) {
        var params = {};
        if ($element.closest('#checkout').length) {
            params['isCheckoutContext'] = true;
        }

        var baseUrl = $element.attr('href');
        if (baseUrl === undefined) {
            var $form = $element.closest('form');
            baseUrl = $form.attr('action');
        }

        return BLC.buildUrlWithParams(baseUrl, params);
    };

    /// Private Methods

    /**
     * Hides the add to cart/add to wishlist button and shows the in cart/in wishlist button
     * orderType can either be 'cart' or 'wishlist'
     * @param {number} productId - the target product id
     * @param {string} orderType - either 'cart' or 'wishlist'
     */
    function showInCartButton(productId, orderType) {
        toggleCartButton(productId, orderType, true);
    }

    /**
     * Toggles the add to cart/add to wishlist button with the in cart/in wishlist button
     * orderType can either be 'cart' or 'wishlist'
     * @param {number} productId - the target product id
     * @param {string} orderType - either 'cart' or 'wishlist'
     * @param {boolean} inCart - whether or not the item has been added to the cart
     */
    function toggleCartButton(productId, orderType, inCart) {
        var $productActions = $('.js-productActions' + productId);
        $productActions.find('.js-addTo' + capitalizeFirstLetter(orderType) + 'Container').toggleClass('is-hidden', inCart);
        $productActions.find('.js-in' + capitalizeFirstLetter(orderType) + 'LinkContainer').toggleClass('is-hidden', !inCart);
    }

    /**
     * Returns the string with the first letter capitalized
     * @param {string} string - String to be capitalized
     */
    function capitalizeFirstLetter(string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    }

    /**
     * Returns a sku id if its present on the passed in data object, otherwise it tries to
     * return a product id.
     * @param {object} data - an object containing "skuId" and/or "productId"
     * @returns {number} either a skuId or productId
     */
    function getItemIdFromData(data) {
        if (data.skuId != null) {
            return data.skuId;
        }
        return data.productId;
    }

    /**
     * Returns the value for the given product option
     * @param {object} element - the product option element
     * @returns {string} the value for the product option
     */
    function getProductOptionValue(element) {
        var optionType = $(element).data('option-type');
        var value;

        if ("TEXT" == optionType) {
            value = $(element).next().find('input').val();
        } else if ("TEXTAREA" == optionType) {
            value = $(element).next().find('textarea').val();
        } else if ("DECIMAL" == optionType) {
            value = $(element).next().find('input').val();
        } else {
            value = $(element).text();
        } // need to add other types(date,long, etc) as needed
        return value;
    }

    /**
     * Displays any errors returned during the product configuration process
     * @param {object} data - data associated with the item that had errors
     */
    function displayConfigurationErrors(data) {
        var errors = JSON.parse(data.errorMessage);
        $(errors).each(function (i, err) {
            var items = $('[name*="addOnXrefId"]').filter(function (i, e) {
                return $(e).val() == err.addOnXrefId;
            });
            var $container = $(items).closest('.js-itemWrapper');
            var $itemErrorSpan = $container.find('.js-itemErrorSpan');
            $itemErrorSpan.text(err.message);
            $itemErrorSpan.show();
        });
    }
})(window.Cart = window.Cart || {}, jQuery);