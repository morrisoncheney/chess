package model;

import chess.ChessGame;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;
import server.BadRequestException;

public record JoinGameRequest (ChessGame.TeamColor color, Integer gameID){
    public void check(){
        try {
            if (this.gameID == null ) {
                throw new Exception("you can't see me");
            }
        } catch (Exception e) {
            throw new BadRequestException("bad request");
        }
    }

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }


}
