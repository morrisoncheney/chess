package client;

import chess.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

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

    private ChessGame currGame;

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
        try {
            if (state.equals(State.IN_GAME)) {
                ws.exit(userAuth, currGameID, userColor);
            }
        } catch (Exception e) {
            System.out.println("Error: exiting game failed.");
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
                case "move" -> move(params);
                case "redraw" -> redraw(params);
                case "resign" -> resign(params);
                case "moves" -> seeMoves(params);
                case "leave" -> leaveGame(params);
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
            listGames();
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
            result.append("\n");
            result.append(String.format("ID[%d] / Name[%s] / White[%s] / Black[%s]\n",
                    game.gameID(),
                    game.gameName(),
                    whiteUsername,
                    blackUsername));
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
                throw new ResponseException(ResponseException.Code.ClientError,
                        String.format("%s is an invalid gameID value.", params[0]));
            }
            if (gameID > maxGameID) {
                maxGameID = gameID;
            }
            ChessGame.TeamColor color;

            try {
                color = ChessGame.TeamColor.valueOf(params[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new ResponseException(ResponseException.Code.ClientError,
                        String.format("Expected: <WHITE|BLACK>. %s is an invalid color value.", params[1]));
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
                throw new ResponseException(ResponseException.Code.ClientError,
                        String.format("%s is an invalid gameID value.", params[0]));
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

    public String redraw(String... empty) throws ResponseException {
        if (empty.length > 0) {
            throw new ResponseException(ResponseException.Code.ClientError, "just type 'move'");
        }
        ws.redraw(userAuth, currGameID, userColor);
        return "";
    }

    public String move(String... empty) throws ResponseException {
        assertInGame("make a move");

        // add a check to make sure empty is actually empty
        if (empty.length > 0) {
            throw new ResponseException(ResponseException.Code.ClientError, "just type 'move'");
        }

        System.out.print("From: ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        String[] tokens = line.toLowerCase().split(" ");

        if (tokens.length > 1) {
            throw new ResponseException(ResponseException.Code.ClientError, "expected <row letter><col number>");
        }
        String startingSquare = tokens[0];

        //convert startingSquare to a ChessPosition
        ChessPosition startPos = parsePosition(startingSquare);

        // get the piece at the beginning square

        boolean isPawn = (ChessPiece.PieceType.PAWN == currBoard().getPiece(startPos).getPieceType());

        ChessGame.TeamColor pieceColor = currBoard().getPiece(startPos).getTeamColor();

        if (pieceColor != userColor) {
            throw new ResponseException(ResponseException.Code.ClientError, "Error: you can't move a piece of the wrong color.");
        }

        System.out.print("To: ");
        line = scanner.nextLine();

        tokens = line.toLowerCase().split(" ");

        if (tokens.length > 1) {
            throw new ResponseException(ResponseException.Code.ClientError, "expected <row letter><col number>");
        }
        String endSquare = tokens[0];

        //convert endSquare to a ChessPosition
        ChessPosition endPos = parsePosition(endSquare);
        ChessPiece.PieceType promoType = null;
        if (isPawn && (endPos.getRow() == 1 || endPos.getRow() == 8)) {
            System.out.println("Promotion type: ");
            line = scanner.nextLine();
            tokens = line.toLowerCase().split(" ");
            try {
                promoType = ChessPiece.PieceType.valueOf(tokens[0]);
            } catch (IllegalArgumentException e) {
                throw new ResponseException(ResponseException.Code.ClientError, "Expected: <QUEEN|ROOK|BISHOP|KNIGHT>");
            }
        }

        ChessMove move = new ChessMove(startPos, endPos, promoType);

        ws.makeMove(userAuth, currGameID, move, userColor);

        return "";
    }

    public String seeMoves(String... empty) throws ResponseException {
        if (empty.length > 0) {
            throw new ResponseException(ResponseException.Code.ClientError, "just type 'moves'");
        }

        assertInGameOrObserving("see moves");
        System.out.print("From: ");
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();

        String[] tokens = line.toLowerCase().split(" ");

        if (tokens.length > 1) {
            throw new ResponseException(ResponseException.Code.ClientError, "expected <row letter><col number>");
        }
        String startingSquare = tokens[0];

        //convert startingSquare to a ChessPosition
        ChessPosition startPos = parsePosition(startingSquare);

        if (currBoard().getPiece(startPos) == null) {
            throw new ResponseException(ResponseException.Code.ClientError,
                    "you cannot see moves from a non-existent piece.");
        }

        Collection<ChessMove> valMoves = currGame.validMoves(startPos);

        BoardPrinter.printChessBoardWithMoves(currBoard(), userColor, valMoves, startPos);
        return "";
    }

    public String resign(String... empty) throws ResponseException {
        if (empty.length > 0) {
            throw new ResponseException(ResponseException.Code.ClientError, "just type 'resign'");
        }
        ws.resign(userAuth, currGameID, userColor);
        return "";
    }

    public String leaveGame(String... empty) throws ResponseException {
        if (empty.length > 0) {
            throw new ResponseException(ResponseException.Code.ClientError, "just type 'leave'");
        }
        assertInGameOrObserving("leave game");
        ws.exit(userAuth, currGameID, userColor);
        state = State.SIGNEDIN;
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
                - moves
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
                - move
                - resign
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
        if (state != State.IN_GAME) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    String.format("You must be playing a game to %s.", tryingTo)
            );
        }
    }

    private void assertInGameOrObserving(String tryingTo) throws ResponseException {
        if (state != State.IN_GAME && state != State.OBSERVING) {
            throw new ResponseException(
                    ResponseException.Code.ClientError,
                    String.format("You must be in a game to %s.", tryingTo)
            );
        }
    }

    public static ChessPosition parsePosition(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Expected: <letter><number>");
        }

        char col = Character.toLowerCase(pos.charAt(0));
        char rawRow = pos.charAt(1);

        if (col < 'a' || col > 'h') {
            throw new IllegalArgumentException("Invalid col: '" + col + "' (must be a–h)");
        }
        if (rawRow < '1' || rawRow > '8') {
            throw new IllegalArgumentException("Invalid row: '" + rawRow + "' (must be 1–8)");
        }

        int colInt = col - 'a' + 1;
        int row = rawRow - '0';

        return new ChessPosition(row, colInt);
    }

    public ChessBoard currBoard() {
        return currGame.getBoard();
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
        currGame = game;
        BoardPrinter.printChessBoard(game.getBoard(), c);
        printPrompt();
    }

    public void printError(ServerMessage msg) {
        System.out.println("Incoming...\nError: " + msg.getErrorMessage());
        printPrompt();
    }

    public void printMessage(ServerMessage msg) {
        System.out.println("Incoming...\n" + msg.getMsg());
        printPrompt();
    }

}
