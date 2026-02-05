package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board = new ChessBoard();
    TeamColor currentTurn;

    public ChessGame() {
        this.board.resetBoard();
        currentTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        if (currentTurn == TeamColor.WHITE) {
            currentTurn =  TeamColor.BLACK;
        } else {
            currentTurn =  TeamColor.WHITE;
        }
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece p = this.board.getPiece(startPosition);

        ArrayList<ChessMove> moves = new ArrayList<>();

        ArrayList<ChessMove> possibilities = new ArrayList<>();
        for (int r = 1; r < 9; r++) {
            for (int c = 1; c < 9; c++) {
                if (p != null && p.color == this.currentTurn) {
                    possibilities.addAll(p.pieceMoves(this.board, startPosition));
                } else {
                    return null;
                }



            }
        }

        // Iterate through each move
        for (ChessMove move: possibilities) {
            ChessBoard boardCopy = this.board;
            // pretend to execute the given move
            boardCopy = executeMove(boardCopy, move);
            // if the color who's moving's king does not end in check
            // add move
            // else
            // continue
        }
        return moves;
    }


    // create a function called checkForCheck(ChessBoard checkBoard){}
    // which returns the color of the king in check or null

    private TeamColor checkForCheck(ChessBoard checkBoard, ChessMove move) {

        return TeamColor.BLACK;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

    }

    /**
     *
     * @param currBoard
     * @param move
     * @return currBoard with move executed
     */
    private ChessBoard executeMove(ChessBoard currBoard, ChessMove move){
        ChessPiece piece = currBoard.getPiece(move.getStartPosition());
        currBoard.addPiece(move.getStartPosition(), null);
        if(move.getPromotionPiece() != null) {
            currBoard.addPiece(move.getEndPosition(), piece);
        } else {
            currBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        }
        return currBoard;
    }

    private boolean iterativeCheckFinder(ChessBoard checkBoard, TeamColor color){
        // Checks if the color king is in check.
        return false;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
//        throw new RuntimeException("Not implemented");
        ChessPiece currPiece;
        ArrayList<ChessMove> opMoves = new ArrayList<>();
        ChessPosition currPos;
        ChessPosition kingPos = null;
        for (int row = 1; row < 9; row += 1) {
            for (int col = 1; col < 9; col += 1) {
                currPos = new ChessPosition(row, col);
                currPiece = this.board.getPiece(currPos);
                if (currPiece != null) {
                    if (currPiece.getTeamColor() == teamColor && currPiece.getPieceType() == ChessPiece.PieceType.KING){
                        kingPos = currPos;
                    }
                    if (currPiece.getTeamColor() != teamColor){
                        opMoves.addAll(validMoves(currPos));
                    }
                }
            }
        }

        for (ChessMove move: opMoves){
            if (move.getEndPosition() == kingPos) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
