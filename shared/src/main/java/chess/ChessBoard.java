package chess;

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

    public ChessBoard() {

    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        private void row_iterator(ChessPiece piece, int[] rows){
            for (int n : rows){
                board.addPiece(ChessGame.TeamColor.WHITE, piece);
                board.addPiece(ChessGame.TeamColor.BLACK, piece);
            }
        }
        ChessPiece[][] board = new ChessPiece[8][8];
        int[] pawns = new int[] {0,1,2,3,4,5,6,7};
        for (int n : pawns){
            board[1][n] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        for (int n : pawns){
            board[7][n] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        int[] rooks = new int[] {0,7};
    }
    }
}

// create a chess piece class, and the board should be an array of this chess piece thing.