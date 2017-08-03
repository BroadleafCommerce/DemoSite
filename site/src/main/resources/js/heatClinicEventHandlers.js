/**
 * Reveal the Product's quickview link
 */
$('body').on('mouseenter', '.js-productContainer .js-image', function() {
    HC.toggleQuickview($(this), true);
});

/**
 * Hide the Product's quickview link
 */
$('body').on('mouseleave', '.js-productContainer .js-image', function() {
    HC.toggleQuickview($(this), false);
});

/**
 * Handle the Product's quickview link click & show the quickview modal
 */
$('.btn-quickview').click(function(e) {
    var $button = $(this);
    var $targetModal = $($button.data('target'));
    // If the modal isn't on the page, add it
    if (!$targetModal.length) {
        BLC.ajax({
            url: $button.data('url')
        }, function(data) {
            var $data = $(data);
            $('body').append(data);
            $data.modal('toggle');
            $data.find('.selectpicker').selectpicker({ container: 'body' });
            $('.selectpicker.js-configure-product-choices').trigger('change');
            updateUncacheableData(params);
            
            $('.btn-detailsview').click(function(e) {
                var $detailsButton = $(this);
                $data.modal('toggle');
                window.location.replace($detailsButton.data('url'));
            });
        });
    }
});
