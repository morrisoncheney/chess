package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

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
//        if (currentTurn == TeamColor.WHITE) {
//            currentTurn =  TeamColor.BLACK;
//        } else {
//            currentTurn =  TeamColor.WHITE;
//        }
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
//        System.out.println("[DEBUG] validMoves called for " + startPosition); // DEBUG

        ChessPiece p = this.board.getPiece(startPosition);
        if (p == null) {
//            System.out.println("[DEBUG] No piece found at " + startPosition); // DEBUG
            return null;
        }
//        System.out.println("[DEBUG] Piece is: " + p.getPieceType() + " (" + p.getTeamColor() + ")"); // DEBUG

        ArrayList<ChessMove> moves = new ArrayList<>();
        Collection<ChessMove> pMoves = p.pieceMoves(this.board, startPosition);

//        System.out.println("[DEBUG] Raw pieceMoves found: " + pMoves.size()); // DEBUG

//        this.board.printBoard();

        for (ChessMove move: pMoves) {
            // System.out.println("[DEBUG] Testing move validity: " + move); // Optional: can be noisy
            ChessBoard tempBoard = new ChessBoard();
            tempBoard.set(this.board);
            executeMove(tempBoard, move);
//            tempBoard.printBoard();
            boolean checkFound = iterativeCheckFinder(tempBoard, p.getTeamColor());
            if (!checkFound){
                // System.out.println("[DEBUG] Move VALID: " + move); // Optional
                moves.add(move);
            } else {
//                System.out.println("[DEBUG] Move BLOCKED by check: " + move); // DEBUG
            }
        }
//        System.out.println("[DEBUG] Total valid moves returning: " + moves.size()); // DEBUG
        return moves;
    }

    public void makeMove(ChessMove move) throws InvalidMoveException {
//        System.out.println("\n[DEBUG] --- makeMove called ---"); // DEBUG
//        System.out.println("[DEBUG] Attempting to move: " + move); // DEBUG

        ChessPiece p = this.board.getPiece(move.getStartPosition());
        if (p == null) {
//            System.out.println("[DEBUG] ERROR: No piece at start position"); // DEBUG
            throw new InvalidMoveException("You cannot move a nonexistent piece.");
        } else if (p.getTeamColor() != currentTurn) {
            throw new InvalidMoveException("It is not this team's turn.");
        }

        TeamColor c = p.getTeamColor();
        // Verify turn order here if you have access to game state
        // System.out.println("[DEBUG] Piece color: " + c);

        ChessBoard tempBoard = new ChessBoard();
        tempBoard.set(this.board);
        tempBoard = executeMove(tempBoard, move);

        // DEBUG: Checking if move is in validMoves
//        System.out.println("[DEBUG] Verifying move against validMoves list..."); // DEBUG
        Collection<ChessMove> valid = validMoves(move.getStartPosition());

        if (valid.contains(move)){
//            System.out.println("[DEBUG] Move confirmed valid. Updating board."); // DEBUG
            this.board = tempBoard;
        } else {
//            System.out.println("[DEBUG] ERROR: Move not in validMoves list!"); // DEBUG
//            System.out.println("[DEBUG] Valid moves were: " + valid); // DEBUG
            throw new InvalidMoveException("Invalid move attempted.");
        }
        if (p.getTeamColor() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        } else {
            setTeamTurn(TeamColor.WHITE);
        }
    }

    private ChessBoard executeMove(ChessBoard currBoard, ChessMove move){
        // System.out.println("[DEBUG] Executing move on board state..."); // DEBUG
        ChessPiece piece = currBoard.getPiece(move.getStartPosition());
        currBoard.addPiece(move.getStartPosition(), null);

        if(move.getPromotionPiece() != null) {
            // System.out.println("[DEBUG] PROMOTION detected to " + move.getPromotionPiece()); // DEBUG
            currBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
        } else {
            currBoard.addPiece(move.getEndPosition(), piece);
        }
        return currBoard;
    }

    private boolean iterativeCheckFinder(ChessBoard checkBoard, TeamColor color){
        // System.out.println("[DEBUG] CheckFinder checking " + color); // DEBUG
        ChessPiece currPiece;
        ArrayList<ChessMove> opMoves = new ArrayList<>();
        ChessPosition currPos;
        ChessPosition kingPos = null;

        // Scan board
        for (int row = 1; row < 9; row += 1) {
//            System.out.println("{DEBUG} r:" + row);
            for (int col = 1; col < 9; col += 1) {
//                System.out.println("{DEBUG} c:" + col);
                currPos = new ChessPosition(row, col);
//                System.out.println("{DEBUG} current position:" + currPos);
                currPiece = checkBoard.getPiece(currPos);
//                System.out.println("{DEBUG} current piece:" + currPiece);
                if (currPiece != null) {
                    if (currPiece.getTeamColor() == color && currPiece.getPieceType() == ChessPiece.PieceType.KING){
                        kingPos = new ChessPosition(currPos.getRow(), currPos.getColumn());
                    } else if (currPiece.getTeamColor() != color){
                        opMoves.addAll(currPiece.pieceMoves(checkBoard, currPos));
                    }
                }
            }
        }

        if (kingPos == null) {
//            System.out.println("[DEBUG] CRITICAL: King not found for " + color); // DEBUG
            return false;
        }
//        System.out.println("{DEBUG} King Position: " + kingPos);
        for (ChessMove move: opMoves){
//            System.out.println("{DEBUG} Testing: " + move);
            if (move.getEndPosition().equals(kingPos)) {
//                System.out.println("[DEBUG] CHECK DETECTED! King at " + kingPos + " threatened by " + move.getStartPosition()); // DEBUG
                return true;
            }
        }
        return false;
    }

    private Collection<ChessMove> allValidMoves(TeamColor color){
        ChessPiece p;
        ChessPosition pos;
        ArrayList<ChessMove> moves = new ArrayList<>();
        for (int r = 1; r < 9; r +=1){
            for (int c = 1; c < 9; c +=1){
                pos = new ChessPosition(r,c);
                p =  this.board.getPiece(pos);
                if (p != null && p.getTeamColor() == color){
                    moves.addAll(validMoves(pos));
                }
            }
        }
        return moves;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return iterativeCheckFinder(this.board, teamColor);
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (allValidMoves(teamColor).isEmpty() && iterativeCheckFinder(this.board, teamColor)) return true;
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (allValidMoves(teamColor).isEmpty() && !iterativeCheckFinder(this.board, teamColor)) return true;
        return false;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board.clear();
        this.board.set(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && currentTurn == chessGame.currentTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, currentTurn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "board=" + board +
                ", currentTurn=" + currentTurn +
                '}';
    }
}
