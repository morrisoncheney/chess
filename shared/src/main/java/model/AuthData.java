package model;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public record AuthData (String username, String authToken) {

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

}
