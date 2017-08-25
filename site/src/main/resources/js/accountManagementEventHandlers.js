/**
 * Event handlers that deal with the customer's account
 */

/**
 * Toggle order details view on click
 */
$('body').on('click', '.js-viewOrderDetails', function(){
    var $orderHistoryRow = $(this).closest('.js-orderHistoryRow');
    var $orderDetailsRow = $orderHistoryRow.next('.js-orderDetailsRow');
    var $orderDetailsRowData = $orderDetailsRow.find('td');
    var $downArrow = $orderHistoryRow.find('.js-downArrow');
    var $upArrow = $orderHistoryRow.find('.js-upArrow');

    // if this is the currently opened order details, collapse it
    if ($orderDetailsRow.hasClass('is-hidden')) {
        var url = $(this).attr('href');

        $orderDetailsRowData.load(url, function() {
            $orderDetailsRow.removeClass('is-hidden');
            $upArrow.removeClass('is-hidden');
            $downArrow.addClass('is-hidden');
        });
    } else {
        $orderDetailsRow.addClass('is-hidden');
        $upArrow.addClass('is-hidden');
        $downArrow.removeClass('is-hidden');
    }

    return false;
});

/**
 * Update the Dynamic Customer Address Form when the country is changed
 */
$('body').on('change', '.js-customerAddressCountry', function(){
    var $option = $(this).children(':selected');
    BLC.ajax({
        url: $option.data('href'),
        type: "GET",
        cache: false
    }, function(data) {
        $('.js-dynamicCustomerAddressForm').html(data);
    });
});

/**
 * Makes the mobile account menu (un)collapse
 */
$('#account-menu-dropdown').click(function () {
    $('#account-menu-collapsable').slideToggle();
});


/**
 * Handles the click event of an address during the customer address management section.
 */
$body.on('click', '.js-manage-address', function() {
    var $formInfo = $('.js-manage-address-entry form');
    var action = $(this).data('value');

    $(this).parent().find('.active').removeClass('active');
    $(this).addClass('active');

    if (action === "new-address") {
        // Clear all inputs and dropdowns
        $formInfo.find('input:not(.btn, :radio,[name=csrfToken],[name=address\\.isoCountryAlpha2]), select:not(.js-chooseAddress)')
            .each(function(i, el) {
                if ($(el).is(':checkbox')) {
                    $(el).prop('checked', false);
                }
                $(el).val('').trigger('change');
            });
        return false;
    }

    BLC.ajax({
        url: action,
        type: 'GET'
    }, function(data) {
        // Update all inputs and dropdowns
        $formInfo.find('input:not(.btn, :radio,[name=csrfToken],[name=address\\.isoCountryAlpha2]), select:not(.js-chooseAddress)')
            .each(function(i, el) {
                var newVal;
                if ($(el).is('select')) {
                    newVal = $(data).find("select[name='" + $(el).attr('name') + "']").val();
                } else {
                    newVal = $(data).find("input[name='" + $(el).attr('name') + "']").val();
                }

                if ($(el).is(':checkbox')) {
                    $(el).prop('checked', $(data).find("input[name='" + $(el).attr('name') + "']").is(':checked'));
                }

                $(el).val(newVal).trigger('change');
            });

        $formInfo.prop('action', $(data).find('form.manage-account').prop('action'));
    });
    return false;
});

/**
 * Responsible for handling the "remove" button click event on an address during customer address management.
 */
$body.on('click', '.js-remove-address', function() {
    var $addressButton = $(this).closest('.js-manage-address');
    var action = $addressButton.data('value');
    var $form = $('.js-manage-address-entry form');
    var formData = BLC.serializeObject($form);
    formData['removeAddress'] = 'Remove';

    BLC.ajax({
        method: 'POST',
        url: action,
        data: formData
    }, function() {
        window.location = '/account/addresses';
    });

    return false;
});

/**
 * Handles requests for adding a saved payment
 */
$body.on('click','.js-savePayment', function () {
    var $paymentForm = $('.js-creditCardPaymentForm');
    var $creditCardForm = $paymentForm.find('.js-creditCardData');
    var creditCardToken = SamplePaymentService.tokenizeCard($creditCardForm);
    $paymentForm.find('.js-paymentToken').val(creditCardToken);

    BLC.ajax({
        type: "POST",
        url: $paymentForm.attr('action'),
        data: $paymentForm.serialize()
    }, function(data) {
        AccountManagement.replaceManageAccountForm(data);
    });

    return false;
});

/**
 * Handles requests for making a saved payment the default for a customer
 */
$body.on('click','.js-makePaymentDefault', function () {
    BLC.ajax({
        type: "POST",
        url: $(this).attr('href')
    }, function(data) {
        AccountManagement.replaceSavedPaymentCards(data);
    });

    return false;
});

/**
 * Handles requests for deleting a saved payment
 */
$body.on('click','.js-deletePayment', function () {
    BLC.ajax({
        type: "POST",
        url: $(this).attr('href')
    }, function(data) {
        AccountManagement.replaceSavedPaymentCards(data);
    });

    return false;
});

$(document).ready(function() {
    $(window).resize(accountResizeListener); //start window resize listener

    //allows error messages closer to input fields
    $('.error-group .text-danger').parent().find('input').css('margin-bottom', '0');
});

/**
 * Listen for resizes to show the mobile account menu
 */
var accountResizeListener = function () {
    var accountMenu = $('#account-menu');
    var collapsbleMenu = $('#account-menu-collapsable');
    if($(window).width() >= 768 && !accountMenu.hasClass('js-desktop'))
    {
        accountMenu.addClass('js-desktop');
        collapsbleMenu.css('display', 'block')
            .css('overflow', '')
            .css('height', '')
            .css('padding-top', '')
            .css('margin-top', '')
            .css('padding-bottom', '')
            .css('margin-bottom', '');
    }
    else if($(window).width() < 768 && accountMenu.hasClass('js-desktop'))
    {
        accountMenu.removeClass('js-desktop');
        collapsbleMenu.css('display', 'none');
    }
};

accountResizeListener();
