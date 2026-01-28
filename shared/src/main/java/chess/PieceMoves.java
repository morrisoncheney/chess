package chess;

import java.util.ArrayList;



public class PieceMoves {

    private static ArrayList<ChessMove> moveIterator(int[][] iter, ChessPosition p, ChessBoard board){
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition pNew;
        ChessPiece inTheWay;

        for (int[] xy : iter){
            int r = p.getRow() + xy[0];
            int c = p.getColumn() + xy[1];

            if (r >= 1 && r <= 8 && c >= 1 && c <=8){
                pNew = new ChessPosition(r,c);
                inTheWay = board.getPiece(pNew);
                if (inTheWay == null){
                    moves.add(new ChessMove(p,pNew, null));
                } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                    moves.add(new ChessMove(p,pNew, null));
                }
            }
        }

        return moves;
    }

    private static ArrayList<ChessMove> longMoveIterator(int[][] iter, ChessPosition p, ChessBoard board){
        ArrayList<ChessMove> moves = new ArrayList<>();

        ChessPosition pNew;
        ChessPiece inTheWay;
        boolean cont;

        for (int[] xy : iter){
            cont = true;
            pNew = p;

            while (cont){
                int r = pNew.getRow() + xy[0];
                int c = pNew.getColumn() + xy[1];

                if (r >= 1 && r <= 8 && c >= 1 && c <=8){
                    pNew = new ChessPosition(r,c);
                    inTheWay = board.getPiece(pNew);
                    if (inTheWay == null){
                        moves.add(new ChessMove(p,pNew, null));
                    } else if (inTheWay.getTeamColor() != board.getPiece(p).getTeamColor()) {
                        moves.add(new ChessMove(p,pNew, null));
                        cont = false;
                    } else {
                        cont = false;
                    }
                } else {
                    cont = false;
                }
            }

        }

        return moves;
    }

    public static ArrayList<ChessMove> kingMoves(ChessPosition p, ChessBoard board) {
        ArrayList<ChessMove> moves;

        int[][] king = {{-1,-1}, {-1,0}, {-1,1},{0,1},{1,1},{1,0},{1,-1},{0,-1}};

        moves = moveIterator(king, p, board);

        return moves;
    }

    public static ArrayList<ChessMove> knightMoves(ChessPosition p, ChessBoard board) {
        ArrayList<ChessMove> moves;

        int[][] knight = {{-2,1}, {-2,-1}, {1,2},{-1,2},{2,1},{2,-1},{1,-2},{-1,-2}};

        moves = moveIterator(knight, p, board);

        return moves;
    }

    public static ArrayList<ChessMove> bishopMoves(ChessPosition p, ChessBoard board) {
        ArrayList<ChessMove> moves;

        int[][] bishop = {{-1,-1}, {-1,1}, {1,1},{1,-1}};

        moves = longMoveIterator(bishop, p, board);

        return moves;
    }

    public static ArrayList<ChessMove> rookMoves(ChessPosition p, ChessBoard board) {
        ArrayList<ChessMove> moves;

        int[][] rook = {{-1,0}, {0,-1}, {1,0},{0,1}};

        moves = longMoveIterator(rook, p, board);

        return moves;
    }

    public static ArrayList<ChessMove> queenMoves(ChessPosition p, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();

        moves.addAll(rookMoves(p,board));
        moves.addAll(bishopMoves(p,board));


        return moves;
    }

    public static ArrayList<ChessMove> addAllPromos(ChessPosition p, ChessPosition pNew){
        ArrayList<ChessMove> moves = new ArrayList<>();

        moves.add(new ChessMove(p,pNew, ChessPiece.PieceType.QUEEN));
        moves.add(new ChessMove(p,pNew, ChessPiece.PieceType.BISHOP));
        moves.add(new ChessMove(p,pNew, ChessPiece.PieceType.ROOK));
        moves.add(new ChessMove(p,pNew, ChessPiece.PieceType.KNIGHT));

        return moves;
    }

    public static boolean rcCheck(int r, int c){
        return (r >= 1 && r <= 8 && c >= 1 && c <=8);
    }

    public static ArrayList<ChessMove> pawnMoves(ChessPosition p, ChessBoard board) {
        ArrayList<ChessMove> moves = new ArrayList<>();
        int dir;

        ChessGame.TeamColor color = board.getPiece(p).getTeamColor();

        if (color == ChessGame.TeamColor.WHITE) {
            dir = 1;
        } else { // color is black
            dir = -1;
        }

        ChessPosition pNew;
        ChessPiece inTheWay;
        boolean firstMove = false;

        pNew = new ChessPosition(p.getRow() + dir, p.getColumn());

        inTheWay = board.getPiece(pNew);

        if (inTheWay == null) {
            if (pNew.getRow() == 1 || pNew.getRow() == 8) {
                moves.addAll(addAllPromos(p,pNew));

            } else {
                moves.add(new ChessMove(p,pNew, null));
                if ((p.getRow() == 2 && color == ChessGame.TeamColor.WHITE) ||
                        (p.getRow() == 7 && color == ChessGame.TeamColor.BLACK) ) {
                    firstMove = true;
                }
            }

            if (firstMove) {
                pNew = new ChessPosition(p.getRow() + (dir * 2), p.getColumn());
                inTheWay = board.getPiece(pNew);
                if (inTheWay == null) {
                    moves.add(new ChessMove(p, pNew, null));
                }
            }
        }

        for (int col: new int[] {-1, 1}) {
            int r = p.getRow() + dir;
            int c = p.getColumn() + col;
            if (rcCheck(r,c)) {
                pNew = new ChessPosition(r, c);
                inTheWay = board.getPiece(pNew);
                if (inTheWay != null && inTheWay.getTeamColor() != color) {
                    if (pNew.getRow() == 1 || pNew.getRow() == 8) {
                        moves.addAll(addAllPromos(p, pNew));
                    } else {
                        moves.add(new ChessMove(p, pNew, null));
                    }
                }
            }
        }

        return moves;
    }

}
