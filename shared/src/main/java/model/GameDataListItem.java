package model;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public record GameDataListItem (int gameID, String whiteUsername, String blackUsername, String gameName){

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }



}
