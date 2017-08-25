/* Operations that deal with checkout */
(function(Checkout, $, undefined) {

    // Public properties

    /**
     * These options define the checkout modal view
     */
    Checkout.modalCheckoutOptions = {
        maxWidth    : 720,
        maxHeight   : 560,
        minHeight   : 360,
        position    : ['30px']
    };

    // Public Functions

    /**
     * Hides/shows fields on the checkout page
     */
    Checkout.initialize = function() {
        Checkout.showDynamicAddressForms();
        Checkout.togglePromoCreditOptions();
        Checkout.hideAllPaymentOptionDescriptions();

        if (!savedPaymentContainerIsHidden()) {
            selectActiveSavedPayment();
        }

        // Make checkboxes update the value of their "value holder"
        $(':checkbox').on('change', function() {
            var valueHolderName = $(this).attr('name').replace('-check', '');
            $("[name='" + valueHolderName + "']").val($(this).is(':checked'));
        });
    };

    /**
     * Handles the submission of checkout stage forms & updates the view
     *  with either validation errors or the next stage of the checkout flow
     * @param {element} $checkoutStageAction
     * @param {array} formData
     */
    Checkout.handleCheckoutStageSubmission = function($checkoutStageAction, formData) {
        var $checkoutStage = $checkoutStageAction.closest('.js-checkoutStage');
        var $form = $checkoutStage.find('form');

        if (formData === undefined) {
            formData = $form.serialize();
        }

        BLC.ajax({
            method: 'POST',
            url: $form.attr('action'),
            data: formData
        }, function(data) {
            clearWindowStateHistory();

            Checkout.updateCartPricingSummary();
            replaceCheckoutStages(data);
        });
    };

    /**
     * Handles the submission of the payment checkout stage & updates the view
     *  with the next stage of the checkout flow
     *
     * @param {element} $checkoutStageAction
     */
    Checkout.handlePaymentCheckoutStageSubmission = function($checkoutStageAction) {
        var $paymentStage = $checkoutStageAction.closest('.js-checkoutStage');

        var paymentMethod = getPaymentMethod();
        if ('CreditCard' === paymentMethod) {
            if (shouldSaveNewPayment() || shouldUseCustomerPayment()) {
                savePaymentInfo($paymentStage)
            } else {
                // If we are not saving the payment method, then the payment data will be converted into a nonce.
                // Therefore, we want to avoid tokenizing the credit card data until the checkout is submitted
                // in the review stage. In the meantime, we can save the billing address.
                saveBillingAddressOnly($paymentStage)
            }
        } else if ('COD' === paymentMethod) {
            advanceFromPaymentToReviewStage();
            showReadOnlyPaymentMethod(paymentMethod);
        } else if ('PayPal' === paymentMethod) {
            if (isPayPalConfigComplete()) {
                collectPaymentInfoViaPayPal();
            }
        }
    };

    /**
     * Handles the submission of the review checkout stage
     */
    Checkout.performCheckout = function() {
        var paymentMethod = getPaymentMethod();
        var $reviewStageContent = $('.js-reviewStageContent');
        var $form = $reviewStageContent.find('#' + paymentMethod + 'CheckoutSubmissionForm');

        if ('CreditCard' === paymentMethod) {
            if (!shouldUseCustomerPayment() && !shouldSaveNewPayment()) {
                var $paymentStageContent = $('.js-paymentStageContent');
                var $creditCardData = $paymentStageContent.find('.js-creditCardData');
                var nonce = SamplePaymentService.tokenizeCard($creditCardData);

                $form.find('#payment_method_nonce').val(nonce);
            }
        }

        $form.submit();
    };

    /**
     * Handles the navigation from one checkout stage to another by swapping
     *  out the section specified by `js-checkoutStages`.
     * @param {String} requestedCheckoutStage
     */
    Checkout.navigateToCheckoutStage = function(requestedCheckoutStage) {
        var url = '/checkout/' + requestedCheckoutStage;

        BLC.ajax({
            method: 'GET',
            url: url
        }, function(data) {
            updateWindowStateHistory(requestedCheckoutStage);

            Checkout.updateCartPricingSummary();
            replaceCheckoutStages(data);
        });
    };

    /**
     * Handles the navigation from one checkout stage to another by swapping
     *  out the section specified by `js-checkoutStages`.
     * @param {$element} $editCheckoutStageAction
     */
    Checkout.updateCartPricingSummary = function() {
        BLC.ajax({
            url: '/cart/summary?isCheckoutContext=true',
            method: 'GET'
        }, function(data) {
            $('.js-cart-summary').replaceWith(data);
        });
    };

    /**
     * Copies all 'js-cloneable' fields from the Shipping Form to the Billing Form
     */
    Checkout.clearFormInputs = function($form) {
        $form.find('input:not(:radio,[name=csrfToken],[name=emailAddress],[name=address\\.isoCountryAlpha2]), select:not(.js-chooseAddress)')
            .each(function(i, el) {
                $(el).val('').trigger('change');
            });
    };

    /**
     * Copies all 'js-cloneable' fields from the Shipping Form to the Billing Form
     */
    Checkout.copyShippingForm = function() {
        $('.js-cloneable').each(function() {
            var $billingInfo = $(".js-billingInfo");
            $billingInfo.find("input[name='" + $(this).attr('name') + "']").val($(this).val()).attr('disabled', 'disabled');
            $billingInfo.find("select[name='" + $(this).attr('name') + "']").val($(this).val()).attr('disabled', 'disabled');
        });
    };

    /**
     * Copies all 'js-cloneable' fields from the Billing Form to the Shipping Form
     */
    Checkout.copyBillingForm = function() {
        $('.js-cloneable').each(function() {
            var $shippingInfo = $(".js-shippingInfo");
            $shippingInfo.find("input[name='" + $(this).attr('name') + "']").val($(this).val()).attr('disabled', 'disabled');
            $shippingInfo.find("select[name='" + $(this).attr('name') + "']").val($(this).val()).attr('disabled', 'disabled');
        });
    };

    /**
     * Reveals the Multi-Ship add address form in the current modal
     */
    Checkout.showAddAddress = function() {
        var $form = $('.js-multishipAddressForm');
        BLC.ajax({
            url: $form.attr('action'),
            type: "POST",
            data: $form.serialize()
        }, function(data, extraData) {
            var showAddAddressUrl = $('a.addAddressLink').attr('href');
            BLC.ajax({url: showAddAddressUrl}, function(data, extraData) {
                $('.js-multishipProducts').hide();
                $('.simplemodal-wrap').append(data);
            });
        });
        return false;
    };

    /**
     * Based on the provided dropdown, reload the dynamic form body to show the correct fields.
     * @param {element} dropdown - contains selected option that should drive the dynamic form display
     * @param {element} formDiv - the form whose contents should be replaced
     * @param {function} copyForm - an additional copy method that can be run after the dynamic form is replaced
     */
    Checkout.reloadDynamicForm = function(dropdown, formDiv, copyForm) {
        var $selectedOption = $(dropdown).children(':selected');

        BLC.ajax({
            url: $selectedOption.data('href'),
            type: "GET",
            cache: false
        }, function(data) {
            $(formDiv).html(data);
            if (typeof copyForm == "function") {
                copyForm()
            }
        });
    };

    /**
     * Reveals the dynamic address forms and hides all non-js elements
     */
    Checkout.showDynamicAddressForms = function() {
        $('.js-dynamicAddressCountry').show();
        $('.js-billingInfoCountryNonJs').hide();
        $('.js-shippingInfoCountryNonJs').hide();
    };

    /**
     * Hides all PaymentOption description elements
     */
    Checkout.hideAllPaymentOptionDescriptions = function() {
        $('.js-paymentOptions > div').hide();
    };

    /**
     * Reveals description element related to the selected Payment Option
     */
    Checkout.showSelectedPaymentOptionDetails = function() {
        var paymentMethod = getPaymentMethod();

        $('#' + paymentMethod + 'Options').show();
    };

    /**
     * Reveals description element related to the provided Payment Option
     * @param {element} $paymentOption - the payment option whose details should be revealed
     */
    Checkout.showPaymentOptionDetails = function($paymentOption) {
        var targetOptions = '#' + $paymentOption.val() + 'Options';
        $(targetOptions).show();
    };

    /**
     * Toggles description element related to the provided Payment Option
     * @param {element} $paymentOption - the payment option whose details should be toggled
     */
    Checkout.togglePaymentOptionDetails = function($paymentOption) {
        var targetOptions = $paymentOption.val() + 'Options';
        $(targetOptions).toggle();
    };

    /**
     * Hides/shows the promo credit options based on the presence or absence of errors
     */
    Checkout.togglePromoCreditOptions = function() {
        $('.js-promoCreditOptions').children('dd').each(function(){
            var isChecked = $(this).prev().find('input[type=checkbox]').prop('checked');
            var hasErrors = $(this).find('span.error').length != 0;

            if (!isChecked && !hasErrors) {
                $(this).hide();
            } else {
                $(this).prev().find('input[type=checkbox]').prop('checked', true);
                $(this).show();
            }
        })
    };

    /// Private Methods

    /**
     * Replaces the checkout stages partial with an updated version
     * @param {html} newCheckoutStagesPartial
     */
    function replaceCheckoutStages(newCheckoutStagesPartial) {
        var $previousPaymentOptionForms = $('.js-checkoutStages').find('.js-paymentInfoForm');

        $('.js-checkoutStages').replaceWith(newCheckoutStagesPartial);

        // Initialize any select pickers
        $(newCheckoutStagesPartial).find('.selectpicker').selectpicker({ container: 'body' });

        // Run initialization logic
        Checkout.initialize();

        copyPaymentStageDataToNewPartial($previousPaymentOptionForms);

        if (paymentStageIsReadOnly()) {
            var $paymentStageContent = $('.js-paymentStageContent');
            var $readOnlyPaymentStageContent = $('.js-readOnlyPaymentStageContent');
            populateReadOnlyCreditCardView($paymentStageContent, $readOnlyPaymentStageContent);
        }

        Checkout.hideAllPaymentOptionDescriptions();
        Checkout.showSelectedPaymentOptionDetails();
    }

    function savedPaymentContainerIsHidden() {
        var $savedPaymentsContainer = $('.js-savedPaymentsContainer');

        return $savedPaymentsContainer.hasClass('is-hidden');
    };

    function selectActiveSavedPayment() {
        var $savedPaymentsContainer = $('.js-savedPaymentsContainer');
        var $defaultSavedPayment = $savedPaymentsContainer.find('.js-chooseSavedPayment.active');

        $defaultSavedPayment.click();
    };

    /**
     * If the customer is not anonymous, they will have the opportunity to save their credit card payment method
     *
     * @return
     */
    function shouldSaveNewPayment() {
        var $saveNewPaymentCheckbox = $("[name='shouldSaveNewPayment']");
        var shouldSaveNewPayment = $saveNewPaymentCheckbox.length > 0 && $saveNewPaymentCheckbox.val() === "true";

        return shouldSaveNewPayment;
    };

    /**
     * If the customer is not anonymous, they will have the opportunity to select one of their saved payments
     *
     * @return
     */
    function shouldUseCustomerPayment() {
        var $useCustomerPaymentCheckbox = $("[name='shouldUseCustomerPayment']");
        var shouldUseCustomerPayment = $useCustomerPaymentCheckbox.length > 0 && $useCustomerPaymentCheckbox.val() === "true";

        return shouldUseCustomerPayment;
    };

    /**
     * Submits the payment info form & advances to the next stage of the checkout flow.
     *
     * If a customer's saved payment is not being used, then we tokenize the provided credit card data
     *  and send the token along in the form. This assumes that we are given a multi-use token from
     *  the SamplePaymentService.
     */
    function savePaymentInfo($paymentStage) {
        var $paymentInfoForm = $paymentStage.find('.js-creditCardPaymentForm');

        if (!shouldUseCustomerPayment()) {
            var $paymentStageContent = $('.js-paymentStageContent');
            var $creditCardData = $paymentStageContent.find('.js-creditCardData');

            var paymentToken = SamplePaymentService.tokenizeCard($creditCardData);
            $paymentInfoForm.find('.js-paymentToken').val(paymentToken);
        }

        BLC.ajax({
            url: $paymentInfoForm.attr('action'),
            type: "POST",
            data: $paymentInfoForm.serialize()
        }, function(data) {
            clearWindowStateHistory();

            replaceCheckoutStages(data);
            showHiddenPerformCheckoutActions();
        });
    };

    /**
     * Submits the payment info form & advances to the next stage of the checkout flow.
     *
     * If the customer is anonymous or elects not to save their provided credit card, then this method should be used
     *  to simply submit the billing address data. This assumes that we would NOT be given a multi-use token from
     *  the SamplePaymentService, therefore, tokenizing the credit card data should be delayed until the final
     *  submission of the checkout flow.
     */
    function saveBillingAddressOnly($paymentStage) {
        var $paymentInfoForm = $paymentStage.find('.js-creditCardPaymentForm');

        BLC.ajax({
            url: $paymentInfoForm.attr('action') + '/billing',
            type: "POST",
            data: $paymentInfoForm.serialize()
        }, function(data) {
            clearWindowStateHistory();

            replaceCheckoutStages(data);
            showHiddenPerformCheckoutActions();
        });
    };

    function isPayPalConfigComplete() {
        return $('.js-payPalConfigLink').length === 0;
    }

    /**
     * Clicks a link that leads to a redirect into PayPal's ExpressCheckout using Broadleaf's PayPal integration module.
     *
     * Note: The link will not be present if there is already a third party payment associated with the order. If you wish
     *  to change this behavior, you'll need to override the `payPalPaymentMethodForm.html` template partial.
     */
    function collectPaymentInfoViaPayPal() {
        var $payPalPaymentMethodAction = $('.js-payPalPaymentMethodAction');

        if ($payPalPaymentMethodAction.length) {
            $payPalPaymentMethodAction[0].click();
        } else {
            Checkout.navigateToCheckoutStage('REVIEW');
        }
    };

    /**
     * Replaces the checkout stages partial with an updated version
     * @param {Element} $previousPaymentOptionForms
     */
    function copyPaymentStageDataToNewPartial($previousPaymentOptionForms) {
        var $newPaymentOptionForms = $('.js-checkoutStages').find('.js-paymentInfoForm');

        $newPaymentOptionForms.find('input:not([name=customerPaymentId], [name=useCustomerPayment]), select').each(function(i, el) {
            var value;
            if ($(el).is('select')) {
                value = $previousPaymentOptionForms.find("select[name='" + $(el).attr('name') + "']").val();
            } else {
                value = $previousPaymentOptionForms.find("input[name='" + $(el).attr('name') + "']").val();
            }
            $(el).val(value);

            if ($(el).hasClass('selectpicker')) {
                $('.selectpicker').selectpicker('refresh');
            }
        });

        var $newCustomerPaymentId = $newPaymentOptionForms.find('input[name=customerPaymentId]');

        if ($newCustomerPaymentId.val() === undefined || $newCustomerPaymentId.val().length == 0) {
            var previousValue = $previousPaymentOptionForms.find("input[name=customerPaymentId]").val();
            $newCustomerPaymentId.val(previousValue)
        }
    }

    /**
     * Gathers the specified payment method from the payment stage
     */
    function getPaymentMethod() {
        var $paymentStageContent = $('.js-paymentStageContent');

        return $paymentStageContent.find('.js-paymentMethodSelectors').find('li.active').data('value');
    }

    function paymentStageIsReadOnly() {
        var $readOnlyPaymentStageContent = $('.js-readOnlyPaymentStageContent');
        var $checkoutStage = $readOnlyPaymentStageContent.closest('.js-checkoutStage');

        return !$checkoutStage.hasClass('is-hidden') && !$readOnlyPaymentStageContent.hasClass('is-hidden');
    }

    function populateReadOnlyCreditCardView($paymentStageContent, $readOnlyPaymentStageContent) {
        var $creditCardForm = $paymentStageContent.find('.js-creditCardPaymentFormContainer');
        var creditCardNumber = $creditCardForm.find('#cardNumber').val();
        var cardType = SamplePaymentService.cardType(creditCardNumber);
        var lastFour = SamplePaymentService.lastFour(creditCardNumber);
        var expirationDate = SamplePaymentService.expirationDate($creditCardForm);

        $readOnlyPaymentStageContent.find('#' + cardType).removeClass('is-hidden');
        $readOnlyPaymentStageContent.find('.js-creditCardLastFour').text(lastFour);
        $readOnlyPaymentStageContent.find('.js-creditCardNumber').removeClass('is-hidden');
        $readOnlyPaymentStageContent.find('.js-creditCardExpDate').text(expirationDate);
        $readOnlyPaymentStageContent.find('.js-creditCardExpDate').removeClass('is-hidden');
    }

    function advanceFromPaymentToReviewStage() {
        $('.js-paymentStageContent').addClass('is-hidden');
        $('.js-readOnlyPaymentStageContent').removeClass('is-hidden');

        var $paymentStageCard = $('.js-paymentStageCard');
        var $checkoutStageCompleteIcon = $paymentStageCard.find('.js-checkoutStageCompleteIcon');
        var $editPaymentStageAction = $paymentStageCard.find('.js-editCheckoutStage');
        $checkoutStageCompleteIcon.removeClass('is-hidden');
        $editPaymentStageAction.removeClass('is-hidden');

        var $reviewStageContent = $('.js-reviewStageContent');
        $reviewStageContent.removeClass('is-hidden');

        showHiddenPerformCheckoutActions();
    }

    function showHiddenPerformCheckoutActions() {
        var $hiddenPerformCheckoutActions = $('.js-hiddenPerformCheckoutActions');
        $hiddenPerformCheckoutActions.removeClass('is-hidden');
    }

    function showReadOnlyPaymentMethod(paymentMethod) {
        $('.js-readOnlyPaymentMethod:not(.is-hidden)').addClass('is-hidden');
        $(".js-readOnlyPaymentMethod[data-type='" + paymentMethod + "']").removeClass('is-hidden');
    }

    function clearWindowStateHistory() {
        updateWindowStateHistory(null);
    }

    /**
     * Replaces the active checkout stage request param
     * @param {String} checkoutStage
     */
    function updateWindowStateHistory(checkoutStage) {
        var params = BLC.getUrlParameters();
        if (checkoutStage === undefined || checkoutStage ===null || checkoutStage.length === 0) {
            delete params['activeStage'];
        } else {
            params['activeStage'] = checkoutStage;
        }

        var urlWithParams = BLC.buildUrlWithParams('/checkout', params);

        window.history.replaceState(null, null, urlWithParams);
    }

})(window.Checkout = window.Checkout || {}, jQuery);
