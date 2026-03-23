package client;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.Scanner;

import java.util.ArrayList;
import exception.ResponseException;
import model.*;
import static ui.EscapeSequences.*;

public class Client {
    private ServerFacade server;
    private State state = State.SIGNEDOUT;

    private String userAuth;

    public Client(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + WHITE_KING +
                " 240 Chess ClientMain. Sign in to start." + SET_TEXT_COLOR_BLACK + BLACK_KING
                + SET_TEXT_COLOR_WHITE);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    private void printPrompt() {
        System.out.print( ERASE_SCREEN + "\n" + SET_BG_COLOR_BLACK + "[" + state + "]>>> " + SET_TEXT_COLOR_BLUE);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> signIn(params);
                case "register" -> register(params);
//                case "create" -> adoptPet(params);
                case "list" -> listGames();
//                case "join" -> adoptAllPets();
//                case "logout" -> signOut();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String signIn(String... params) throws ResponseException {
        if (params.length == 2) {
            AuthData auth = server.login(params[0], params[1]);
            userAuth = auth.authToken();
            // can we check response codes here somehow?
            state = State.SIGNEDIN;
            return String.format("You signed in as %s.", params[0]);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);

            AuthData auth = server.registerUser(user);
            // can we check response codes here somehow?
            userAuth = auth.authToken();
            state = State.SIGNEDIN;
            return String.format("Registration successful.\nYou signed in as %s.", username);
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ListGamesResult games = server.listGames();
        var result = new StringBuilder();
        var gson = new Gson();

        for (Object game : gson.fromJson(String.valueOf(games.games()), ArrayList.class)) {
            result.append(gson.toJson(game)).append('\n');
        }
        return result.toString();
    }

//    public String adoptPet(String... params) throws ResponseException {
//        assertSignedIn();
//        if (params.length == 1) {
//            try {
//                int id = Integer.parseInt(params[0]);
//                Pet pet = getPet(id);
//                if (pet != null) {
//                    server.logout();
//                    return String.format("%s says %s", pet.name(), pet.sound());
//                }
//            } catch (NumberFormatException ignored) {
//            }
//        }
//        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <pet id>");
//    }

//    public String adoptAllPets() throws ResponseException {
//        assertSignedIn();
//        var buffer = new StringBuilder();
//        for (Pet pet : server.listGames()) {
//            buffer.append(String.format("%s says %s%n", pet.name(), pet.sound()));
//        }
//
//        server.clear();
//        return buffer.toString();
//    }
//
//    public String signOut() throws ResponseException {
//        assertSignedIn();
//        state = State.SIGNEDOUT;
//        return String.format("%s left the shop", visitorName);
//    }

//    private Pet getPet(int id) throws ResponseException {
//        for (Pet pet : server.listGames()) {
//            if (pet.id() == id) {
//                return pet;
//            }
//        }
//        return null;
//    }

    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - quit
                    """;
        }
        return """
                - create <gameName>
                - list
                - join <gameID>
                - logout
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }
}
