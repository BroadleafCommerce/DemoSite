(function(HC, $, undefined) {

    // Public Parameters

    /**
     * Defines the quickview modal options
     */
    HC.quickviewOptions = {
        minWidth    : 760,
        maxWidth    : 760,
        minHeight   : 485,
        maxHeight   : 700,
        position    : ['30px']
    };

    HC.defaultNotificationDelay = 3500;

    // Public Functions

    /**
     * Reveals a modal showing the facet multi-select
     * @param {string} abbr - the abbreviated name of the facet
     */
    HC.showFacetMultiselect = function(abbr) {
        $.modal($('#facet-' + abbr), {
            maxWidth: 200,
            minWidth: 200,
            minHeight: 150
        });
    };

    /**
     * Shows a toast notification with the provided text
     * If a delay is not provided, it will default to the value of HC.defaultNotificationDelay
     * @param {string} type - 'success', 'info', 'warning', or 'error' depending on the type of notification desired
     * @param {string} notification - the text/html to be shown
     * @param {number} delay - how long the notification should be shown
     */
    HC.showNotification = function(type, notification, delay) {
        if (!delay) {
            delay = HC.defaultNotificationDelay;
        }

        toastr.options = {
            'showMethod': 'slideDown'
        };
        toastr[type](notification, { showDuration: delay });
    };

    /**
     * Shows a successful toast notification with the provided text
     * If a delay is not provided, it will default to the value of HC.defaultNotificationDelay
     * @param {string} notification - the text/html to be shown
     * @param {number} delay - how long the notification should be shown
     */
    HC.showNotificationSuccess = function (notification, delay) {
        HC.showNotification('success', notification, delay);
    };

    /**
     * Shows an info toast notification with the provided text
     * If a delay is not provided, it will default to the value of HC.defaultNotificationDelay
     * @param {string} notification - the text/html to be shown
     * @param {number} delay - how long the notification should be shown
     */
    HC.showNotificationInfo = function (notification, delay) {
        HC.showNotification('info', notification, delay);
    };

    /**
     * Shows a warning toast notification with the provided text
     * If a delay is not provided, it will default to the value of HC.defaultNotificationDelay
     * @param {string} notification - the text/html to be shown
     * @param {number} delay - how long the notification should be shown
     */
    HC.showNotificationWarning = function (notification, delay) {
        HC.showNotification('warning', notification, delay);
    };

    /**
     * Shows an error toast notification with the provided text
     * If a delay is not provided, it will default to the value of HC.defaultNotificationDelay
     * @param {string} notification - the text/html to be shown
     * @param {number} delay - how long the notification should be shown
     */
    HC.showNotificationError = function (notification, delay) {
        HC.showNotification('error', notification, delay);
    };

    /**
     * Handles the change of a ProductOption
     * @param {element} $option - the text/html to be shown
     */
    HC.changeProductOption = function($option) {
        if (!$option.is('.active')) {

            var $container = $option.closest('.js-configureRow');
            if (!$container.length) {
                $container = $option.closest('.js-itemWrapper');
            }
            if (!$container.length) {
                $container = $('body');
            }

            $option.siblings('.active').removeClass('active');
            $option.toggleClass('active');

            var selectedOption = $option.data('product-option-value');
            var $optionText = $option.parents('.js-productOptionGroup').find('.js-optionValue');

            if ($option.is('select')) {
                selectedOption = $option.find(':selected').data('product-option-value');
                $option.removeClass('active');
                $option.find(':not(:selected)').removeClass('active');
                $option.find(':selected').addClass('active');
            }

            if (selectedOption !== undefined) {
                $optionText.text(selectedOption.valueName);
                var productOptionData = getProductOptionData($container);

                for (var i = 0; i < productOptionData.length; i++) {
                    var option = productOptionData[i];
                    if (option.id === selectedOption.optionId) {
                        option.selectedValue = selectedOption.valueId;
                        break;
                    }
                }

                updateCurrentImage($container);
                updatePriceDisplay($container);
            }
        }
    };

    /**
     * Helps to manage the selected locale
     */
    HC.updateLocaleSelection = function(){
        var locale = $('#selectedLocale').text();
        $("#" + locale).addClass('selected');
    };

    /**
     * Hides/shows a product's quickview link
     * @param {element} $container - the container of the quickview link
     * @param {boolean} show - whether or not the quickview link should be shown
     */
    HC.toggleQuickview = function($container, show) {
        var $qv = $container.find('.js-quickview');
        $qv.toggle(show);
    };

    /**
     * This method sets any existing product options for the passed in add-on element
     * @param {element} $container - the add-on to set options for
     */
    HC.setExistingProductOptions = function($container) {
        $container.find('.item-attributes input').each(function (i, attribute) {
            var attrName = $(attribute).attr('name');
            var attrValue = $(attribute).val();

            var $option = $container.find('.js-optionValue[id=' + attrName + ']');
            var optionType = $option.data('option-type');

            if ("TEXT" == optionType) {
                $option.next().find('input').val(attrValue).trigger('change');
            } else if ("TEXTAREA" == optionType) {
                $option.next().find('textarea').val(attrValue).trigger('change');
            } else if ("DECIMAL" == optionType) {
                $option.next().find('input').val(attrValue).trigger('change');
            } else if ("COLOR" == optionType) {
                var $choices = $option.next();
                var chosenOption = $choices.find('.js-productOption').filter(function (i, e) {
                    return $(e).text().trim() == attrValue;
                })[0];
                if ($(chosenOption).length) {
                    HC.changeProductOption($(chosenOption));
                }
            } else {
                var $select = $option.next().find('.selectpicker');
                $select.selectpicker('val', attrValue);
                HC.changeProductOption($select);
            }
            $(attribute).remove();
        });
    };

    // Private Functions

    /**
     * Updates a product's image after product options have been updated
     * @param {element} $container - the product container
     */
    function updateCurrentImage($container) {
        //grab the active product option values
        var activeOptions = $container.find('.js-productOptions .active');
        var optionValues = [];
        $.each(activeOptions, function() {
            optionValues.push($.parseJSON($(this).attr('data-product-option-value')));
        });

        var mediaItems = $container.find('.js-productThumbs a');
        var finalMedia;
        var finalMediaMatches = 0;
        $.each(mediaItems, function() {
            var candidateMedia = this;
            var candidateMediaMatches = 0;
            $.each(optionValues, function() {
                if ($(candidateMedia).attr('data-tags') != undefined && $(candidateMedia).attr('data-tags').toLowerCase() === this.valueName.toLowerCase()) {
                    candidateMediaMatches++;
                }
            });
            if (candidateMediaMatches > finalMediaMatches) {
                finalMedia = candidateMedia;
                finalMediaMatches = candidateMediaMatches;
            }
        });

        //at this point I should have the best-matched media item; select it
        if (finalMedia != null) {
            var $wrapper = $(finalMedia).closest('.js-configureRow');
            if ($wrapper.length) {
                var $img = $(finalMedia).find('img');
                var src = $img.attr('src');
                $wrapper.find('.js-mainItemImage').attr('src', src);
            } else {
                $wrapper = $(finalMedia).closest('.js-itemWrapper');
                if ($wrapper.length) {
                    var $img = $(finalMedia).find('img');
                    var src = $img.attr('src');
                    $wrapper.find('.js-mainItemImage').attr('src', src);
                } else {
                    finalMedia.click();
                }
            }

        }
    };

    /**
     * Updates a product's price after product options have been updated
     * @param {element} $container - the product container
     */
    function updatePriceDisplay($container) {
        var productOptions = getProductOptionData($container);

        var selectedProductOptions = [];

        for (var i = 0; i < productOptions.length; i++) {
            if (productOptions[i].selectedValue != null) {
                selectedProductOptions.push(productOptions[i].selectedValue); // add selected value to array
            }
        }

        var productOptionPricing = getPricingData($container);

        var price;

        for (var i = 0; i < productOptionPricing.length; i++) {
            var pricing = productOptionPricing[i];
            if ($(pricing.selectedOptions).not(selectedProductOptions).length == 0 && $(selectedProductOptions).not(pricing.selectedOptions).length == 0) {
                price = pricing.price;
                break;
            }
        }

        if (price) {
            var $price = $container.find('.js-itemPrice');
            if ($price.length != 0) {
                var $productPriceSpan = $price.find('.item-price-span');
                if ($price.data('baseprice') !== '0.00') {
                    $productPriceSpan.html(price);
                }
            } else {
                $price = $('#price span:first');
                if ($price.length !== 0) {
                    $price.text(price);
                }
            }
        }
    };

    /**
     * Gathers the Product's ProductOption data
     * @param {element} $container - the product container
     */
    function getProductOptionData($container) {
        return $container.find('.js-productOptionData').data('product-options');
    };

    /**
     * Gathers the Product's pricing data
     * @param {element} $container - the product container
     */
    function getPricingData($container) {
        return $container.find('.js-productOptionData').data('product-option-pricing');
    };

})(window.HC = window.HC || {}, jQuery);
