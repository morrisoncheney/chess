package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.GameDataListItem;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess { // this should use the same method names as MemoryDataAccess

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    final private HashMap<Integer, GameData> games = new HashMap<>();

    private Integer nextId = 1233;

//////////////////////////////////////////////////////////////////////////////////////////

    public UserData addUser(UserData user) {

        var statement = "INSERT INTO users VALUES (?, ?, ?)";

        String hashed = hashP(user.password());

        runUpdate(statement, user.username(), hashed, user.email());

        return user;
    }

    public UserData getUser(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {

            var statement = "SELECT password, email FROM users WHERE user=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {

                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return new UserData(username, rs.getNString("password"), rs.getNString("email"));
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: ", e);
        }
        return null;
    }

    public void deleteAllUserData() {
        var statement = "DROP TABLE users";
        runUpdate(statement);
    }



    private String hashP(String clearPassword) {
        return BCrypt.hashpw(clearPassword, BCrypt.gensalt());
    }

/////////////////////////////////////////////////////////////////////

    public void addAuth(AuthData auth) {
        var statement = "INSERT INTO auths (username, authToken) VALUES (?, ?)";

        runUpdate(statement, auth.username(), auth.authToken());
    }

    public AuthData getAuth(String authToken) {
        try (Connection conn = DatabaseManager.getConnection()) {

            var statement = "SELECT username FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {

                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return new AuthData(rs.getString("username"), authToken);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: ", e);
        }
        return null;
    }

    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auths WHERE authToken=?";
        runUpdate(statement, authToken);
    }

    public void deleteAllAuthData() {
        var statement = "DROP TABLE auths";
        runUpdate(statement);
    }

/////////////////////////////////////////////////////////////////////

    public Integer addGame(String gameName) {

        GameData game = new GameData(nextId, new ChessGame(), null, null, gameName);

        games.put(nextId, game);

        return nextId;
    }

    public GameData getGame(Integer gameId) {
        return new GameData(1, new ChessGame(), "w ", "b ", "gn ");
    }

    public ArrayList<GameDataListItem> getGameList(){

        ArrayList<GameDataListItem> gameList = new ArrayList<>();

        for (GameData game : games.values()) {
            GameDataListItem item;
            item = new GameDataListItem(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
            gameList.add(item);
        }
        return gameList;

    }

    public void replaceUser(ChessGame.TeamColor color, String username, Integer gameID) {
        GameData data = games.get(gameID);

        GameData newGame;
        if (color == ChessGame.TeamColor.WHITE){
            newGame = new GameData(data.gameID(), data.game(), username, data.blackUsername(), data.gameName());
            games.replace(gameID, newGame);
        } else {
            newGame = new GameData(data.gameID(), data.game(), data.whiteUsername(), username, data.gameName());
            games.replace(gameID, newGame);
        }
    }

    public void deleteAllGames() {
        var statement = "DROP TABLE games";
        runUpdate(statement);
    }

private final String[] createStatements = {
        """ 
        CREATE TABLE IF NOT EXISTS users (
          `user` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256) NOT NULL,
          PRIMARY KEY (`user`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """,
        """
        CREATE TABLE IF NOT EXISTS auths (
            `authToken` varchar(256) NOT NULL,
            `username` varchar(256) NOT NULL,
            PRIMARY KEY (`authToken`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """,
        """
        CREATE TABLE IF NOT EXISTS games (
            `gameID` int NOT NULL AUTO_INCREMENT,
            `gameDataJson` JSON DEFAULT NULL,
            PRIMARY KEY (`gameID`)
        ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
        """
};


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to configure database: ", ex);
        }
    }

    private int runUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof GameData g) ps.setString(i + 1, g.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException(String.format("unable to update database | Statement: %s | ", statement), e);
        }
    }


}





