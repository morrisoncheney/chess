package client;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Arrays;
import java.util.Scanner;

import client.websocket.ClientNotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.*;
import static ui.EscapeSequences.*;
import ui.BoardPrinter;
import websocket.messages.ServerMessage;

public class Client {
    private ServerFacade server;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    private String userAuth;
    Integer currGameID = null;
    private ChessGame.TeamColor userColor;
    private String activeUsername;
    private ChessBoard genericChessBoard = new ChessBoard();
    private int maxGameID = 0; // get max game num and save it so observe won't work for that
    // should only accept ints

    public Client(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, new ClientNotificationHandler());
    }

    public void run() {
        System.out.println(SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + WHITE_KING +
                " 240 Chess Client. Sign in to start." + SET_TEXT_COLOR_BLACK + BLACK_KING
                + SET_TEXT_COLOR_WHITE);
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        String result = "";
        while (!result.equals("Bye bye.")) {
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
                case "play" -> join(params);
                case "observe" -> observe(params);
//                case "move" ->
//                case "resign" ->
//                case "taunt" ->
//                case "leave" ->
                case "logout" -> signOut();
                case "clear" -> clearDB();
                case "quit" -> "Bye bye.";
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
            return String.format("Registration successful.\n\nWelcome to 240 Chess %s.\n", username) + help();
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <username> <password> <email>");
    }

    public String listGames() throws ResponseException {
        assertSignedIn();
        ListGamesResult games = server.listGames(userAuth);
        var result = new StringBuilder();

        for (GameDataListItem game : games.games()) {
            String whiteUsername = game.whiteUsername();
            String blackUsername = game.blackUsername();
            result.append(String.format("ID[%d] Name[%s]\n" +
                    "   White[%s] Black[%s]", game.gameID(), game.gameName(), whiteUsername, blackUsername)).append("\n\n");
            if (game.gameID() > maxGameID) {
                maxGameID = game.gameID();
            }
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
            Integer gameID;
            try {
                gameID = Integer.valueOf(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(ResponseException.Code.ClientError, String.format("%s is an invalid gameID value.", params[0]));
            }
            if (gameID > maxGameID) {
                maxGameID = gameID;
            }
            ChessGame.TeamColor color;

            try {
                color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseException(ResponseException.Code.ClientError, String.format("Expected: <WHITE|BLACK>. %s is an invalid color value.", params[1]));
            }


            server.join(gameID, color, userAuth);
            ws.enter(userAuth, gameID, color);
            currGameID = gameID;
            genericChessBoard.resetBoard();
            BoardPrinter.printChessBoard(genericChessBoard, color);
            userColor = color;
            return "Successfully joined.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameID> <WHITE|BLACK>");
    }

    public String observe(String... params) throws ResponseException {
        assertSignedIn();
        if (params.length == 1) {
            Integer gameID;
            try {
                gameID = Integer.valueOf(params[0]);
            } catch (NumberFormatException e) {
                throw new ResponseException(ResponseException.Code.ClientError, String.format("%s is an invalid gameID value.", params[0]));
            }
            if (gameID > maxGameID || gameID < 1) {
                throw new ResponseException(ResponseException.Code.ClientError, "gameID too high");
            }

            ChessGame.TeamColor color = ChessGame.TeamColor.WHITE;

            genericChessBoard.resetBoard();
            BoardPrinter.printChessBoard(genericChessBoard, color);

            return "Successfully observed.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameID>");
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
        if (activeUsername.equals("mo")) {
            state = State.SIGNEDOUT;
            server.clear();
            return "Bye bye everything.";
        } else {
            return "Naughty naughty.\nThe international man of mystery frowns upon you.\n\n" +
                    "Yeah baby, yeah!\n";
        }
    }


    public String help() {
        if (state == State.SIGNEDOUT) {
            return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - help
                    - quit
                    """ + RESET_BG_COLOR + SET_TEXT_COLOR_BLACK;
        }
        return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + """
                - create <gameName>
                - list
                - play <gameID> <WHITE|BLACK>
                - observe <gameID>
                - logout
                - help
                - quit
                """ + RESET_BG_COLOR + SET_TEXT_COLOR_BLACK;
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
