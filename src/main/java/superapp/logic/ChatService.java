package superapp.logic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.utils.exceptions.AlreadyExistException;
import superapp.utils.exceptions.NotFoundException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * ChatService class for interacting with the ChatEngine API.
 */
@Service("Chat")
public class ChatService implements MiniAppService {

    private static final String CHAT_ENGINE_PROJECT_ID = "f12245a0-b21b-4418-86db-62a6b73f8c72";
    private static final String CHAT_ENGINE_PRIVATE_KEY = "7b8b1b51-b1d8-4b64-9442-3817af720fde";
    public static boolean shouldChatEngine = false;
    private final Log logger = LogFactory.getLog(ChatService.class);

    /**
     * Default constructor for MiniAppChatBL.
     */
    public ChatService() {
    }

    /**
     * Performs a login request to the ChatEngine API.
     *
     * @param userSuperApp The user superapp.
     * @param userEmail    The user email.
     * @throws NotFoundException if the user doesn't exist in ChatEngine.
     */
    public void chatEngineLogIn(String userSuperApp, String userEmail) {
        this.logger.trace(String.format("%s under %s super app, log in into Chat mini app with", userEmail, userSuperApp));
        HttpURLConnection con = null;
        try {
            // Create GET request
            URL url = new URL("https://api.chatengine.io/users/me");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            // Set headers
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Project-ID", CHAT_ENGINE_PROJECT_ID);
            con.setRequestProperty("User-Name", userEmail);
            con.setRequestProperty("User-Secret", userEmail);
            // Generate response String
            StringBuilder responseStr = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseStr.append(responseLine.trim());
                }
            }

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> response = objectMapper.readValue(responseStr.toString(),
                    new TypeReference<HashMap<String, Object>>() {
                    });

            ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK);
            this.logger.debug(String.format("user %s with %s super app, finished to log in", userEmail, userSuperApp));
        } catch (Exception e) {
            this.logger.error("User {%s, %s} Doesn't Exist IN CHAT ENGINE".formatted(userSuperApp, userEmail));
            throw new NotFoundException("User {%s, %s} Doesn't Exist IN CHAT ENGINE".formatted(userSuperApp, userEmail));
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }


    /**
     * Performs a signup request to the ChatEngine API.
     *
     * @throws AlreadyExistException if the signup fails.
     */
    public void chatEngineSignUp(String email, String firstName, String lastName) {
        HttpURLConnection con = null;
        try {
            this.logger.trace(String.format("User with email %s, first name %s and last name %s, sign up to Chat mini app", email, firstName, lastName));
            // Create POST request
            URL url = new URL("https://api.chatengine.io/users");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            // Set headers
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Private-Key", CHAT_ENGINE_PRIVATE_KEY);
            // Add request body
            con.setDoOutput(true);
            Map<String, String> body = new HashMap<String, String>();
            body.put("username", email);
            body.put("secret", email);
            body.put("email", email);
            body.put("first_name", firstName); // first name
            body.put("last_name", lastName); // last name
            String jsonInputString = new JSONObject(body).toString();
            try (OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            // Generate response String
            StringBuilder responseStr = new StringBuilder();
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    responseStr.append(responseLine.trim());
                }
            }

            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> response = objectMapper.readValue(responseStr.toString(),
                    new TypeReference<HashMap<String, Object>>() {
                    });

            ResponseEntity<Map<String, Object>> res = new ResponseEntity<>(response, HttpStatus.OK); // run connection with chat engine
            this.logger.debug(String.format("User with email %s, first name %s and last name %s," +
                    " finished to sign up to Chat mini app", email, firstName, lastName));
        } catch (Exception e) {
            this.logger.error("CHAT ENGINE FAILED TO SIGNUP %s".formatted(email));
            throw new AlreadyExistException("CHAT ENGINE FAILED TO SIGNUP %s".formatted(email));
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    @Override
    public Object runCommand(MiniAppCommandBoundary command) {
        try {
            this.logger.trace("Run command %s in Chat mini app service".formatted(command.getCommand()));
            String commandOpt = command.getCommand();
            if (commandOpt.equals("activateChatSignUp")) {
                Map<String, Object> commandAttributes1 = command.getCommandAttributes();
                String firstName = commandAttributes1.get("firstName").toString();
                String lastName = commandAttributes1.get("lastName").toString();

                String email = command.getInvokedBy().getUserId().getEmail();
                chatEngineSignUp(email, firstName, lastName);
            } else if (commandOpt.equals("activateChatLogin")) {
                String superApp = command.getInvokedBy().getUserId().getSuperapp();
                String email = command.getInvokedBy().getUserId().getEmail();
                chatEngineLogIn(superApp, email);
            }

            shouldChatEngine = true;
            this.logger.debug("Finished to execute command %s in Chat mini app service".formatted(command.getCommand()));
            return null;
        } catch (Exception e) {
            this.logger.error("Undefined Command");
            throw new NotFoundException("Undefined Command");
        }
    }
}
