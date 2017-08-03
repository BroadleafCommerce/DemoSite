(function(AccountManagement, $, undefined) {

    /// Public Methods

    /**
     * Replaces the content of the manage account form
     *
     * @param {HTML} newContent
     */
    AccountManagement.replaceManageAccountForm = function(newContent) {
        replaceAccountContent('js-manageAccountFormWrapper', newContent);
    };

    /**
     * Replaces the content of the manage account form
     *
     * @param {HTML} newContent
     */
    AccountManagement.replaceSavedPaymentCards = function(newContent) {
        replaceAccountContent('js-savedPaymentCards', newContent);
    };

    /// Private Methods

    /**
     * Replaces the content identified by the target class with the provided content
     *
     * @param {String} targetClass
     * @param {HTML} newContent
     */
    function replaceAccountContent(targetClass, newContent) {
        var $newContent = $(newContent);

        if (!$newContent.hasClass(targetClass)) {
            $newContent = $newContent.find('.' + targetClass);
        }

        $('.' + targetClass).replaceWith($newContent);
    }

})(window.AccountManagement = window.AccountManagement || {}, jQuery);