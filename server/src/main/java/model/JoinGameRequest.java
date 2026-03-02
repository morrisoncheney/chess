//package model;
//
//import chess.ChessGame;
//import com.google.gson.Gson;
//import org.jetbrains.annotations.NotNull;
//import server.BadRequestException;
//
//public class JoinGameRequest {
//    ChessGame.TeamColor playerColor;
//    Integer gameID;
//
//    JoinGameRequest (ChessGame.TeamColor col, Integer n) {
//        System.out.println("con 1");
//        this.playerColor = col;
//        this.gameID = n;
//    }
//
//    JoinGameRequest (String col, Integer n) {
//        System.out.println("con 2");
//        if (col.equals("WHITE")) {
//            this.playerColor = ChessGame.TeamColor.WHITE;
//        } else if (col.equals("BLACK")) {
//            this.playerColor = ChessGame.TeamColor.BLACK;
//        }
//
//        this.gameID = n;
//    }
//
//    public Integer gameID(){
//        return this.gameID;
//    }
//
//    public ChessGame.TeamColor color(){
//        return this.playerColor;
//    }
//
//    public void check(){
//        try {
//            if (this.playerColor == null || this.gameID == null ) {
//                System.out.println(this.playerColor);
//                System.out.println(this.gameID);
//                throw new Exception("you can't see me");
//            }
//        } catch (Exception e) {
//            throw new BadRequestException("bad request (inside join game request)");
//        }
//    }
//
//    @NotNull
//    public String toString() {
//        return new Gson().toJson(this);
//    }
//
//
//}

package model;

import chess.ChessGame;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import server.BadRequestException;

public record JoinGameRequest (ChessGame.TeamColor playerColor, Integer gameID){

    public void check(){
        try {
            if (this.playerColor == null || this.gameID == null ) {
                System.out.println(this.playerColor);
                System.out.println(this.gameID);
                throw new Exception("you can't see me");
            }
        } catch (Exception e) {
            throw new BadRequestException("bad request (inside join game request)");
        }
    }

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }


}

