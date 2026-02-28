package model;

import server.BadRequestException;

public record LogoutRequest(String authToken) {
    public void check(){
        try {
            if (this.authToken().isEmpty()) {
                throw new Exception("you can't see me");
            }
        } catch (Exception e) {
            throw new BadRequestException("bad request");
        }
    }

}
