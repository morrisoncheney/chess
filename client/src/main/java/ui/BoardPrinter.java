package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 1 ;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";

    /**
     *
     * @param board chessboard
     * @param color given user's color (ChessGame.TeamColor)
     */
    public static void printChessBoard(ChessBoard board, ChessGame.TeamColor color) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8); // needed to print together
        int colorInt;
        if (color == ChessGame.TeamColor.WHITE){
            colorInt = 0;
        } else {
            colorInt = 1;
        }

        drawChessBoard(out, board, colorInt);

    }

    /**
     *
     * @param out where to print
     * @param board what to print
     * @param color who to print for (white : 0, black : 1)
     */
    private static void drawChessBoard(PrintStream out, ChessBoard board, int color) {
        out.print(ERASE_SCREEN);

        drawHorizontalLine(out, color);

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRow(out, boardRow, color, board); // change the last number based on the color of the user

        }
        drawHorizontalLine(out, color);
    }

    /**
     *
     * @param out where to print to
     * @param boardRow index
     * @param color is 0 if white else black
     * @param board is a chess board
     */
    private static void drawRow(PrintStream out, int boardRow, int color, ChessBoard board) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {


            printVerticalBar(out, boardRow, squareRow, color);

            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {

                if ((boardRow + boardCol) % 2 == 1) { // just removed color thing (CODE QUALITY CHECK)
                    setLightGrey(out);

                } else {
                    setWhite(out);

                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPiece(out, board, boardRow, boardCol, color);
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
                    out.print(EscapeSequences.EMPTY);
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
                }

            }

            printVerticalBar(out, boardRow, squareRow, color);

            out.print(RESET_BG_COLOR);
            out.println();
        }
    }

    private static void printVerticalBar(PrintStream out, int boardRow, int squareRow, int color) {
        setBlack(out);
        boardRow++;
        int rowIndex;
        if (color == 0) {
            rowIndex = 9 - boardRow;
        } else {
            rowIndex = boardRow;
        }

        if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
            out.print(" " + rowIndex + " ");
        } else {
            out.print(EMPTY);
        }
    }
        private static void drawHorizontalLine(PrintStream out, int color) {

        ArrayList<String> letters = new ArrayList<>();
        letters.add("a");
        letters.add("b");
        letters.add("c");
        letters.add("d");
        letters.add("e");
        letters.add("f");
        letters.add("g");
        letters.add("h");

        if (color == 1) {
            Collections.reverse(letters);
        }

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setBlack(out);
            out.print(EMPTY);
            for (String l : letters) {
                int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                out.print(EMPTY.repeat(prefixLength));
                out.print(" " + l + " ");
                out.print(EMPTY.repeat(suffixLength));
            }
            out.print(EMPTY);
        }

        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setBlack(PrintStream out) {
        out.print(SET_BG_COLOR_BLACK);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void setLightGrey(PrintStream out) {
        out.print(SET_BG_COLOR_LIGHT_GREY);
        out.print(SET_TEXT_COLOR_WHITE);
    }

    private static void printPiece(PrintStream out, ChessBoard board, int boardRow, int col, int color) {

        String piece;
        ChessPiece p;

        if (color == 1) {
            p = board.getPiece(new ChessPosition(boardRow + 1, 8 - col));
        } else {
            p = board.getPiece(new ChessPosition(8 - boardRow, col + 1));
        }

        if (p == null) {
            piece = EMPTY;
        } else if (p.getPieceType() == ChessPiece.PieceType.KING) {
            if (p.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_KING;
            } else {
                piece = WHITE_KING;
            }
        } else if (p.getPieceType() == ChessPiece.PieceType.QUEEN) {
            if (p.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_QUEEN;
            } else {
                piece = WHITE_QUEEN;
            }
        } else if (p.getPieceType() == ChessPiece.PieceType.BISHOP) {
            if (p.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_BISHOP;
            } else {
                piece = WHITE_BISHOP;
            }
        } else if (p.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            if (p.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_KNIGHT;
            } else {
                piece = WHITE_KNIGHT;
            }
        } else if (p.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (p.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_ROOK;
            } else {
                piece = WHITE_ROOK;
            }
        } else if (p.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (p.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_PAWN;
            } else {
                piece = WHITE_PAWN;
            }
        } else {
            piece = EMPTY;
        }
        if (p != null && p.getTeamColor() == ChessGame.TeamColor.BLACK){ out.print(SET_TEXT_COLOR_BLACK); }
        out.print(piece);


    }
}