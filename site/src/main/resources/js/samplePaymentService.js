(function(SamplePaymentService, $, undefined) {

    /**
     * This is where you would tokenize the card data gathered from the paymentForm.
     *
     * @param  {Object} creditCardForm
     * @return {String} credit card token
     */
    SamplePaymentService.tokenizeCard = function(creditCardForm) {
        // Use constant value regardless of form data so that we do not potentially save a legitimate credit card
        return '4111111111111111|Hotsauce Connoisseur|01/99|123'
    };

    /**
     * @param  {String} creditCardNumber
     * @return {String}
     */
    SamplePaymentService.cardType = function(creditCardNumber) {
        return 'MasterCard';
    };

    /**
     * @param  {String} creditCardNumber
     * @return {String}
     */
    SamplePaymentService.lastFour = function(creditCardNumber) {
        return creditCardNumber.substr(creditCardNumber.length - 4);
    };

    /**
     * @param  {Object} creditCardForm
     * @return {String}
     */
    SamplePaymentService.expirationDate = function(creditCardForm) {
        return '1/99';
    };

})(window.SamplePaymentService = window.SamplePaymentService || {}, jQuery);
