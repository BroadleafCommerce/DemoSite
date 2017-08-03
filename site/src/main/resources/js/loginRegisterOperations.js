/**
 * Start the listener on the login and register pages
 * TODO: Move to appropriate place
 */
cardResizeListener();
$(window).resize(cardResizeListener);


/**
 * Shows error if password and password confirm field don't match
 */
if(window.location.pathname === '/register') {
    var passwordField = $('#password');
    var passwordConfirmField = $('#passwordConfirm');
    var passwordMatchText = $('#confirm-password-text');

    passwordField.change(passwordValidationListener);
    passwordConfirmField.change(passwordValidationListener);
}