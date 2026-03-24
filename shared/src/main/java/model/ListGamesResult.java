package model;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public record ListGamesResult (ArrayList<GameDataListItem> games){

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

}
