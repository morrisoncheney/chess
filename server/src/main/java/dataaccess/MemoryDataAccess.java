package dataaccess;

import model.UserData;

import java.util.HashMap;

public class MemoryDataAccess {
    final private HashMap<String, UserData> users = new HashMap<>();

    public UserData addUser(UserData user) {
        user = new UserData(user.username(), user.password(), user.email());

        users.put(user.username(), user);
        return user;
    }

//    public PetList listPets() {return new PetList(users.values());}
//    I don't think I need a UserList or anything like that.

    public UserData getUserData(String username) {
        return users.get(username);
    }

    public void deleteUserData(String username) {
        users.remove(username);
    }

    public void deleteAllUserData() {
        users.clear();
    }
}
