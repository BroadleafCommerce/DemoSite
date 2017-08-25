var $body = $('body');

$body.on('click', '.js-submitCheckoutStage', function() {
    Checkout.handleCheckoutStageSubmission($(this));

    return false;
});

$body.on('click', '.js-submitPaymentCheckoutStage', function() {
    Checkout.handlePaymentCheckoutStageSubmission($(this));

    return false;
});

$body.on('click', '.js-performCheckout', function() {
    Checkout.performCheckout();

    return false;
});

$body.on('click', '.js-editCheckoutStage', function() {
    var requestedCheckoutStage = $(this).data('requested-checkout-stage');

    Checkout.navigateToCheckoutStage(requestedCheckoutStage);

    return false;
});

$body.on('changed.bs.select', '.js-paymentMethod', function() {
    Checkout.hideAllPaymentOptionDescriptions();
    Checkout.showPaymentOptionDetails($(this));
    return false;
});

/**
 * Toggle visibility of promo and credit options
 */
$body.on('click', '.js-promoCreditOptionCredit', function() {
    Checkout.togglePaymentOptionDetails($(this));
});

/**
 * Toggle visibility of promo and credit options
 */
$body.on('click', '.js-promoCreditOptionPromo', function() {
    Checkout.togglePaymentOptionDetails($(this));
});

/**
 * Copy Billing Form to Shipping Form, triggered by a Checkbox
 */
$body.on('click', '.js-useBillingAddress', function() {
    if ($(this).is(':checked')) {
        if ($('.js-dynamicShippingForm').length) {
            var $option = $('.js-billingInfoCountry').children(':selected');
            $('.js-shippingInfoCountry').val($option.val());
            Checkout.reloadDynamicForm($('.js-shippingInfoCountry'), $('.js-dynamicShippingForm'), Checkout.copyBillingForm);
        } else {
            Checkout.copyBillingForm();
        }
    } else {
        $(this).closest('form').find('.js-clearable').val("").removeAttr('disabled');
    }
});

/**
 * Show the read-only shipping address in the context of the Billing stage
 */
$body.on('change', '.js-shouldUseShippingAddress', function() {
    var useShippingAddress = $(this).is(':checked');

    if (useShippingAddress) {
        $('.js-checkoutAddressContainer').addClass('is-hidden');

        var copiedShippingAddress = $('.js-readOnlyShippingAddress').html();
        $('.js-copiedShippingAddress').html(copiedShippingAddress);
        $('.js-copiedShippingAddressContainer').removeClass('is-hidden');
    } else {
        $('.js-copiedShippingAddressContainer').addClass('is-hidden');

        Checkout.clearFormInputs($('.js-customAddress'));

        $('.js-checkoutAddressContainer').removeClass('is-hidden');
    }

    return false;
});

/**
 * Submit Shipping Form when the shipping method has been selected
 */
$body.on('click', '.js-selectShipping', function() {
    $('.js-shippingInfo').submit();
});

/**
 *
 */
$body.on('click', 'a.js-multiship', function() {
    var link = this;
    BLC.ajax({
        url: $(link).attr('href')
    }, function(data) {
        var $shippingStage = $(link).closest('.js-checkoutStage');
        $shippingStage.replaceWith(data);
    });
    return false;
});

/**
 * Reveal an add multiship address form
 */
$body.on('change', '.js-multishipAddress', function() {
    var $option = $(this).children(':selected');
    if ($option.val() == 'addNewAddress')  {
        Checkout.showAddAddress();
    }
});

/**
 * Reveal an add multiship address form
 */
$body.on('click', '.js-addAddressLink', function() {
    return Checkout.showAddAddress();
});

/**
 * Close the edit multiship options modal
 */
$body.on('click', '.js-multishipProducts .js-cancel', function() {
    $.modal.close();
    return false;
});

/**
 * Close the add multiship address modal
 */
$body.on('click', '.js-multishipAddAddress .js-cancel', function() {
    $('.js-multishipProducts').show();
    $('.js-multishipAddAddress').remove();
    return false;
});

/**
 * Process the saving of an additional multiship address
 */
$body.on('click', '.js-multishipAddAddress .js-save', function() {
    var $form = $(this).closest('form');

    BLC.ajax({
        url: $form.attr('action'),
        type: "POST",
        data: $form.serialize(),
        cache: false
    }, function(data, extraData) {
        $('.simplemodal-wrap').html(data);
    });
    return false;
});

/**
 * Reload the Billing dynamic form based on the selected country
 */
$body.on('change', '.js-billingInfoCountry', function(){
    Checkout.reloadDynamicForm($(this), $('.js-dynamicBillingForm'));
});

/**
 * Reload the Shipping dynamic form based on the selected country
 */
$body.on('change', '.js-shippingInfoCountry', function(){
    Checkout.reloadDynamicForm($(this), $('.js-dynamicShippingForm'));
});

/**
 * Reload the Multi-ship dynamic form based on the selected country
 */
$body.on('change', '.js-multiShippingInfoCountry', function(){
    Checkout.reloadDynamicForm($(this), $('.js-dynamicMultiShippingForm'));
});

/**
 * Handles the click event of a saved payment during the payment stage of the checkout process.
 */
