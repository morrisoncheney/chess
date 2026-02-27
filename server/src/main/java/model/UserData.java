package model;

import com.google.gson.*;
import server.BadRequestException;

import java.util.Objects;
//import org.jetbrains.annotations.NotNull;

public record UserData(String username, String password, String email) {

    public void check(){
        try {
            if (this.username().isEmpty() || this.password.isEmpty() || this.email().isEmpty()) {
                throw new Exception("you can't see me");
            }
        } catch (Exception e) {
            throw new BadRequestException("bad request");
        }
    }

//    @NotNull
    public String toString() {
        return new Gson().toJson(this);
    }
}
