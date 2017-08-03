var $body = $('body');

/**
 * Reveal all inventory notifications for the given SKU
 */
$body.on('click', '.js-inventoryNotification', function() {
    BLC.ajax({
        url: $(this).attr('href')
    }, function(data) {
        var extendedOptions = $.extend({
            afterShow: function() {
                $('.simplemodal-wrap').find('form:first').find('input:first').focus();
                return true;
            }
        }, Inventory.modalAccountOptions);
        $.modal(data, extendedOptions);
    });
    return false;
});

/**
 * Subscribe to inventory notifications for the given SKU
 */
$body.on('click','.js-subscribeButton', function() {
    var $form = $(this).closest("form");
    BLC.ajax({
        url: $form.attr('action'),
        type: "POST",
        data: $form.serialize()
    }, function(responseData) {
        if (responseData.success) {
            $.modal.close();
            HC.showNotificationSuccess("Successfully subscribed to inventory notifications");
        }
    });
    return false;
});