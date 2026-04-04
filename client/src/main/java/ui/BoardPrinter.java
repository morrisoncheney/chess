package ui;

import chess.*;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
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

        drawChessBoard(out, board, colorInt, null);

    }

    public static void printChessBoardWithMoves(
            ChessBoard board, ChessGame.TeamColor color, Collection<ChessMove> moves, ChessPosition selectedPos
    ) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8); // needed to print together
        int colorInt;
        if (color == ChessGame.TeamColor.WHITE){
            colorInt = 0;
        } else {
            colorInt = 1;
        }

        drawChessBoard(out, board, colorInt, moves, selectedPos);
    }

    /**
     *
     * @param out where to print
     * @param board what to print
     * @param color who to print for (white : 0, black : 1)
     */
    private static void drawChessBoard(PrintStream out, ChessBoard board, int color, Collection<ChessMove> moves) {
        drawChessBoard(out, board, color, moves, null);
    }

    private static void drawChessBoard(PrintStream out, ChessBoard board, int color, Collection<ChessMove> moves, ChessPosition selectedPos) {
        if (moves == null) {
            out.print(ERASE_SCREEN);

            drawHorizontalLine(out, color);

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

                drawRow(out, boardRow, color, board, null, null);

            }
            drawHorizontalLine(out, color);
        } else {
            out.print(ERASE_SCREEN);

            drawHorizontalLine(out, color);

            for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

                drawRow(out, boardRow, color, board, moves, selectedPos);

            }
            drawHorizontalLine(out, color);
        }
    }

    /**
     *
     * @param out to print to
     * @param boardRow index
     * @param color number
     * @param board ChessBoard
     * @param moves Collection<PieceMove>
     * @param selectedPos ChessPosition
     */
    private static void drawRow(
            PrintStream out,
            int boardRow,
            int color,
            ChessBoard board,
            Collection<ChessMove> moves,
            ChessPosition selectedPos
    ) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {

            printVerticalBar(out, boardRow, squareRow, color);

            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
                ChessPosition p;
                if (color == 1) {
                    p = new ChessPosition(boardRow + 1, 8 - boardCol);
                } else {
                    p = new ChessPosition(8 - boardRow, boardCol + 1);
                }
                if (selectedPos != null && p.equals(selectedPos)) {
                    setYellow(out);
                } else if ((boardRow + boardCol) % 2 == 1) {
                    setLightGrey(out);
                    if (checkMoves(p, moves)) {
                        setDGreen(out);
                    }
                } else {
                    setWhite(out);
                    if (checkMoves(p, moves)) {
                        setGreen(out);
                    }
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPiece(out, board, p, color);
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

    private static boolean checkMoves(ChessPosition pos, Collection<ChessMove> moves) {
        if (moves == null) {
            return false;
        }
        for (ChessMove m : moves) {
            if (m.getEndPosition().equals(pos)) {
                return true;
            }
        }
        return false;
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setGreen(PrintStream out) {
        out.print(SET_BG_COLOR_GREEN);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setYellow(PrintStream out) {
        out.print(SET_BG_COLOR_YELLOW);
        out.print(SET_TEXT_COLOR_BLACK);
    }

    private static void setDGreen(PrintStream out) {
        out.print(SET_BG_COLOR_DARK_GREEN);
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

    private static void printPiece(PrintStream out, ChessBoard board, ChessPosition p, int color) {

        String piece;
        ChessPiece pP = board.getPiece(p);

        if (pP == null) {
            piece = EMPTY;
        } else if (pP.getPieceType() == ChessPiece.PieceType.KING) {
            if (pP.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_KING;
            } else {
                piece = WHITE_KING;
            }
        } else if (pP.getPieceType() == ChessPiece.PieceType.QUEEN) {
            if (pP.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_QUEEN;
            } else {
                piece = WHITE_QUEEN;
            }
        } else if (pP.getPieceType() == ChessPiece.PieceType.BISHOP) {
            if (pP.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_BISHOP;
            } else {
                piece = WHITE_BISHOP;
            }
        } else if (pP.getPieceType() == ChessPiece.PieceType.KNIGHT) {
            if (pP.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_KNIGHT;
            } else {
                piece = WHITE_KNIGHT;
            }
        } else if (pP.getPieceType() == ChessPiece.PieceType.ROOK) {
            if (pP.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_ROOK;
            } else {
                piece = WHITE_ROOK;
            }
        } else if (pP.getPieceType() == ChessPiece.PieceType.PAWN) {
            if (pP.getTeamColor() == ChessGame.TeamColor.BLACK) {
                piece = BLACK_PAWN;
            } else {
                piece = WHITE_PAWN;
            }
        } else {
            piece = EMPTY;
        }
        if (pP != null && pP.getTeamColor() == ChessGame.TeamColor.BLACK){ out.print(SET_TEXT_COLOR_BLACK); }
        out.print(piece);


    }
}