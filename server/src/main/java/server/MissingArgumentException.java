package server;

public class MissingArgumentException extends RuntimeException {
    public MissingArgumentException(String message) {
        super(message);
    }
    // this may not be necessary
    // see : https://github.com/softwareconstruction240/softwareconstruction/wiki/Web-API
    // it looks like a runtime exception with a custom message should work
}
