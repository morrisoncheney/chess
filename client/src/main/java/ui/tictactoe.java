package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

import static ui.EscapeSequences.*;

public class tictactoe {

    // Board dimensions.
    private static final int BOARD_SIZE_IN_SQUARES = 8;
    private static final int SQUARE_SIZE_IN_PADDED_CHARS = 3;
    private static final int LINE_WIDTH_IN_PADDED_CHARS = 1;

    // Padded characters.
    private static final String EMPTY = "   ";

    private static Random rand = new Random();


    public static void main(String[] args) {
        var out = new PrintStream(System.out, true, StandardCharsets.UTF_8); // needed to print together

        out.print(ERASE_SCREEN);

        drawChessBoard(out);

    }

    private static void drawChessBoard(PrintStream out) {
        drawHorizontalLine(out);

        for (int boardRow = 0; boardRow < BOARD_SIZE_IN_SQUARES; ++boardRow) {

            drawRowOfSquares(out, boardRow);

        }
        drawHorizontalLine(out);
    }

    private static void drawRowOfSquares(PrintStream out, int boardRow) {

        for (int squareRow = 0; squareRow < SQUARE_SIZE_IN_PADDED_CHARS; ++squareRow) {
            setBlack(out);
            out.print(EMPTY);
            for (int boardCol = 0; boardCol < BOARD_SIZE_IN_SQUARES; ++boardCol) {
//                out.print(SET_TEXT_COLOR_BLACK);
                if ((boardRow + boardCol) % 2 == 0) {
                    setLightGrey(out);
//                    out.print(SET_TEXT_COLOR_WHITE);
                } else {
                    setWhite(out);
//                    out.print(SET_TEXT_COLOR_BLACK);
                }

                if (squareRow == SQUARE_SIZE_IN_PADDED_CHARS / 2) {
                    int prefixLength = SQUARE_SIZE_IN_PADDED_CHARS / 2;
                    int suffixLength = SQUARE_SIZE_IN_PADDED_CHARS - prefixLength - 1;

                    out.print(EMPTY.repeat(prefixLength));
                    printPlayer(out, rand.nextBoolean() ? SET_TEXT_COLOR_BLACK + BLACK_PAWN : WHITE_PAWN);
                    out.print(EMPTY.repeat(suffixLength));
                }
                else {
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
                    out.print(EscapeSequences.EMPTY);
                    out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
                }

            }
            setBlack(out);
            out.print(EMPTY);
            out.print(RESET_BG_COLOR);
            out.println();
        }
    }

    private static void drawHorizontalLine(PrintStream out) {

        int boardSize = 8;

        for (int lineRow = 0; lineRow < LINE_WIDTH_IN_PADDED_CHARS; ++lineRow) {
            setBlack(out);
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
            out.print(EscapeSequences.EMPTY);
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
            for (int col = 0; col < boardSize; ++col) {
                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
                out.print(" " + 1 + " ");
//                out.print(1);
                out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
            }
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
            out.print(EscapeSequences.EMPTY);
            out.print(EMPTY.repeat(SQUARE_SIZE_IN_PADDED_CHARS / 2));
        }





        out.print(RESET_BG_COLOR);
        out.println();
    }

    private static void setWhite(PrintStream out) {
        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_BG_COLOR_LIGHT_GREY);
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

    private static void printPlayer(PrintStream out, String player) {
//        out.print(SET_BG_COLOR_WHITE);
//        out.print(SET_TEXT_COLOR_BLACK);

        out.print(player);

//        setWhite(out);
    }
}