import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import superapp.utils.GeneralUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Tag("InnerFunctionsUtilsTest")
public class InnerFunctionsUtilsTest {
    private final GeneralUtils temp = new GeneralUtils();
    @Test
    void testEmail() {
        Map<String, Boolean> test = new HashMap<String, Boolean>();
        test.put("yarden269@gmai.com", true);
        test.put("1dorfernc", false);
        test.put("jill@demo.org", true);
        ArrayList<Boolean> actualRes = new ArrayList<Boolean>();
        for (String var: test.keySet()) {
            actualRes.add(GeneralUtils.isValidEmail(var));
        }
        assertIterableEquals(test.values(), actualRes);
    }

    @Test
    void testRole() {
        String role1 = "SUPERAPP_USER";
        boolean actualRes = GeneralUtils.isValidRole(role1);
        assertTrue(actualRes);
    }

//    @Test
//    void testAvatar() {
//        Map<String, Boolean> test = new HashMap<String, Boolean>();
//        test.put("yarden", false);
//        test.put("https://static.thcdn.com/images/large/original//productimg/1600/1600/13733771-1754981942831425.jpg", true);
//        test.put("123", false);
//        ArrayList<Boolean> actualRes = new ArrayList<Boolean>();
//        for (String var: test.keySet()) {
//            actualRes.add(temp.isValidAvatar(var));
//        }
//        assertIterableEquals(test.values(), actualRes);
//    }

    @Test
    void testType() {
        Map<String, Boolean> test = new HashMap<String, Boolean>();
        test.put("dummy test", true);
        test.put("ass", true);
        test.put("0", false);
        ArrayList<Boolean> actualRes = new ArrayList<Boolean>();
        for (String var: test.keySet()) {
            actualRes.add(GeneralUtils.isValidType(var));
        }
        assertIterableEquals(test.values(), actualRes);
    }

    @Test
    void testAlias() {
        Map<String, Boolean> test = new HashMap<String, Boolean>();
        test.put("shut up", true);
        test.put("ass", true);
        test.put("0", false);
        ArrayList<Boolean> actualRes = new ArrayList<Boolean>();
        for (String var: test.keySet()) {
            actualRes.add(GeneralUtils.isValidAlias(var));
        }
        assertIterableEquals(test.values(), actualRes);
    }

    @Test
    void testActive() {
        Map<String, Boolean> test = new HashMap<String, Boolean>();
        test.put("true", true);
        test.put("false", true);
        test.put("anything", false);
        ArrayList<Boolean> actualRes = new ArrayList<Boolean>();
        for (String var: test.keySet()) {
            actualRes.add(GeneralUtils.activeTest(var));
        }
        assertIterableEquals(test.values(), actualRes);
    }
}
