package exceptions;

/**
 * Custom Exception for Authentication-related errors
 * Demonstrates exception handling for CO3
 */
public class AuthenticationException extends Exception {
    
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
