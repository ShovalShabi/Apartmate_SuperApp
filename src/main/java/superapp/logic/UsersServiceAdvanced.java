package superapp.logic;

import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserBoundary;

public interface UsersServiceAdvanced extends UsersService {
    UserBoundary createUser(NewUserBoundary user);
}
