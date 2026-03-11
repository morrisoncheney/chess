package dataaccess;

import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

public class DataAccessTests {
    // we shall begin testing
    MySqlDataAccess db = new MySqlDataAccess();
    private Executable Exception;

    @Test
    void addAndGetUserPass() {
        UserData user = new UserData("me", "memememe", "me@me.me");
        db.addUser(user);

        UserData rec = db.getUser(user.username());

        Assertions.assertEquals(user, rec);
    }

    @Test
    void addUserFail() {
        UserData user = new UserData("me", "memememe", "me@me.me");
        Assertions.assertThrows(Exception.class, () -> db.addUser( user ));
    }

    @Test
    void getUserPass() {
        UserData user = new UserData("me", "memememe", "me@me.me");

        UserData rec = db.getUser(user.username());

        Assertions.assertEquals(user, rec);
    }
}
