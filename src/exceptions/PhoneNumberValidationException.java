package exceptions;

/**
 * Custom exception for invalid phone number input.
 */
public class PhoneNumberValidationException extends ValidationException {

    // RBR
    public PhoneNumberValidationException(String message) {
        super(message);
    }

    public PhoneNumberValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}