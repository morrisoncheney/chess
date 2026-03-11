package dataaccess;

public class DataAccessException extends RuntimeException{
    public DataAccessException(String message, Exception ex) {
        super(message + ex.getMessage()); // Not sure what to do with ex
    }
}
