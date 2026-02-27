package model;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

public class MemoryGame {
    int gameIdNum;
    ChessGame game;
    String gameName;
    String whiteUsername;
    String blackUsername;

    public MemoryGame (int gameIdNum, ChessGame game, String whiteUsername, String blackUsername, String gameName){
        this.gameIdNum = gameIdNum;
        this.game = game;
        this.whiteUsername = whiteUsername;
        this.blackUsername = blackUsername;
        this.gameName = gameName;
    }

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

    public String toJsonString() {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("gameID", gameIdNum);
        jsonObject.addProperty("whiteUsername", whiteUsername);
        jsonObject.addProperty("blackUsername", blackUsername);
        jsonObject.addProperty("gameName", gameName);

        return new Gson().toJson(jsonObject);
    }

    public int gameIdNum() {
        return gameIdNum;
    }

    public ChessGame game() {
        return game;
    }

    public String whiteUsername() {
        return whiteUsername;
    }

    public String blackUsername() {
        return blackUsername;
    }

    public String gameName() {
        return gameName;
    }
}
