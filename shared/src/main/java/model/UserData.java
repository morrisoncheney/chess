package model;

import com.google.gson.Gson;

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

    public String toString() {
        return new Gson().toJson(this);
    }
}
