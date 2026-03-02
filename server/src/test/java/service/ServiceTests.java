package service;

import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.LoginRequest;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.BadRequestException;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTests {

    private Service service;
    private MemoryDataAccess data;

    @BeforeEach
    void setup() {
        data = new MemoryDataAccess();
        service = new Service(data);
    }

    @Test
    void registerAndLoginLogout() {
        UserData user = new UserData("x", "y", "z");
        AuthData auth = service.registerRequest(user);
        assertEquals("x", auth.username());
        assertNotNull(data.getAuth(auth.authToken()), "token should be stored after register");

        // login should succeed and produce a fresh token
        LoginRequest login = new LoginRequest("x", "y");
        AuthData loginAuth = service.loginRequest(login);
        assertEquals("x", loginAuth.username());
        assertNotNull(data.getAuth(loginAuth.authToken()));

        // log out the first auth token
        service.logoutRequest(auth.authToken());
        assertNull(data.getAuth(auth.authToken()), "token should be removed after logout");
    }

    @Test
    void registerDuplicateThrowsForbidden() {
        UserData user = new UserData("x", "y", "z");
        service.registerRequest(user);
        assertThrows(ForbiddenResponse.class, () -> service.registerRequest(user));
    }

    @Test
    void loginWrongPasswordThrowsUnauthorized() {
        UserData user = new UserData("x2", "y2", "z2");
        service.registerRequest(user);
        LoginRequest bad = new LoginRequest("x2", "wrong");
        assertThrows(UnauthorizedResponse.class, () -> service.loginRequest(bad));
    }

    @Test
    void loginNonexistentThrowsUnauthorized() {
        LoginRequest bad = new LoginRequest("nosuch", "whatever");
        assertThrows(UnauthorizedResponse.class, () -> service.loginRequest(bad));
    }

    @Test
    void logoutInvalidTokenThrowsUnauthorized() {
        assertThrows(UnauthorizedResponse.class, () -> service.logoutRequest("nope"));
    }

    @Test
    void logoutEmptyOrNullTokenThrowsBadRequest() {
        assertThrows(BadRequestException.class, () -> service.logoutRequest(""));
        assertThrows(BadRequestException.class, () -> service.logoutRequest(null));
    }

    @Test
    void findUserAndAuthenticate() {
        UserData u = new UserData("x3", "y3", "z3");
        service.registerRequest(u);
        UserData found = service.findUser("x3");
        assertEquals(u.username(), found.username());

        AuthData auth = data.getAuth(service.loginRequest(new LoginRequest("x3", "y3")).authToken());
        // authenticate should not throw for a valid token
        assertDoesNotThrow(() -> service.authenticate(auth.authToken()));

        // invalid tokens cause unauthorized
        assertThrows(UnauthorizedResponse.class, () -> service.authenticate("badtoken"));
    }

    @Test
    void memorySelfDestructClearsAllData() {
        UserData u = new UserData("x4", "y4", "z4");
        AuthData auth = service.registerRequest(u);
        assertNotNull(data.getUser("x4"));
        assertNotNull(data.getAuth(auth.authToken()));

        service.memorySelfDestruct();
        assertNull(data.getUser("x4"));
        assertNull(data.getAuth(auth.authToken()));
    }
}

