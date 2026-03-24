package client;

import chess.ChessGame;

import java.util.Arrays;
import java.util.Scanner;

import exception.ResponseException;
import model.*;
import static ui.EscapeSequences.*;

public class Client {
    private ServerFacade server;
    private State state = State.SIGNEDOUT;

    private String userAuth;
    private Integer currGameID;
    private ChessGame.TeamColor userColor;
    private String activeUsername;

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
        System.out.print( ERASE_SCREEN + "\n" + SET_BG_COLOR_BLACK
                + SET_TEXT_COLOR_WHITE + "[" + state + "]>>> " + SET_TEXT_COLOR_BLUE);
    }


    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "login" -> signIn(params);
                case "register" -> register(params);
                case "create" -> create(params);
                case "list" -> listGames();
                case "play" -> join();
                case "logout" -> signOut();
                case "clear" -> clearDB();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String signIn(String... params) throws ResponseException {
        assertSignedOut("login");
        if (params.length == 2) {
            AuthData auth = server.login(params[0], params[1]);
            userAuth = auth.authToken();
            activeUsername = auth.username();
            // can we check response codes here somehow?
            state = State.SIGNEDIN;
            return String.format("Welcome back %s.\n", params[0]) + help();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password>");
    }

    public String register(String... params) throws ResponseException {
        assertSignedOut("register");
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];
            UserData user = new UserData(username, password, email);

            AuthData auth = server.registerUser(user);
            // can we check response codes here somehow?
            userAuth = auth.authToken();
            activeUsername = auth.username();

            state = State.SIGNEDIN;
            return String.format("Registration successful.\nWelcome to 240 Chess %s.\n", username) + help();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ListGamesResult games = server.listGames(userAuth);
        var result = new StringBuilder();

        for (GameDataListItem game : games.games()) {

            result.append(String.format("ID:%d Name:%s\n" +
                    "   White:%s Black:%s", game.gameID(), game.gameName(),game.whiteUsername(),game.blackUsername())).append("\n\n");
        }
        return result.toString();
    }

    public String create(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            String gameName = params[0];
            CreateGameResult gameData = server.create(gameName, userAuth);
            return String.format("Successfully created chess game [%s] at gameID [%d].", gameName, gameData.gameID());
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <game name>");
    }

    public String join(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 2) {
            server.join(Integer.valueOf(params[0]), ChessGame.TeamColor.valueOf(params[1]), userAuth);

            return "Successfully joined.";
        }
    }

    public String signOut() throws ResponseException {
        assertSignedIn();
        server.logout(userAuth);

        state = State.SIGNEDOUT;
        userAuth = null;
        String temp = activeUsername;
        activeUsername = null;

        return String.format("Bye %s.", temp);
    }

    public String clearDB() throws ResponseException {
        assertSignedIn();
        if (activeUsername == "mo") {
            state = State.SIGNEDOUT;
            server.clear();
            return "Bye bye everything.";
        } else {
            return "Judo Chop failed.";
        }
    }


    public String help() {
        if (state == State.SIGNEDOUT) {
            return """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - help
                    - quit
                    """;
        }
        return """
                - create <gameName>
                - list
                - play <gameID> <WHITE|BLACK>
                - observe <gameID>
                - logout
                - help
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(ResponseException.Code.ClientError, "You must sign in");
        }
    }

    private void assertSignedOut(String tryingTo) throws ResponseException {
        if (state == State.SIGNEDIN) {
            throw new ResponseException(ResponseException.Code.ClientError, String.format("You must sign out to %s.", tryingTo));
        }
    }
}
