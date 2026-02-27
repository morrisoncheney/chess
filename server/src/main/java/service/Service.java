package service;

import dataaccess.MemoryDataAccess;
import model.UserData;

public class Service {

    private final MemoryDataAccess dataAccess;

    public Service(MemoryDataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public UserData registerRequest(UserData user){
        user.check();

        checkForUser(user);

        return new UserData("","","");
    }

    public void checkForUser(UserData user){
        UserData currentUser = dataAccess.getUser(user.username());
        if (currentUser != null) {
            throw new IllegalArgumentException("Username already taken.");
        }
    }
}
