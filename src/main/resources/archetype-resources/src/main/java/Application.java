#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId};

import com.google.inject.Guice;
import com.google.inject.Injector;
import ${groupId}.${artifactId}.config.Properties;
import ${groupId}.${artifactId}.service.SwaggerController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Spark;

import javax.inject.Inject;

import static spark.Spark.*;


@SuppressWarnings("FieldCanBeLocal")
public class Application {

    private final SwaggerController swaggerController;
    private final Properties properties;

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        Injector injector = Guice.createInjector();
        Application main = injector.getInstance(Application.class);
    }

    @Inject
    public Application(SwaggerController swaggerController, Properties properties) {
        this.properties = properties;

        String ver = this.properties.getString("server.version");
        log.info("Start Spark server " + ((ver != null) ? "version " + ver : ""));
        if (this.properties.getInt("server.port", 0) != 0) {
            port(properties.getInt("server.port"));
        }
        if (this.properties.getInt("server.maxThreads", 0) != 0) {
            threadPool(properties.getInt("server.maxThreads"));
        }

        //secure("deploy/keystore.jks", "password", null, null);
        enableCORS("*", "GET POST PUT PATCH DELETE", "origin, content-type, accept");
        // Define route after ingnite spark standalone server

        this.swaggerController = swaggerController;
        this.swaggerController.init();
        // can get Spark status after route initialized

        log.info("Swagger-ui params : "
                + this.properties.getString("swagger-ui.scheme")
                +"://"
                +this.properties.getString("swagger-ui.host")
                +this.properties.getString("swagger-ui.basePath"));
        log.info("Listen on " + Spark.port());
    }

    @SuppressWarnings("SameParameterValue")
    private void enableCORS(final String origin, final String methods, final String headers) {

        options("/*", (request, response) -> {

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
            }
            return "OK";
        });

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", origin);
            response.header("Access-Control-Request-Method", methods);
            response.header("Access-Control-Allow-Headers", headers);
            response.type("application/json");
        });
    }
}
