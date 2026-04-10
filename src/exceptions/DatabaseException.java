package exceptions;

/**
 * Custom Exception for Database-related errors
 * Demonstrates exception handling for CO3
 */
public class DatabaseException extends Exception {
    
    // RBR
    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
