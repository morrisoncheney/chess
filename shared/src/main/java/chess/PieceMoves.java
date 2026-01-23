package chess;

import java.util.ArrayList;
import java.util.Collection;

public class PieceMoves {

    public static Collection<ChessMove> kingMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> kMoves = new ArrayList<ChessMove>();
        int[][] kingPossibleMoves = {{-1,0},{-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1},{-1,-1}};

        ChessPosition pNew;

        for (int[] xy : kingPossibleMoves) {

            int r = p.getRow() + xy[0];

            int c = p.getColumn() + xy[1];

            if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                pNew = new ChessPosition(r, c);
                ChessPiece inTheWay = board.getPiece(pNew);
                if (inTheWay == null) {
                    kMoves.add(new ChessMove(p,pNew,null));
                } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                    kMoves.add(new ChessMove(p,pNew,null));
                }
            }
        }

        return kMoves;
    }

    public static Collection<ChessMove> rookMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> rMoves = new ArrayList<ChessMove>();
        int[][] rookPossibleMoves = {{-1,0},{0,1},{1,0},{0,-1}};

        ChessPosition pNew;



        for (int[] xy : rookPossibleMoves) {
            boolean cont = true;
            pNew = p;
            while (cont) {

                int r = pNew.getRow() + xy[0];

                int c = pNew.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {

                    pNew = new ChessPosition(r,c);

                    ChessPiece inTheWay = board.getPiece(pNew);

                    if (inTheWay == null) {
                        rMoves.add(new ChessMove(p, pNew, null));

                    } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                        rMoves.add(new ChessMove(p, pNew, null));
                        cont = false;

                    } else {
                        cont = false;

                    }
                } else {
                    cont = false;
                }
            }
        }

        return rMoves;
    }

    public static Collection<ChessMove> bishopMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> bMoves = new ArrayList<ChessMove>();
        int[][] bishopPossibleMoves = {{-1,-1},{-1,1},{1,-1},{1,1}};

        ChessPosition pNew;



        for (int[] xy : bishopPossibleMoves) {
            boolean cont = true;
            pNew = p;
            while (cont) {

                int r = pNew.getRow() + xy[0];

                int c = pNew.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {

                    pNew = new ChessPosition(r,c);

                    ChessPiece inTheWay = board.getPiece(pNew);

                    if (inTheWay == null) {
                        bMoves.add(new ChessMove(p, pNew, null));

                    } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                        bMoves.add(new ChessMove(p, pNew, null));
                        cont = false;

                    } else {
                        cont = false;

                    }
                } else {
                    cont = false;
                }
            }
        }

        return bMoves;
    }

    public static Collection<ChessMove> pawnMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> pMoves = new ArrayList<ChessMove>();
        ChessPosition pNew;
        ChessPiece inTheWay;
        ChessGame.TeamColor color = board.getPiece(p).getTeamColor();
        boolean hasMoved;

        if (color == ChessGame.TeamColor.WHITE){ /*////////////////////////////////////////////////////////*/
            if (p.getRow() == 2) {
                hasMoved = false;
            } else {
                hasMoved = true;
            }

            pNew = new ChessPosition(p.getRow() + 1, p.getColumn());

            inTheWay = board.getPiece(pNew);

            boolean moveOne = false;
            if (inTheWay == null) {
                if (pNew.getRow() == 8){
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.QUEEN));
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.BISHOP));
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.ROOK));
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.KNIGHT));
                } else {
                    pMoves.add(new ChessMove(p,pNew, null));
                }
                moveOne = true;
            }

            if (!hasMoved) {
                pNew = new ChessPosition(p.getRow() + 2, p.getColumn());
                inTheWay = board.getPiece(pNew);
                if (inTheWay == null && moveOne) { pMoves.add(new ChessMove(p,pNew,null)); }
            }

            int[][] pawn_takes = {{1,1}, {1,-1}};

            for (int[] xy : pawn_takes) {
                int r = p.getRow() + xy[0];
                int c = p.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                    pNew = new ChessPosition(r, c);
                    inTheWay = board.getPiece(pNew);
                    if (inTheWay != null && inTheWay.getTeamColor() == ChessGame.TeamColor.BLACK){
                        if (pNew.getRow() == 8){
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.QUEEN));
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.BISHOP));
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.ROOK));
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.KNIGHT));
                        } else {
                            pMoves.add(new ChessMove(p,pNew, null));
                        }
                    }
                }
            }
        } else if (color == ChessGame.TeamColor.BLACK) { /*///////////////////////////////////////////////////////*/
            if (p.getRow() == 7) {
                hasMoved = false;
            } else {
                hasMoved = true;
            }

            pNew = new ChessPosition(p.getRow() - 1, p.getColumn());

            inTheWay = board.getPiece(pNew);

            boolean moveOne = false;
            if (inTheWay == null) {
                if (pNew.getRow() == 1){
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.QUEEN));
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.BISHOP));
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.ROOK));
                    pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.KNIGHT));
                } else {
                    pMoves.add(new ChessMove(p,pNew, null));
                }
                moveOne = true;
            }

            if (!hasMoved) {
                pNew = new ChessPosition(p.getRow() - 2, p.getColumn());
                inTheWay = board.getPiece(pNew);
                if (inTheWay == null && moveOne) { pMoves.add(new ChessMove(p,pNew,null)); }
            }

            int[][] pawnTakes = {{-1,1}, {-1,-1}};

            for (int[] xy : pawnTakes) {
                int r = p.getRow() + xy[0];
                int c = p.getColumn() + xy[1];

                if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                    pNew = new ChessPosition(r, c);
                    inTheWay = board.getPiece(pNew);
                    if (inTheWay != null && inTheWay.getTeamColor() == ChessGame.TeamColor.WHITE){
                        if (pNew.getRow() == 1){
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.QUEEN));
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.BISHOP));
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.ROOK));
                            pMoves.add(new ChessMove(p,pNew, ChessPiece.PieceType.KNIGHT));
                        } else {
                            pMoves.add(new ChessMove(p,pNew, null));
                        }
                    }
                }
            }
        }


        return pMoves;
    }

    public static Collection<ChessMove> knightMoves(ChessBoard board, ChessPosition p) {
        ArrayList<ChessMove> knMoves = new ArrayList<ChessMove>();
        int[][] knightPossibleMoves = {{-2,1},{-2,-1},{2,1},{2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};

        ChessPosition pNew;

        for (int[] xy : knightPossibleMoves) {

            int r = p.getRow() + xy[0];

            int c = p.getColumn() + xy[1];

            if (1 <= r && r <= 8 && 1 <= c && c <= 8) {
                pNew = new ChessPosition(r, c);
                ChessPiece inTheWay = board.getPiece(pNew);
                if (inTheWay == null) {
                    knMoves.add(new ChessMove(p,pNew,null));
                } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                    knMoves.add(new ChessMove(p,pNew,null));
                }
            }
        }

        return knMoves;
    }

}
