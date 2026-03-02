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

