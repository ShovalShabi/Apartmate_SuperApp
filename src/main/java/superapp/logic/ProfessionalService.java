package superapp.logic;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import superapp.boundaries.command.MiniAppCommandBoundary;
import superapp.converters.ObjectConverter;
import superapp.dal.ObjectCrud;
import superapp.data.SuperAppObjectEntity;
import superapp.utils.exceptions.InvalidInputException;
import superapp.utils.exceptions.NotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service("Professionals")
public class ProfessionalService implements MiniAppService {
    private final ObjectCrud objectCrud;
    private final ObjectConverter objectConverter;
    private String apiKey;
    private String apiHost;
    private final String SPLITTER;
    private String baseUrl;
    private final String RAPID_KEY;
    private final String RAPID_HOST;
    private ObjectMapper jackson;
    private final Log logger = LogFactory.getLog(ProfessionalService.class);

    @Autowired
    public ProfessionalService(ObjectCrud objectCrud, ObjectConverter objectConverter) {
        this.objectCrud = objectCrud;
        this.objectConverter = objectConverter;
        this.RAPID_KEY = "X-RapidAPI-Key";
        this.RAPID_HOST = "X-RapidAPI-Host";
        this.SPLITTER = "%20";
    }

    @PostConstruct
    public void setup() {
        this.jackson = new ObjectMapper();
        this.apiKey = "6776a2c8d3msh1c0d85f49449326p1f2fccjsncc9f10d368dd";
        this.apiHost = "local-business-data.p.rapidapi.com";
        this.baseUrl = "https://local-business-data.p.rapidapi.com/search?query=";
    }

    @Override
    public Object runCommand(MiniAppCommandBoundary command) {
        this.logger.trace("Run command %s in Professionals service".formatted(command.getCommand()));
        String commandOpt = command.getCommand();
        if (commandOpt.equals("searchProfessional")) {
            this.logger.debug("Finished to run command %s in Professionals service".formatted(command.getCommand()));
            return this.sendQuery(command);
        }
        this.logger.error("Undefined Command");
        throw new NotFoundException("Undefined Command");
    }

    private Object sendQuery(MiniAppCommandBoundary command) {
        this.logger.trace("Send query to external API in Professionals Service");
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(this.baseUrl + buildQuery(command.getCommandAttributes())))
                    .header(RAPID_KEY, apiKey)
                    .header(RAPID_HOST, apiHost)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String id = objectConverter.createID(command.getTargetObject().getObjectId());
                Optional<SuperAppObjectEntity> house = objectCrud.findById(id);
                SuperAppObjectEntity houseEntity = null;
                if (house.isPresent())
                    houseEntity = house.get();
                else {
                    this.logger.error("House not exist");
                    throw new InvalidInputException("House not exist");
                }

                Map<String, Object> objectDetails = new HashMap<>();
                objectDetails.put("lastSearch", jackson.readValue(response.body(), Map.class));
                houseEntity.setObjectDetails(objectDetails);
                this.objectCrud.save(houseEntity);

                this.logger.trace("Query retrieved successfully from the API in Professionals Service");
                return response.body();
            } else
                throw new RuntimeException("Request Failed");
        } catch (IOException | InterruptedException e) {
            this.logger.fatal("Unknown exception");
            throw new RuntimeException(e.getMessage());
        }
    }

    private String buildQuery(Map<String, Object> queryAttributes) {
        this.logger.trace("Build query in Professionals service");
        String encodedQuery = queryAttributes.values()
                .stream()
                .map(Object::toString)
                .map(value -> URLEncoder.encode(value, StandardCharsets.UTF_8))
                .reduce((q1, q2) -> q1 + SPLITTER + q2)
                .orElse("");
        this.logger.debug("Finished build query in Professionals service");
        return encodedQuery;
    }
}
