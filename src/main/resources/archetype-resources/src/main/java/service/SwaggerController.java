#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.service;

import ${groupId}.${artifactId}.config.Properties;
import ${groupId}.${artifactId}.helper.ResourceFileHelper;  
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.MimeTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
     
@Singleton
public class SwaggerController {

    private final static String PATH = "api/";
    private final static String PATH_1 = "api";
    private final static String PATH_API_YAML = "api/swagger.yaml";
    private final static String PATH_API_YML = "api/swagger.yml";
    private final static String PATH_API_JSON = "api/swagger.json";
    private final static String PATH_API_JS = "api/swagger.js";

    @SuppressWarnings("unused")
    @Inject
    private Properties properties;

    private static final Logger log = LoggerFactory.getLogger(SwaggerController.class);

    public void init() {
        Spark.get(PATH, redirectToGetSwaggerModel);
        Spark.get(PATH_API_YAML, getSwaggerModel);
        Spark.get(PATH_API_YML, getSwaggerModel);
        Spark.get(PATH_1, redirectToGetSwaggerModel);
        Spark.get(PATH_API_JSON, getSwaggerModelJson);
        Spark.get(PATH_API_JS, getSwaggerModelJson);

    }

    private final Route redirectToGetSwaggerModel = (Request request, Response response) -> {
        String baseUrl = properties.getString("swagger-ui.scheme")
                + "://"
                + properties.getString("swagger-ui.host")
                + properties.getString("swagger-ui.basePath")
                + "/";
        response.redirect(baseUrl + "swagger.json");
        return "";

    };

    private final Route getSwaggerModel = (Request request, Response response) -> {
        response.header(HttpHeader.CONTENT_TYPE.asString(), "text/plain; charset=UTF8");
        return updatedSwaggerModel("/swagger.yaml");

    };
    private final Route getSwaggerModelJson = (Request request, Response response) -> {
        response.header(HttpHeader.CONTENT_TYPE.asString(), MimeTypes.Type.APPLICATION_JSON.asString());
        return updatedSwaggerModel("/swagger.json");
    };

    private String updatedSwaggerModel(String resourceFileName) {
        String result = null;
        try {
            result = ResourceFileHelper.readFromInputStream(getClass().getResourceAsStream(resourceFileName));
            result = result.replace("{{HOST}}", properties.getString("swagger-ui.host"));
            result = result.replace("{{SCHEME}}", properties.getString("swagger-ui.scheme"));
            result = result.replace("{{BASE_PATH}}", properties.getString("swagger-ui.basePath"));
        } catch (IOException e) {
            log.error(String.format("Access to resource file, failed : %s", resourceFileName));
        }
        return result;
    }
}
