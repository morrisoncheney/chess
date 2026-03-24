package model;

public record CreateGameRequest(String gameName) {

    public void check(){
        try {
            if (this.gameName.isEmpty() ) {
                throw new Exception("you can't see me");
            }
        } catch (Exception e) {
            throw new BadRequestException("bad request");
        }
    }

}
