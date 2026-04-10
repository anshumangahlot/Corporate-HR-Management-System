package exceptions;

/**
 * Custom Exception for Validation-related errors
 * Demonstrates exception handling for CO3
 */
public class ValidationException extends Exception {
    
    // RBR
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
