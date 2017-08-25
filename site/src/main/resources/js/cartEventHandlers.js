var $body = $('body');

/**
 * Show the cart in a modal when any link with the class "js-modalCartLink" is clicked
 */
$body.on('click', '.js-modalCartLink', function() {
    BLC.ajax({
        url: $(this).attr('href')
    }, function(data) {
        if (BLC.hasOwnProperty("theme") && BLC.theme.vars.ajaxCart == "false") {
            return true;
        }

        var extendedOptions = $.extend({ href : $(this).attr('href') }, Cart.modalCartOptions);
        if ($(this).hasClass('is-refreshOnClose')) {
            extendedOptions = $.extend({ afterClose: function() { window.location.reload(); }}, extendedOptions);
        }
        $.modal(data, extendedOptions);
    });

    return false;
});

/**
 * Intercept add to cart operations and perform them via AJAX instead
 * This will trigger on any input with class "js-addToCart" or "js-addToWishlist"
 */
$body.on('click', '.js-addToCart, .js-addToWishlist', function(e) {
    if (BLC.hasOwnProperty("theme") && BLC.theme.vars.ajaxCart == "false") {
        return true;
    }

    var $button = $(this);
    var $container = $button.closest('.js-productContainer');
    var $form = $button.closest('form');

    var $errorSpan = $container.find('.error');
    var $productOptionsSpan = $container.find('span.js-productOptionsSpan');
    var itemRequest = BLC.serializeObject($form);
    var wishlistAdd = $button.hasClass('js-addToWishlist');
    var isUpdateRequest = $button.hasClass('js-updateRequest');

    $button.bind('click', false);

    //Let redirect if item is already in wishlist
    if (wishlistAdd) {
        if ($button.hasClass('is-inWishList')) {
            return true;
        }
    }

    //If the product requires something before being added to the wishlist (options, login, etc) then follow link
    if (wishlistAdd && $button.data('action-required')) {
        window.location.href = $button.attr('href');
        return true;
    }

    // Add any product add-ons to the request
    itemRequest = Object.assign({}, itemRequest, BLC.serializeObject($container.find('.js-productAddOns')));

    Cart.buildProductOptionsForRequest(itemRequest, $container);

    BLC.ajax({
        url: $form.attr('action'),
        type: "POST",
        dataType: "json",
        data: itemRequest
    }, function(data, extraData) {
        if (data.error) {
            Cart.handleAddToCartError(data, $container);
        } else {
            $errorSpan.hide();
            $productOptionsSpan.hide();
            $('#' + data.productId + '-QuickView').modal('hide');
            Cart.addToCartSuccess(data, false, wishlistAdd);
            if (wishlistAdd) {
                var $productContainer = $('.js-productContainer[data-id="' + data.productId + '"]');
                $productContainer.find('.js-wishListAddContainer').addClass('is-hidden');
                $productContainer.find('.js-inWishListLinkContainer').removeClass('is-hidden');
            } else if (isUpdateRequest) {
                window.location = '/cart';
            }
        }

        $button.unbind('click');
    });

    return false;
});

/**
 * Intercept update quantity operations and perform them via AJAX instead
 * This will trigger on any input with class "js-updateQuantity"
 */
$body.on('change', '.js-updateQuantity', function() {
    return Cart.updateItemQuantity(this);
});

/**
 * Intercept remove from cart operations and perform them via AJAX instead
 * This will trigger on any link with class "js-removeFromCart"
 */
$body.on('click', 'a.js-removeFromCart', function() {
    if (BLC.hasOwnProperty("theme") && BLC.theme.vars.ajaxCart == "false") {
        return true;
    }

    BLC.ajax({
        url: $(this).attr('href'),
        type: "GET"
    }, function(data, extraData) {
        if (data.error && data.error == 'illegalCartOperation') {
            alert(data.exception);
        } else {
            if(extraData) {
                if (extraData.skuId !== undefined) {
                    Cart.showAddToCartButton(extraData.skuId, 'cart');
                } else {
                    Cart.showAddToCartButton(extraData.productId, 'cart');
                }
            }

            if ($(data).find('.js-wishlist').length) {
                var wishlist = $(data).find('.js-wishlist');
                $('.js-wishlist').replaceWith(wishlist);
            } else {
                $('.js-cart').replaceWith(data);
                Cart.updateMiniCart();
            }
        }
    });
    return false;
});

/**
 * Intercept remove from cart operations and perform them via AJAX instead
 * This will trigger on any link with class "js-removePromo"
 */
$body.on('click', 'a.js-removePromo', function() {
    var $removeAction = $(this);
    if (BLC.hasOwnProperty("theme") && BLC.theme.vars.ajaxCart == "false") {
        return true;
    }

    BLC.ajax({
        url: Cart.buildPromoUrl($removeAction),
        type: "GET"
    }, function(data) {
        if (data.error && data.error == 'illegalCartOperation') {
            alert(data.exception);
        } else {
            if ($removeAction.closest('#checkout').length) {
                $('.js-promoCodePartial').replaceWith(data);
                Checkout.updateCartPricingSummary();
            } else {
                $('.js-cart').replaceWith(data);
            }
        }
    });
    return false;
});

/**
 * Intercept add promo code operations and perform them via AJAX instead
 * This will trigger on any link with class "js-addPromo"
 */
$body.on('click', '.js-addPromo', function() {
    var $addAction = $(this);
    if (BLC.hasOwnProperty("theme") && BLC.theme.vars.ajaxCart == "false") {
        return true;
    }

    var $form = $addAction.closest('form');
    BLC.ajax({
        url: Cart.buildPromoUrl($addAction),
        type: "POST",
        data: $form.serialize()
    }, function(data, extraData) {
        if (data.error && data.error == 'illegalCartOperation') {
            alert(data.exception);
        } else {
            if(!extraData.promoAdded) {
                $("#cartPromoError").html("Promo could not be applied: " + extraData.exception).css("display", "");
            } else {
                if ($addAction.closest('#checkout').length) {
                    $('.js-promoCodePartial').replaceWith(data);
                    Checkout.updateCartPricingSummary();
                } else {
                    $('.js-cart').replaceWith(data);
                }
            }
        }
    });
    return false;
});

$body.on('click', '.js-bundle-summary', function() {
    var actionText = $(this).html();
    if (actionText.indexOf('Show') != -1) {
        actionText = actionText.replace('Show', 'Hide');
    } else {
        actionText = actionText.replace('Hide', 'Show');
    }
    $(this).html(actionText);

    var $productRow = $(this).closest('tr');
    var rowSelector = $productRow.attr('id');
    rowSelector = rowSelector.replace('productRow', '.summaryRow');
    $(rowSelector).toggle();
    $productRow.toggleClass('has-children');
    return false;
});