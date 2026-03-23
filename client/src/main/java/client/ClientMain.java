package client;

import exception.ResponseException;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ClientMain {
    private static Client client;
    static void main(String[] args) throws ResponseException {// http://localhost:8080/
        PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);

        out.println( "240 Chess ClientMain Initializing...\n");
        client = new Client("http://localhost:8080");
        client.run();
    }
}
