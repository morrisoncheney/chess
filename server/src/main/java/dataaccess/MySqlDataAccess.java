package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.GameDataListItem;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess { // this should use the same method names as MemoryDataAccess

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    final private HashMap<Integer, GameData> games = new HashMap<>();

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
        var statement = "TRUNCATE users";
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
        var statement = "TRUNCATE auths";
        runUpdate(statement);
    }

/////////////////////////////////////////////////////////////////////

    public Integer addGame(String gameName) {

        try (Connection conn = DatabaseManager.getConnection()) {

            var statement = "INSERT INTO games (gameDataJson) VALUES (NULL)";
            try (PreparedStatement ps1 = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

                ps1.executeUpdate();

                int id;

                try (ResultSet rs = ps1.getGeneratedKeys()) {
                    if (rs.next()){
                        id = rs.getInt(1);
                    } else {
                        throw new Error("db failed to generate gameID");
                    }
                }

                GameData game = new GameData(id, new ChessGame(), null, null, gameName);

                statement = "UPDATE games SET gameDataJson=? WHERE gameID=?";

                runUpdate(statement, game.toString(), id);

                return id;
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: ", e);
        }
    }

    public GameData getGame(Integer gameId) {
        try (Connection conn = DatabaseManager.getConnection()) {

            var statement = "SELECT gameDataJson FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {

                ps.setInt(1, gameId);
                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        return new Gson().fromJson(rs.getString("gameDataJson"),GameData.class);
                    }
                }
            }
        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: ", e);
        }
        return null;
    }

    public ArrayList<GameDataListItem> getGameList(){

        ArrayList<GameDataListItem> gameList = new ArrayList<>();

        GameDataListItem gdli;

        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()){
                    while (rs.next()) {
                        GameData game = new Gson().fromJson(rs.getString("gameDataJson"), GameData.class);
                        gdli = new GameDataListItem(rs.getInt("gameID"), game.whiteUsername(),
                                game.blackUsername(), game.gameName());
                        gameList.add(gdli);
                    }

                }
            }


        } catch (Exception e) {
            throw new DataAccessException("Unable to read data: ", e);
        }

        for (GameData game : games.values()) {
            GameDataListItem item;
            item = new GameDataListItem(game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName());
            gameList.add(item);
        }
        return gameList;

    }

    public void replaceUser(ChessGame.TeamColor color, String username, Integer gameID) {
        GameData data = getGame(gameID);

        GameData newGame;
        if (color == ChessGame.TeamColor.WHITE){
            newGame = new GameData(data.gameID(), data.game(), username, data.blackUsername(), data.gameName());
            replace(gameID, newGame);
        } else {
            newGame = new GameData(data.gameID(), data.game(), data.whiteUsername(), username, data.gameName());
            replace(gameID, newGame);
        }
    }

    private void replace(int gameID, GameData game) {

        String statement = "UPDATE games SET gameDataJson=? WHERE gameID=?";

        runUpdate(statement, game.toString(), gameID);

    }

    public void deleteAllGames() {
        var statement = "TRUNCATE games";
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
            `gameID` int NOT NULL AUTO_INCREMENT PRIMARY KEY,
            `gameDataJson` JSON DEFAULT NULL
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





