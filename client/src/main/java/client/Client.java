package client;

import chess.ChessBoard;
import chess.ChessGame;

import java.util.Arrays;
import java.util.Scanner;

import chess.ChessPosition;
import client.websocket.NotificationHandler;
import client.websocket.WebSocketFacade;
import exception.ResponseException;
import model.*;
import static ui.EscapeSequences.*;
import ui.BoardPrinter;
import websocket.messages.ServerMessage;

public class Client implements NotificationHandler {
    private ServerFacade server;
    private WebSocketFacade ws;
    private State state = State.SIGNEDOUT;

    private String userAuth;
    Integer currGameID = null;
    private ChessGame.TeamColor userColor;
    private String activeUsername;
    private ChessBoard currBoard;
    private int maxGameID = 0; // get max game num and save it so observe won't work for that
    // should only accept ints

    public Client(String serverUrl) throws ResponseException {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
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

    private void printPrompt() { //
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
                case "join" -> join(params);
                case "observe" -> observe(params);
//                case "move" -> move(params);
//                case "redraw" -> redraw(params);
//                case "resign" -> resign(params);
//                case "see_moves" -> seeMoves(params);
//                case "leave" -> leaveGame(params);
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
            ws.enter(userAuth, gameID);
            currGameID = gameID;
            userColor = color;
            state = State.IN_GAME;
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
            ws.enter(userAuth, gameID);

            state = State.OBSERVING;

            return "Successfully observed.";
        }
        throw new ResponseException(ResponseException.Code.ClientError, "Expected: <gameID>");
    }

    public String move(String... empty) throws ResponseException {
        assertInGame("make a move");

        // add a check to make sure empty is actually empty
        System.out.print("From: ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        String[] tokens = line.toLowerCase().split(" ");
        String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (params.length > 1) {
            throw new ResponseException(ResponseException.Code.ClientError, "expected <row letter><col number>");
        }
        String startingSquare = params[0];

        //convert startingSquare to a ChessPosition
        ChessPosition startPos = parsePosition(startingSquare);

        // get the piece at the beginning square

        System.out.print("To: ");
        line = scanner.nextLine();

        tokens = line.toLowerCase().split(" ");
        params = Arrays.copyOfRange(tokens, 1, tokens.length);

        if (params.length > 1) {
            throw new ResponseException(ResponseException.Code.ClientError, "expected <row letter><col number>");
        }
        String endSquare = params[0];

        //convert endSquare to a ChessPosition
        ChessPosition endPos = parsePosition(endSquare);

        // if pawn and finish square is back row
        // get promo piece

        // submit ws command

        // adjust the ws so that this user prints the move here
        // and ws sends it to everyone but this user

        return "";
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
        String observing = SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + """
                - see_moves
                - leave
                - logout
                - help
                - quit
                """ + RESET_BG_COLOR + SET_TEXT_COLOR_BLACK;
        if (state == State.SIGNEDOUT) {
            return SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_WHITE + """
                    - register <username> <password> <email>
                    - login <username> <password>
                    - help
                    - quit
                    """ + RESET_BG_COLOR + SET_TEXT_COLOR_BLACK;
        } else if (state == State.IN_GAME) {
            return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + """
                - see_moves
                - move
                """ + observing;
        } else if (state == State.OBSERVING) {
            return observing;
        }
        return SET_BG_COLOR_DARK_GREY + SET_TEXT_COLOR_WHITE + """
                - create <gameName>
                - list
                - join <gameID> <WHITE|BLACK>
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
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    String.format("You must sign out to %s.", tryingTo)
            );
        }
    }

    private void assertInGame(String tryingTo) throws ResponseException {
        if (state == State.IN_GAME) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    String.format("You must sign out to %s.", tryingTo)
            );
        }
    }

    private void assertInGameOrObserving(String tryingTo) throws ResponseException {
        if (state == State.IN_GAME || state == State.OBSERVING) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    String.format("You must be in a game to %s.", tryingTo)
            );
        }
    }

    public static ChessPosition parsePosition(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position: \"" + pos + "\"");
        }

        char file = Character.toLowerCase(pos.charAt(0));
        char rank = pos.charAt(1);

        if (file < 'a' || file > 'h') {
            throw new IllegalArgumentException("Invalid file: '" + file + "' (must be a–h)");
        }
        if (rank < '1' || rank > '8') {
            throw new IllegalArgumentException("Invalid rank: '" + rank + "' (must be 1–8)");
        }

        int col = file - 'a' + 1; // a=1, b=2, ..., h=8
        int row = rank - '0';     // '1'=1, ..., '8'=8

        return new ChessPosition(row, col);
    }

    ////////////////////////////////////////////////////////////////////////////////////////

    public void notify(ServerMessage msg) {
        switch (msg.getServerMessageType()) {
            case LOAD_GAME -> loadGame(msg);
            case ERROR -> printError(msg);
            case NOTIFICATION -> printMessage(msg);
            case ENTER -> loadGame(msg);
        }
    }

    public void loadGame(ServerMessage msg) {
        System.out.println("Incoming...\n");
        ChessGame game = msg.getGame();
        ChessGame.TeamColor c = msg.getColor();
        if (c == null) {
            c = ChessGame.TeamColor.WHITE;
        }
        currBoard = game.getBoard();
        BoardPrinter.printChessBoard(game.getBoard(), c);
        System.out.println(msg.getMsg());
        printPrompt();
    }

    public void printError(ServerMessage msg) {
        System.out.println("Incoming...\nError: " + msg.getMsg());
        printPrompt();
    }

    public void printMessage(ServerMessage msg) {
        System.out.println("Incoming...\n" + msg.getMsg());
        printPrompt();
    }

}
