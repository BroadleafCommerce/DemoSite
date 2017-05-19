/* Operations that deal with checkout */
$(function(){
    // The options used for the login/register modal
    var modalCheckoutOptions = {
        maxWidth    : 720,
        maxHeight   : 560,  
        minHeight   : 360,
        position    : ['30px']
    };
    
    function copyShippingForm() {
        $('.cloneable').each(function() {
            $("#billing_info input[name='" + $(this).attr('name') + "']").val($(this).val()).attr("disabled", "disabled");
            $("#billing_info select[name='" + $(this).attr('name') + "']").val($(this).val()).attr("disabled", "disabled");
        })
    }

    function copyBillingForm() {
        $('.cloneable').each(function() {
            $("#shipping_info input[name='" + $(this).attr('name') + "']").val($(this).val()).attr("disabled", "disabled");
            $("#shipping_info select[name='" + $(this).attr('name') + "']").val($(this).val()).attr("disabled", "disabled");
        })
    }
    
    function showAddAddress() {
        var $form = $('form#multiship_address');
        BLC.ajax({url: $form.attr('action'),
                type: "POST", 
                data: $form.serialize() 
            }, function(data, extraData) {
                var showAddAddressUrl = $('a.add-address-link').attr('href');
                BLC.ajax({url: showAddAddressUrl}, function(data, extraData) {
                    $('#multiship-products').hide();
                    $('.simplemodal-wrap').append(data);
                });
            }
        );
        return false;
    }

    /* Toggle visibility of payment methods */
    $('body').on('click', 'input#paymentMethod_cc', function() {
        $('#paymentOptions dd').css({display:"none"});
        $(this).closest('dt').next().css({display:"block"});
    });

    $('body').on('click', 'input#paymentMethod_paypal', function() {
        $('#paymentOptions dd').css({display:"none"});
        $(this).closest('dt').next().css({display:"block"});
    });

    $('body').on('click', 'input#paymentMethod_cod', function() {
        $('#paymentOptions dd').css({display:"none"});
        $(this).closest('dt').next().css({display:"block"});
    });

    /* Toggle visibility of promo and credit options */
    $('body').on('click', 'input#promoCreditOption_credit', function() {
        $(this).closest('dt').next().toggle();
    });

    $('body').on('click', 'input#promoCreditOption_promo', function() {
        $(this).closest('dt').next().toggle();
    });

    function togglePromoCreditOptions() {
        $('#promoCreditOptions').children('dd').each(function(){
            if ($(this).find('span.error').length == 0) {
                $(this).hide();
            } else {
                $(this).prev().find('input[type=checkbox]').prop('checked', true);
                $(this).show();
            }
        })
    }

    /** Helper methods to copy from billing address to shipping address and vice versa based on your layout **/

    /* Copy Billing Form to Shipping Form Checkbox */
    $('body').on('click', 'input#use_billing_address', function() {
        if ($(this).is(':checked')) {
            copyBillingForm();
        } else {
            $(this).closest('form').find(".clearable").val("").removeAttr("disabled");
        }
    });

    /* Copy Shipping Form to Billing Form Checkbox */
    $('body').on('click', 'input#use_shipping_address', function() {
        if ($(this).is(':checked')) {
            copyShippingForm();
        } else {
            $(this).closest('form').find(".clearable").val("").removeAttr("disabled");
        }
    });

    /* Submit Shipping Form when radio button is checked */
    $('body').on('click', '#select_shipping', function() {
        $('#shipping_info').submit();
    });

    /* Show or Edit multiship options link was clicked */
    $('body').on('click', 'a#multiship', function() {
        BLC.ajax({url: $(this).attr('href')}, function(data) {
            $.modal(data, modalCheckoutOptions);
        });
        return false;
    });
    
    /* Add address from the dropdown was selected */
    $('body').on('change', 'select.multiship-address', function() {
        var $option = $(this).children(':selected');
        if ($option.val() == 'addNewAddress')  {
            showAddAddress();
        }
    });
    
    /* Add address button clicked */
    $('body').on('click', 'a.add-address-link', function() {
        return showAddAddress();
    });
    
    /* Cancel pressed on multiship */
    $('body').on('click', '#multiship-products a.cancel', function() {
        $.modal.close();
        return false;
    });
    
    /* Cancel pressed on add address */
    $('body').on('click', '#multiship-add-address a.cancel', function() {
        $('#multiship-products').show();
        $('#multiship-add-address').remove();
        return false;
    });
    
    /* Save pressed on add address */
    $('body').on('click', '#multiship-add-address input.save', function() {
        var $form = $(this).closest('form');
        
        BLC.ajax({url: $form.attr('action'),
                type: "POST", 
                data: $form.serialize(),
                cache: false
            }, function(data, extraData) {
                $('.simplemodal-wrap').html(data);
            }
        );
        return false;
    });

    togglePromoCreditOptions();
});
