package model;

import chess.ChessGame;
import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public record GameData (int gameID, ChessGame game, String whiteUsername, String blackUsername, String gameName){

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }


}