$body.on('click', '.js-chooseSavedPayment', function() {
    var customerPaymentId = $(this).data('customerpaymentid');

    var $creditCardPaymentForm = $('.js-creditCardPaymentForm');
    $creditCardPaymentForm.find('.js-shouldUseCustomerPayment').val(true);
    $creditCardPaymentForm.find('.js-customerPaymentId').val(customerPaymentId);

    $(this).closest('.js-savedPaymentsContainer').find('.active').removeClass('active');
    $(this).addClass('active');
});

/**
 * Hides the manual payment info forms & shows the customer's saved payments, selecting the active payment
 */
$body.on('click', '.js-chooseSavedPaymentAction', function() {
    var $creditCardPaymentFormContainer = $(this).closest('.js-creditCardPaymentFormContainer');
    var $savedPaymentsContainer = $('.js-savedPaymentsContainer');

    var $creditCardPaymentForm = $('.js-creditCardPaymentForm');
    $creditCardPaymentForm.find('.js-shouldUseCustomerPayment').val(true);
    var customerPaymentId = $savedPaymentsContainer.find('.active').data('customerpaymentid');
    $creditCardPaymentForm.find('.js-customerPaymentId').val(customerPaymentId);

    $creditCardPaymentFormContainer.addClass('is-hidden');

    $savedPaymentsContainer.removeClass('is-hidden');
});

/**
 * Hides the customer's saved payments & shows the manual payment info forms
 */
$body.on('click', '.js-defineNewCreditCard', function() {
    var $savedPaymentsContainer = $(this).closest('.js-savedPaymentsContainer');
    var $creditCardPaymentFormContainer = $('.js-creditCardPaymentFormContainer');

    var $creditCardPaymentForm = $creditCardPaymentFormContainer.find('form');
    $creditCardPaymentForm.find('.js-shouldUseCustomerPayment').val(false);
    $creditCardPaymentForm.find('.js-customerPaymentId').val('');

    $savedPaymentsContainer.addClass('is-hidden');

    $creditCardPaymentFormContainer.removeClass('is-hidden');
});

/**
 * Handles the click event of an address during the shipping and payment sections of the checkout process.
 * Hides and shows options based on what is selected.
 */
$body.on('click', '.js-chooseAddress', function() {
    var $addressContainer = $(this).closest('.js-checkoutAddressContainer');

    // Hide the manual entry form
    $addressContainer.find('.js-customAddress').addClass('is-hidden');

    $(this).parent().find('.active').removeClass('active');
    $(this).addClass('active');

    var action = $(this).data('value');
    if (action === undefined) {
        if ($(this).closest('#shipping_info_stage').length) {
            action = '/checkout/SHIPPING_INFO';
        } else {
            action = '/checkout/PAYMENT_INFO';
        }
    }

    BLC.ajax({
        url: action,
        type: 'GET'
    }, function(data) {
        // Update all inputs and dropdowns
        $addressContainer.find('input:not(:radio,[name=csrfToken],[name=emailAddress],[name=address\\.isoCountryAlpha2]), select:not(.js-chooseAddress)')
            .each(function(i, el) {
                var newVal;
                if ($(el).is('select')) {
                    newVal = $(data).find("select[name='" + $(el).attr('name') + "']").val();
                } else {
                    newVal = $(data).find("input[name='" + $(el).attr('name') + "']").val();
                }
                $(el).val(newVal).trigger('change');
            });
    });
    return false;
});

/**
 * Handles the click event of an address during the shipping and billing sections of the checkout process.
 * Hides and shows options based on what is selected.
 */
$body.on('click', '.js-chooseSavedAddressAction', function() {
    var $formInfo = $(this).closest('form');

    $(this).parent().find('.active').removeClass('active');
    $(this).addClass('active');

    // Show the customer's saved addresses
    $(this).parent().find('li').removeClass('is-hidden');
    $(this).addClass('is-hidden');

    // Hide the manual entry form
    $formInfo.find('.js-customAddress').addClass('is-hidden');
    return false;
});

/**
 * Handles the click event of an address during the shipping and billing sections of the checkout process.
 * Hides and shows options based on what is selected.
 */
$body.on('click', '.js-defineNewAddressAction', function() {
    $(this).parent().find('.active').removeClass('active');
    $(this).addClass('active');

    // Clear all inputs and dropdowns
    var $addressContainer = $(this).closest('.js-checkoutAddressContainer');
    Checkout.clearFormInputs($addressContainer);

    // Hide the customer's saved addresses
    $(this).parent().find('li').addClass('is-hidden');
    $(this).parent().find('li.js-chooseSavedAddressAction').removeClass('is-hidden');

    // Show the manual entry form
    $addressContainer.find('.js-customAddress').removeClass('is-hidden');
    return false;
});

/**
 * Responsible for handling the "edit" button click event on an address during the checkout process.
 */
$body.on('click', '.js-edit-address', function() {
    $(this).closest('.js-chooseAddress').click();

    var $formInfo = $(this).closest('form');

    // Hide the customer's saved addresses
    $formInfo.find('.js-checkoutAddressContainer li').addClass('is-hidden');
    $formInfo.find('.js-chooseSavedAddressAction').removeClass('is-hidden');

    // Show the manual entry form
    $formInfo.find('.js-customAddress').removeClass('is-hidden');
    return false;
});

$(function() {
    Checkout.initialize();

    $('.js-paymentMethodCreditCard').click();
});
