package model;

import com.google.gson.Gson;
import org.jetbrains.annotations.NotNull;

public record AuthData (String username, String authToken) {
//    public AuthData {
//        if (username == null || password == null || email == null) {
//            throw new IllegalArgumentException("Username, password, and email cannot be null");
//        }
//    }

    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }

}
