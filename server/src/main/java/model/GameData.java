package model;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public record GameData (int gameID, ChessGame game, String whiteUsername, String blackUsername, String gameName){

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

    public String toJsonString() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("gameID", gameID);
        jsonObject.addProperty("whiteUsername", whiteUsername);
        jsonObject.addProperty("blackUsername", blackUsername);
        jsonObject.addProperty("gameName", gameName);

        return new Gson().toJson(jsonObject);
    }

    public GameData update(ChessGame newGame) {
        return new GameData(gameID, newGame, whiteUsername, blackUsername, gameName);
    }

}
