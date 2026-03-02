package model;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public record CreateGameResult (Integer gameID){
    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

}
