#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.service;

import ${groupId}.${artifactId}.config.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.inject.Singleton;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

@Singleton
public class SwaggerController {

    private final static String PATH = "api/";
    private final static String PATH_1 = "api";
    private final static String PATH_API_YAML = "api/swagger.yaml";
    private final static String PATH_API_JSON = "api/swagger.json";

    @Inject
    private Properties properties;

    private static final Logger log = LoggerFactory.getLogger(SwaggerController.class);

    public void init() {
        Spark.get(PATH, redirectToGetSwaggerModel);
        Spark.get(PATH_API_YAML, getSwaggerModel);
        Spark.get(PATH_1, redirectToGetSwaggerModel);
        Spark.get(PATH_API_JSON, getSwaggerModelJson);

    }

    private Route redirectToGetSwaggerModel = (Request request, Response response) -> {
        String baseUrl = request.url();
        if (!baseUrl.substring(baseUrl.length() - 1).equals("/")) {
            baseUrl += "/";
        }
        response.redirect(baseUrl + "swagger.json");
        return null;

    };

    private Route getSwaggerModel = (Request request, Response response) -> {
        response.header("Content-type", "application/text; charset=UTF8");
        return updatedSwaggerModel("/swagger.yaml");

    };
    private Route getSwaggerModelJson = (Request request, Response response) -> {
        response.header("Content-type", "application/json; charset=UTF8");
        return updatedSwaggerModel("/swagger.json");
    };

    private String updatedSwaggerModel(String resourceFileName) {
        String result = null;
        try {
            result = readFromInputStream(getClass().getResourceAsStream(resourceFileName));
            result = result.replace("{{HOST}}", properties.getString("swagger-ui.host"));
            result = result.replace("{{SCHEME}}", properties.getString("swagger-ui.scheme"));
            result = result.replace("{{BASE_PATH}}", properties.getString("swagger-ui.basePath"));
        } catch (IOException e) {
            log.error("Access to resource file, failed :" + resourceFileName);
        }
        return result;
    }

    private String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

}
