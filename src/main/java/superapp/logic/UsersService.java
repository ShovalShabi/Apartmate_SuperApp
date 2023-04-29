package superapp.logic;

import superapp.boundaries.user.UserBoundary;

import java.util.List;

public interface UsersService {
    UserBoundary createUser(UserBoundary user);
    UserBoundary login(String userSuperApp, String userEmail);
    UserBoundary updateUser(String userSuperApp, String userEmail, UserBoundary update);
    List<UserBoundary> getAllUsers();
    void deleteAllUsers();

}
