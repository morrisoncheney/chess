package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] board;

    public ChessBoard() {
        this.board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        this.board[position.row][position.col] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return this.board[position.row][position.col];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */

    private void rowIterator(int[] cols, ChessPiece.PieceType type){
        for (int c: cols){ // add at 1 and 8
            addPiece(new ChessPosition(1,c), new ChessPiece(ChessGame.TeamColor.WHITE, type));
            addPiece(new ChessPosition(8,c), new ChessPiece(ChessGame.TeamColor.BLACK, type));
        }
    }

    public void resetBoard() {
        int[] pawns = {1,2,3,4,5,6,7,8};

        for (int c : pawns){
            addPiece(new ChessPosition(2,c), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
            addPiece(new ChessPosition(7,c), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        int[] rooks = {1,8};
        rowIterator(rooks, ChessPiece.PieceType.ROOK);

        int[] knights = {2,7};
        rowIterator(knights, ChessPiece.PieceType.KNIGHT);


        int[] bishops = {3,6};
        rowIterator(bishops, ChessPiece.PieceType.BISHOP);

        int[] queens = {4};
        rowIterator(queens, ChessPiece.PieceType.QUEEN);

        int[] kings = {5};
        rowIterator(kings, ChessPiece.PieceType.KING);

    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        return "ChessBoard{" +
                "board=" + Arrays.toString(board) +
                '}';
    }
}
