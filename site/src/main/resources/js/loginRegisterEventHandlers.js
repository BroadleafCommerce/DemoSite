/**
 * Shows error if password and password confirm field don't match
 */
var passwordValidationListener = function (e) {
    passwordField.parent().removeClass('has-error has-success');
    passwordConfirmField.parent().removeClass('has-error has-success');
    passwordField.siblings('span.material-icons').html('');
    passwordConfirmField.siblings('span.material-icons').html('');

    if (passwordConfirmField.val().length === 0) {
    }
    else if (passwordField.val() !== passwordConfirmField.val()) {
        passwordField.parent().addClass('has-error');
        passwordConfirmField.parent().addClass('has-error');
        passwordField.siblings('span.material-icons').html('clear');
        passwordConfirmField.siblings('span.material-icons').html('clear');
        passwordMatchText.css('visibility', 'visible');
    }
    else {
        passwordField.parent().addClass('has-success');
        passwordConfirmField.parent().addClass('has-success');
        passwordField.siblings('span.material-icons').html('done');
        passwordConfirmField.siblings('span.material-icons').html('done');
        passwordMatchText.css('visibility', 'hidden');
    }
};

/**
 * Adds and removes the card around the login and registration pages on larger screens
 * TODO: Move to appropriate place
 */
var cardResizeListener = function (e) {
    var signupCards = $('.js-responsive-card-signup');
    var cards = $('.js-responsive-card');

    if($(window).width() >= 768 && !cards.hasClass('card'))
    {
        cards.addClass('card')
            .css('padding', '25px');
        signupCards.addClass('card-signup');
    }
    else if($(window).width() < 768)
    {
        cards.removeClass('card')
            .css('padding', '');
        signupCards.removeClass('card-signup');
    }
};