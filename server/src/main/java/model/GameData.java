package model;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.javalin.http.ForbiddenResponse;
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

    public GameData updateGame(ChessGame newGame) {
        return new GameData(gameID, newGame, whiteUsername, blackUsername, gameName);
    }

    public GameData updatePlayer(String username, ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) {
            if (whiteUsername == null){ // I'm not exactly sure what this value will be if it's left empty
                return new GameData(gameID, game, username, blackUsername, gameName);
            }
        } else {
            if (whiteUsername == null){
                return new GameData(gameID, game, whiteUsername, username, gameName);
            }
        }

        throw new ForbiddenResponse(" already taken"); // Must have 403 code " already taken"
    }

}
