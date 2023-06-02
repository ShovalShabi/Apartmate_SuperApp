package superapp.utils;

import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.boundaries.object.SuperAppObjectBoundary;
import superapp.boundaries.user.NewUserBoundary;
import superapp.boundaries.user.UserIdBoundary;
import superapp.utils.Invokers.UserIdInvoker;

import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class TestDBHelpFunc {
    public String superappAuthUtl = "userSuperapp=2023b.dor.ferenc&userEmail=jillSuperApp@demo.org";

    public TestDBHelpFunc() {

    }

    public NewUserBoundary createDummyAdmin() {
        // Creates a new NewUserBoundary object and return it
        return new NewUserBoundary(
                "jillAdmin@demo.org",
                "ADMIN",
                "Jill Smith Admin",
                "https://static.thcdn.com/images/large/original/productimg/1600/1600/13733771-1754981942831425.jpg");
    }

    public NewUserBoundary createDummyMiniappUser() {
        // Creates a new NewUserBoundary object and return it
        return new NewUserBoundary(
                "jillMiniApp@demo.org",
                "MINIAPP_USER",
                "Jill Smith MiniApp",
                "https://static.thcdn.com/images/large/original/productimg/1600/1600/13733771-1754981942831425.jpg");
    }

    public NewUserBoundary createDummySuperappUser() {
        // Creates a new NewUserBoundary object and return it
        return new NewUserBoundary(
                "jillSuperApp@demo.org",
                "SUPERAPP_USER",
                "Jill Smith SuperApp",
                "https://static.thcdn.com/images/large/original/productimg/1600/1600/13733771-1754981942831425.jpg");
    }

    public NewUserBoundary createDummySuperappUser1() {
        // Creates a new NewUserBoundary object and return it
        return new NewUserBoundary(
                "jillSuperApp1@demo.org",
                "SUPERAPP_USER",
                "Jill Smith SuperApp1",
                "https://static.thcdn.com/images/large/original/productimg/1600/1600/13733771-1754981942831425.jpg");
    }

    public MiniAppCommandBoundary createCommandDummy() {
        return new MiniAppCommandBoundary(
                null,
                "doSomething",
                null,
                new Date(),
                null,
                new HashMap<>());
    }

    public SuperAppObjectBoundary createDummyObject() {
        return new SuperAppObjectBoundary(
                null,
                "dummyType",
                "demo instance",
                true,
                null,
                new Location(35.154, 30.81),
                new UserIdInvoker(new UserIdBoundary("2023b.dor.ferenc", "jillSuperApp@demo.org")),
                new TreeMap<>()
        );
    }
}
