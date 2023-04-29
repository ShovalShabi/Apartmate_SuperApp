import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import superapp.utils.JsonTestsUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.fail;

@Tag("JsonUtilsTest")
public class JsonUtilsTest {
    private final JsonTestsUtils myJsonUtils = new JsonTestsUtils();

    @Test
    public void testValidUserBoundaryJson() {
        try {
            String json = loadJsonFile("src/test/java/user_boundary_valid.json");
            myJsonUtils.isValidateUserBoundaryJson(json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    public void testInvalidUserBoundaryJson() {
        try {
            String json = loadJsonFile("src/test/java/user_boundary_invalid.json");
            myJsonUtils.isValidateUserBoundaryJson(json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testValidSuperAppObjectBoundaryJson() {
        try {
            String json = loadJsonFile("src/test/java/SuperAppObjectBoundaryTemplate2.json");
            myJsonUtils.isValidateSuperAppObjectBoundaryJson(json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    @Test
    public void testInvalidSuperAppObjectBoundaryJson() {
        try {
            String json = loadJsonFile("src/test/java/SuperAppObjectBoundaryTemplate1.json");
            myJsonUtils.isValidateSuperAppObjectBoundaryJson(json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testInvalidMiniAppCommandBoundaryJson() {
        try {
            String json = loadJsonFile("src/test/java/miniapp_command_boundary_invalid.json");
            myJsonUtils.isValidateMiniAppCommandBoundaryJson(json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    public void testValidMiniAppCommandBoundaryJson() {
        try {
            String json = loadJsonFile("src/test/java/miniapp_command_boundary_valid.json");
            myJsonUtils.isValidateMiniAppCommandBoundaryJson(json);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    private String loadJsonFile(String filename) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filename)));
    }
}
