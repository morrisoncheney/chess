package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoves {

    public static Collection<ChessMove> KingMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> k_moves = new ArrayList<ChessMove>();
        int[][] king_possible_moves = {{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};

        ChessPosition p_new;

        for (int[] xy : king_possible_moves) {

            int r = p.getRow() + xy[0];

            int c = p.getColumn() + xy[1];

            if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                p_new = new ChessPosition(r, c);
                ChessPiece inTheWay = board.getPiece(p_new);
                if (inTheWay == null) {
                    k_moves.add(new ChessMove(p,p_new,null));
                } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                    k_moves.add(new ChessMove(p,p_new,null));
                }
            }
        }

        return k_moves;
    }

    public static Collection<ChessMove> RookMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> r_moves = new ArrayList<ChessMove>();
        int[][] rook_possible_moves = {{-1,0},{0,1},{1,0},{0,-1}};

        ChessPosition p_new;



        for (int[] xy : rook_possible_moves ) {
            boolean cont = true;
            p_new = p;
            while (cont) {

                int r = p_new.getRow() + xy[0];

                int c = p_new.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {

                    p_new = new ChessPosition(r,c);

                    ChessPiece inTheWay = board.getPiece(p_new);

                    if (inTheWay == null) {
                        r_moves.add(new ChessMove(p, p_new, null));

                    } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                        r_moves.add(new ChessMove(p, p_new, null));
                        cont = false;

                    } else {
                        cont = false;

                    }
                } else {
                    cont = false;
                }
            }
        }

        return r_moves;
    }

    public static Collection<ChessMove> BishopMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> b_moves = new ArrayList<ChessMove>();
        int[][] bishop_possible_moves = {{-1,-1},{-1,1},{1,-1},{1,1}};

        ChessPosition p_new;



        for (int[] xy : bishop_possible_moves ) {
            boolean cont = true;
            p_new = p;
            while (cont) {

                int r = p_new.getRow() + xy[0];

                int c = p_new.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {

                    p_new = new ChessPosition(r,c);

                    ChessPiece inTheWay = board.getPiece(p_new);

                    if (inTheWay == null) {
                        b_moves.add(new ChessMove(p, p_new, null));

                    } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                        b_moves.add(new ChessMove(p, p_new, null));
                        cont = false;

                    } else {
                        cont = false;

                    }
                } else {
                    cont = false;
                }
            }
        }

        return b_moves;
    }

    public static Collection<ChessMove> PawnMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> p_moves = new ArrayList<ChessMove>();
        ChessPosition p_new;
        ChessPiece inTheWay;
        ChessGame.TeamColor color = board.getPiece(p).getTeamColor();
        boolean hasMoved;

        if (color == ChessGame.TeamColor.WHITE ){ /*////////////////////////////////////////////////////////////////////////////////*/
            if (p.getRow() == 2) {
                hasMoved = false;
            } else {
                hasMoved = true;
            }

            p_new = new ChessPosition(p.getRow() + 1, p.getColumn());

            inTheWay = board.getPiece(p_new);

            boolean moveOne = false;
            if (inTheWay == null) {
                if (p_new.getRow() == 8){
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.QUEEN));
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.BISHOP));
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.ROOK));
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.KNIGHT));
                } else {
                    p_moves.add(new ChessMove(p,p_new, null));
                }
                moveOne = true;
            }

            if (!hasMoved) {
                p_new = new ChessPosition(p.getRow() + 2, p.getColumn());
                inTheWay = board.getPiece(p_new);
                if (inTheWay == null && moveOne) { p_moves.add(new ChessMove(p,p_new,null)); }
            }

            int[][] pawn_takes = {{1,1}, {1,-1}};

            for (int[] xy : pawn_takes) {
                int r = p.getRow() + xy[0];
                int c = p.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                    p_new = new ChessPosition(r, c);
                    inTheWay = board.getPiece(p_new);
                    if (inTheWay != null && inTheWay.getTeamColor() == ChessGame.TeamColor.BLACK){
                        if (p_new.getRow() == 8){
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.QUEEN));
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.BISHOP));
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.ROOK));
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.KNIGHT));
                        } else {
                            p_moves.add(new ChessMove(p,p_new, null));
                        }
                    }
                }
            }
        } else if (color == ChessGame.TeamColor.BLACK) { /*////////////////////////////////////////////////////////////////////////////////*/
            if (p.getRow() == 7) {
                hasMoved = false;
            } else {
                hasMoved = true;
            }

            p_new = new ChessPosition(p.getRow() - 1, p.getColumn());

            inTheWay = board.getPiece(p_new);

            boolean moveOne = false;
            if (inTheWay == null) {
                if (p_new.getRow() == 1){
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.QUEEN));
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.BISHOP));
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.ROOK));
                    p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.KNIGHT));
                } else {
                    p_moves.add(new ChessMove(p,p_new, null));
                }
                moveOne = true;
            }

            if (!hasMoved) {
                p_new = new ChessPosition(p.getRow() - 2, p.getColumn());
                inTheWay = board.getPiece(p_new);
                if (inTheWay == null && moveOne) { p_moves.add(new ChessMove(p,p_new,null)); }
            }

            // If there is a non-same color piece on the {1,1} or {-1,1} (because white's Johnson increases) add possibility.
            int[][] pawn_takes = {{-1,1}, {-1,-1}};

            for (int[] xy : pawn_takes) {
                int r = p.getRow() + xy[0];
                int c = p.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                    p_new = new ChessPosition(r, c);
                    inTheWay = board.getPiece(p_new);
                    if (inTheWay != null && inTheWay.getTeamColor() == ChessGame.TeamColor.WHITE){
                        if (p_new.getRow() == 1){
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.QUEEN));
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.BISHOP));
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.ROOK));
                            p_moves.add(new ChessMove(p,p_new, ChessPiece.PieceType.KNIGHT));
                        } else {
                            p_moves.add(new ChessMove(p,p_new, null));
                        }
                    }
                }
            }
        }


        return p_moves;
    }

    public static Collection<ChessMove> KnightMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> kn_moves = new ArrayList<ChessMove>();
        int[][] knight_possible_moves = {{-2,1},{-2,-1},{2,1},{2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};

        ChessPosition p_new;

        for (int[] xy : knight_possible_moves) {

            int r = p.getRow() + xy[0];

            int c = p.getColumn() + xy[1];

            if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                p_new = new ChessPosition(r, c);
                ChessPiece inTheWay = board.getPiece(p_new);
                if (inTheWay == null) {
                    kn_moves.add(new ChessMove(p,p_new,null));
                } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                    kn_moves.add(new ChessMove(p,p_new,null));
                }
            }
        }

        return kn_moves;
    }

}
