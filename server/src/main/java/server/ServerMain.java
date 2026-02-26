package server;

import server.Server;

public class ServerMain {
    public static void main(String[] args) {
        new Server().run(8080);
    }

}