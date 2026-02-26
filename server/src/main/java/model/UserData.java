package model;

import com.google.gson.*;

import java.util.Objects;
//import org.jetbrains.annotations.NotNull;

public record UserData(String username, String password, String email) {

    public UserData {
        if (username == null || password == null || email == null) {
            throw new IllegalArgumentException("Username, password, and email cannot be null");
        }
    }

    public void check(){
        if (Objects.equals(this.username(), "") || Objects.equals(this.password(), "") || Objects.equals(this.email(), "")){
            throw new IllegalArgumentException("Username, password, and email cannot be null");
        }
    }

//    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }
}
