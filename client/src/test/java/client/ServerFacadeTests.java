package client;

import chess.ChessGame;
import exception.ResponseException;
import model.*;
import org.junit.jupiter.api.*;
import server.Server;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port); // fix: use actual port
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDatabase() throws ResponseException {
        serverFacade.clear();
    }

    // ─── register ────────────────────────────────────────────────────────────

    @Test
    public void registerSuccess() {
        UserData user = new UserData("greg", "greg", "greg@email.com");
        Assertions.assertDoesNotThrow(() -> serverFacade.registerUser(user));
    }

    @Test
    public void registerDuplicateUserFails() throws ResponseException {
        UserData user = new UserData("greg", "greg", "greg@email.com");
        serverFacade.registerUser(user);
        // registering the same username again should throw
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.registerUser(user));
    }

    // ─── login ───

    @Test
    public void loginSuccess() throws ResponseException {
        serverFacade.registerUser(new UserData("alice", "pass", "alice@email.com"));
        Assertions.assertDoesNotThrow(() -> serverFacade.login("alice", "pass"));
    }

    @Test
    public void loginWrongPasswordFails() throws ResponseException {
        serverFacade.registerUser(new UserData("alice", "pass", "alice@email.com"));
        Assertions.assertThrows(ResponseException.class, () ->
                serverFacade.login("alice", "ligma"));
    }

    // ─── create ────

    @Test
    public void createGameSuccess() throws ResponseException {
        AuthData auth = serverFacade.registerUser(new UserData(
                "bob", "pass", "bob@email.com"));
        Assertions.assertDoesNotThrow(() -> serverFacade.create("MyGame", auth.authToken()));
    }

    @Test
    public void createGameInvalidAuthFails() {
        Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.create("MyGame", "sugma"));
    }

    // ─── join ───

    @Test
    public void joinGameSuccess() throws ResponseException {
        AuthData auth = serverFacade.registerUser(
                new UserData("carol", "pass", "carol@email.com"));
        CreateGameResult game = serverFacade.create("JoinableGame", auth.authToken());
        Assertions.assertDoesNotThrow(
                () -> serverFacade.join(game.gameID(), ChessGame.TeamColor.WHITE, auth.authToken()));
    }

    @Test
    public void joinNonexistentGameFails() throws ResponseException {
        AuthData auth = serverFacade.registerUser(
                new UserData("carol", "pass", "carol@email.com"));
        Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.join(99999, ChessGame.TeamColor.WHITE, auth.authToken()));
    }

    // ─── logout ──

    @Test
    public void logoutSuccess() throws ResponseException {
        AuthData auth = serverFacade.registerUser(
                new UserData("dave", "pass", "dave@email.com"));
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(auth.authToken()));
    }

    @Test
    public void logoutInvalidTokenFails() {
        Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.logout("bogus-token"));
    }

    // ─── clear ───

    @Test
    public void clearSuccess() throws ResponseException {
        serverFacade.registerUser(new UserData("eve", "pass", "eve@email.com"));
        Assertions.assertDoesNotThrow(() -> serverFacade.clear());
    }

    @Test
    public void clearAllowsReRegisterAfterClear() throws ResponseException {
        // register, clear, then re-register same username — should succeed
        serverFacade.registerUser(new UserData("frank", "pass", "frank@email.com"));
        serverFacade.clear();
        Assertions.assertDoesNotThrow(
                () -> serverFacade.registerUser(
                        new UserData("frank", "pass", "frank@email.com")));
    }

    // ─── listGames —

    @Test
    public void listGamesSuccess() throws ResponseException {
        AuthData auth = serverFacade.registerUser(
                new UserData("grace", "pass", "grace@email.com"));
        serverFacade.create("Game1", auth.authToken());
        serverFacade.create("Game2", auth.authToken());
        ListGamesResult result = serverFacade.listGames(auth.authToken());
        Assertions.assertEquals(2, result.games().size());
    }

    @Test
    public void listGamesInvalidAuthFails() {
        Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.listGames("fake-token"));
    }

}
