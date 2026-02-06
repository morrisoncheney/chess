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

    /**
     * Resets the board to input board
     *
     * @param board
     */
    public void set(ChessBoard board) {
        ChessPosition p;
        for (int row = 1; row < 9; row += 1) {
            for (int col = 1; col < 9; col += 1) {
                p = new ChessPosition(row,col);
                ChessPiece pc = board.getPiece(p);
                if (pc != null) {
                    addPiece(p, new ChessPiece(pc.getTeamColor(), pc.getPieceType()));
                }
            }
        }
    }

    private String getPieceChar(ChessPiece piece) {
        if (piece == null) {
            return " "; // Empty square
        }

        String letter = "";
        // Determine the letter based on the type
        switch (piece.getPieceType()) {
            case KING -> letter = "K";
            case QUEEN -> letter = "Q";
            case BISHOP -> letter = "B";
            case KNIGHT -> letter = "N";
            case ROOK -> letter = "R";
            case PAWN -> letter = "P";
        }

        // If it's Dr. Evil's team (Black), lower the case!
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            return letter.toLowerCase();
        } else {
            return letter; // White pieces stand tall and uppercase
        }
    }

    public void printBoard() {
        System.out.println("\n  -------------------------");
        // Start from Row 8 (top) down to Row 1 (bottom)
        for (int row = 8; row >= 1; row--) {
            System.out.print("\n"+row + " |"); // Print the row number
            for (int col = 1; col <= 9; col++) {
                // Wait! The loop should be 1 to 8, but let's be safe
                if (col > 8) break;

                ChessPiece piece = getPiece(new ChessPosition(row, col));
                System.out.print(getPieceChar(piece) + "|");
            }
//            System.out.println("\n");
        }
        System.out.println("-------------------------");
//        System.out.println("   a  b  c  d  e  f  g  h"); // Print column letters
    }

    public void clear(){
        for (int r = 1; r < 9; r +=1){
            for (int c = 1; c < 9; c +=1){
                addPiece(new ChessPosition(r,c), null);
            }
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
