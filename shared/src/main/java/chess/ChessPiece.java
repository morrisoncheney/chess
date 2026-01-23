package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    PieceType type;
    ChessGame.TeamColor color;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.color = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<ChessMove>();

        ChessPiece piece = board.getPiece(myPosition);

        if (piece.type == PieceType.PAWN) {
            moves = PieceMoves.PawnMoves(board, myPosition);

        } else if (piece.type == PieceType.BISHOP) {
            moves = PieceMoves.BishopMoves(board, myPosition);

        } else if (piece.type == PieceType.ROOK) {
            moves = PieceMoves.RookMoves(board, myPosition);

        } else if (piece.type == PieceType.KNIGHT) {
            moves = PieceMoves.KnightMoves(board, myPosition);

        } else if (piece.type == PieceType.QUEEN) {
            moves.addAll(PieceMoves.RookMoves(board, myPosition));
            moves.addAll(PieceMoves.BishopMoves(board, myPosition));

        } else if (piece.type == PieceType.KING) {
            moves = PieceMoves.KingMoves(board, myPosition);
        }

        return moves;
    }


    // BISHOP: in 4 directions, using a while loop,
    // add 1 to r and c, and once you hit another object stop moving in that direction.

    // ROOK: Same as Bishop except different directions.

    // KNIGHT: knowing the shape, check the indexes of the possible positions,
    // and then add them to the list if they are empty.

    // KING: Go over every possible change in indexes. Create an array of arrays with x y cords.

    // Queen: BISHOP and ROOK



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "type=" + type +
                ", color=" + color +
                '}';
    }
}