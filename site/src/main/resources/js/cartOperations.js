$(function(){
    // Set up basic options for the cart modal
    var modalCartOptions = {
        minWidth    : 940,
        maxWidth    : 940,
        minHeight   : 355,
        maxHeight   : 570,
        position    : ['30px']
    };
    
    var modalProductOptionsOptions = {
        minWidth    : 190,
        maxWidth    : 190,
        minHeight   : 300,
        position    : ['30px']
    };
    
    // This will change the header "X item(s)" text to the new count and pluralization of "item"
    function updateHeaderCartItemsCount(newCount) {

        //Pull the word that was set in the html from the internationalized version from the locale
        var singularItem = $('span#headerCartItemWordSingular_i18n').text();
        var plurarlItem = $('span#headerCartItemWordPlural_i18n').text();

        $('.headerCartItemsCount').html(newCount);
        $('.headerCartItemsCountWord').html((newCount == 1) ? singularItem: plurarlItem);
    }
    
    function updateWithPromo(promo) {
        $('.headerCartItemCount').html();
    }
    
    // Hides the add to cart/add to wishlist button and shows the in cart/in wishlist button
    // orderType can either be 'cart' or 'wishlist'
    function showInCartButton(productId, orderType) {
        $('.productActions' + productId).children('.in_'+orderType).removeClass('hidden');
        $('.productActions' + productId).children('.add_to_'+orderType).addClass('hidden');
    }
    
    // Hides the in cart/in wishlist button and shows the add to cart/add to wishlist button
    // orderType can either be 'cart' or 'wishlist'
    function showAddToCartButton(productId, orderType) {
        $('.productActions' + productId).children('.add_to_'+orderType).removeClass('hidden');
        $('.productActions' + productId).children('.in_'+orderType).addClass('hidden');
    }

    // Show the cart in a modal when any link with the class "modalcart" is clicked
    $('body').on('click', 'a.modalcart', function() {
        BLC.ajax({ url: $(this).attr('href') }, function(data) {
            var extendedOptions = $.extend({}, modalCartOptions);
            if ($(this).hasClass('refreshonclose')) {
                extendedOptions = $.extend({ afterClose: function() { window.location.reload(); }}, extendedOptions);
            }
            $.modal(data, extendedOptions);
        });

        return false;
    });
    
    // Intercept add to cart operations and perform them via AJAX instead
    // This will trigger on any input with class "addToCart" or "addToWishlist"
    $('body').on('click', 'input.addToCart,input.addToWishlist', function() {
        var $button = $(this),
            $container = $button.closest('.product_container'),
            $form = $button.closest('form'),
            $options = $container.find('span.option-value'),
            $errorSpan = $container.find('span.error');
            $productOptionsSpan = $container.find('span.productOptionsSpan');
        if ($container.length == 0) {
            var myId = $button.parent().attr('id').substring('productOptions'.length);
            $container = $('.productActions' + myId).closest('.product_container');
            $form = $container.find('form');
            $options = $button.parent().find('span.option-value');
            $errorSpan = $button.parent().find('span.error');
            $productOptionsSpan = $container.find('span.productOptionsSpan');
        }
        
        var itemRequest = BLC.serializeObject($form),
            modalClick = $button.parents('.simplemodal-wrap').length > 0,
            wishlistAdd = $button.hasClass('addToWishlist');
            
        if (itemRequest.hasProductOptions == "true" && !modalClick) {
            $.modal($('#productOptions' + itemRequest.productId), modalProductOptionsOptions);
        } else {
            $options.each(function(index, element) {
            	var optionType = $(element).data('optiontype');
            	var value;
            	
            	if ("TEXT" == optionType) {
            		value = $(element).next().find('input').val();
            	} else if ("TEXTAREA" == optionType) {
            		value = $(element).next().find('textarea').val();
            	} else if ("DECIMAL" == optionType) {
            		value = $(element).next().find('input').val();
            	} else if ("SELECT" == optionType) {
            		value = $(element).next().find('select option:checked').val();
            	} else {
            		value = $(element).text();
            	}//need to add other types(date,long, etc) as needed
            	
            	
            	itemRequest['itemAttributes[' + $(element).attr('id') + ']'] = value;
            });
            
            BLC.ajax({url: $form.attr('action'), 
                    type: "POST",
                    dataType: "json",
                    data: itemRequest
                }, function(data, extraData) {
                    if (data.error) {
                        if (data.error == 'allOptionsRequired') {
                            $errorSpan.css('display', 'block');
                            $errorSpan.effect('highlight', {}, 1000);
                        } else if (data.error == 'productOptionValidationError') {
                        	// find the product option that failed validation with jquery
                        	// put a message next to that text box with value = data.message
                        	$productOptionsSpan.text('Product Validation Failed: '+ data.errorCode+' '+data.errorMessage);
                        	$productOptionsSpan.css('display', 'block');
                        	$productOptionsSpan.effect('highlight', {}, 1000);
                        	
                        } else if (data.error = 'inventoryUnavailable') {
                            HC.showNotification("This item is no longer in stock. We apologize for the inconvenience.", 7000);
                        } else {
                            HC.showNotification("Error adding to cart");
                        }
                    } else {
                        $errorSpan.css('display', 'none'); 
                        $productOptionsSpan.css('display', 'none'); 
                        updateHeaderCartItemsCount(data.cartItemCount);
                        
                        if (modalClick) {
                            $.modal.close();
                        } else if (wishlistAdd) {
                            showInCartButton(data.productId, 'wishlist');
                        } else {
                        	if(data.skuId != null) {
                        		showInCartButton(data.skuId, 'cart');
                        	} else {
                            	showInCartButton(data.productId, 'cart');
                        	}
                        }
                        
                        if (wishlistAdd) {
                            HC.showNotification(data.productName + "  has been added to your wishlist!");
                        } else {
                            HC.showNotification(data.productName + "  has been added to the cart!", 2000);
                        }
                    }
                }
            );
        }
        return false;
    });

    // Intercept update quantity operations and perform them via AJAX instead
    // This will trigger on any input with class "updateQuantity"
    $('body').on('click', 'input.updateQuantity', function() {
        var $form = $(this).closest('form');
        
        BLC.ajax({url: $form.attr('action'),
                type: "POST", 
                data: $form.serialize() 
            }, function(data, extraData) {
                if (extraData) {
                    updateHeaderCartItemsCount(extraData.cartItemCount);
                    if ($form.children('input.quantityInput').val() == 0) {
                    	if(extraData.skuId != null) {
                        	showAddToCartButton(extraData.skuId, 'cart');
                    	} else {
                    		showAddToCartButton(extraData.productId, 'cart');
                    	}
                    }
                }

                $('.simplemodal-wrap').html(data);
            }
        );
        return false;
    });
    
    // Intercept remove from cart operations and perform them via AJAX instead
    // This will trigger on any link with class "remove_from_cart"
    $('body').on('click', 'a.remove_from_cart', function() {
        var link = this;
        
        BLC.ajax({url: $(link).attr('href'),
                type: "GET"
            }, function(data, extraData) {
                updateHeaderCartItemsCount(extraData.cartItemCount);
                if(extraData.skuId != null) {
                	showAddToCartButton(extraData.skuId, 'cart');
            	} else {
            		showAddToCartButton(extraData.productId, 'cart');
            	}
                
                $('.simplemodal-wrap').html(data);
            }
        );
        return false;
    });
    
    // Intercept remove from cart operations and perform them via AJAX instead
    // This will trigger on any link with class "remove_promo"
    $('body').on('click', 'a.remove_promo', function() {
        var link = this;
        
        BLC.ajax({url: $(link).attr('href'),
                type: "GET"
            }, function(data) {
                $('.simplemodal-wrap').html(data);
            }
        );
        return false;
    });
    
    $('body').on('click', 'input#addPromo', function() {
        var $form = $(this).closest('form');
        
        BLC.ajax({url: $form.attr('action'),
                type: "POST", 
                data: $form.serialize() 
            }, function(data, extraData) {
                if(!extraData.promoAdded) {
                    $("#cart_promo_error").html("Promo could not be applied: " + extraData.exception).css("display", "");
                } else {
                    $('.simplemodal-wrap').html(data);
                }
            }
        );
        return false;
    });
});
