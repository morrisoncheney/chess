package chess;

/**
 * Indicates an invalid move was made in a game
 */
public class InvalidMoveException extends Exception {

    public InvalidMoveException() {super("You attempted to make an un-shagadelic move punk.");}

    public InvalidMoveException(String message) {
        super(message);
    }
}
