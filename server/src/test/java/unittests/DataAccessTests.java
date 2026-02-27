package unittests;

import dataaccess.MemoryDataAccess;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDataAccessTests {
    private static final MemoryDataAccess data = new MemoryDataAccess();

    @BeforeEach
    void clear() {
        data.deleteAllUserData();
    }

    @Test
    void addAndGetUser() {
        UserData u = new UserData("alice", "pw", "a@a.com");
        UserData returned = data.addUser(u);

        assertEquals("alice", returned.username());
        assertNotNull(data.getUser("alice"));
        assertEquals(returned, data.getUser("alice"));
    }

    @Test
    void deleteUser() {
        data.addUser(new UserData("bob", "pw", "b@b.com"));
        data.deleteUser("bob");
        assertNull(data.getUser("bob"));
    }

    @Test
    void deleteAllUserData_clears() {
        data.addUser(new UserData("x", "y", "z"));
        data.deleteAllUserData();
        assertNull(data.getUser("x"));
    }
}