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

//    public static void main (String[] args){
//        ChessBoard board = new ChessBoard();
//        board.addPiece();
//    }
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
    public void resetBoard() {

        ChessPiece[][] board = new ChessPiece[8][8];
        int[] pawns = new int[] {1,2,3,4,5,6,7,8};

        for (int n : pawns) {
                addPiece(new ChessPosition(2, n), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
                addPiece(new ChessPosition(7, n), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        int[] rooks = new int[] {1,8};
        row_iterator(ChessPiece.PieceType.ROOK, rooks);

        int[] bishops = new int[] {3,6};
        row_iterator(ChessPiece.PieceType.BISHOP, bishops);

        int[] knights = new int[] {2,7};
        row_iterator(ChessPiece.PieceType.KNIGHT, knights);

        int[] kings = new int[] {5};
        row_iterator(ChessPiece.PieceType.KING, kings);

        int[] queens = new int[] {4};
        row_iterator(ChessPiece.PieceType.QUEEN, queens);
    }

    /**
     * iterates through rows on front and back rank to add pieces.
     *
     * @param type The ChessPiece.PieceType of your pieces.
     * @param cols An integer array of the column indexes to add them to.
     */
    private void row_iterator(ChessPiece.PieceType type, int[] cols){
        for (int n : cols) {
                addPiece(new ChessPosition(1, n), new ChessPiece(ChessGame.TeamColor.WHITE, type));
                addPiece(new ChessPosition(8, n), new ChessPiece(ChessGame.TeamColor.BLACK, type));
        }
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


// create a chess piece class, and the board should be an array of this chess piece thing.