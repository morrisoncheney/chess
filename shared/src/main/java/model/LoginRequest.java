package model;

public record LoginRequest(String username, String password) {
    public void check(){
        try {
            if (this.username().isEmpty() || this.password.isEmpty()) {
                throw new Exception("you can't see me");
            }
        } catch (Exception e) {
            throw new BadRequestException("bad request");
        }
    }

}
