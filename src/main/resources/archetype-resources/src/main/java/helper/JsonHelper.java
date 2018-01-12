#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.helper;

import com.google.gson.*;
import spark.Request;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Gson formater Herlper
 */
public class JsonHelper {
    private static final GsonBuilder gsonBuilder = new GsonBuilder();

    /**
     * Convert an object to Json representation
     *
     * @param data Object to serialize
     * @return JSON
     */
    public static String dataToJson(Object data) {
        final Gson gson = gsonBuilder
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Timestamp.class, new TimestampAdapter())
                .create();
        return gson.toJson(data);
    }

    /**
     * Set Json message with Http Error
     * @param code Http Code
     * @param message Message
     * @return String
     */
    public static String responseError(int code, String message) {
        final Gson gson = gsonBuilder
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Timestamp.class, new TimestampAdapter())
                .create();
        return gson.toJson(new Error(code, message));
    }

    /**
     * Check if request accept response Json format
     *
     * @param request Request
     * @return Boolean if accept Json
     */
    public static boolean clientAcceptsJson(Request request) {
        String accept = request.headers("Accept");
        return accept != null && accept.contains("application/json");
    }

    /**
     * Adapter for LocalDate
     */
    static class LocalDateAdapter implements JsonSerializer<LocalDate> {

        public JsonElement serialize(LocalDate localDate, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
        }
    }

    /**
     * Adapter for LocalDateTime
     */
    static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {

        public JsonElement serialize(LocalDateTime localDateTime, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    /**
     * Adapter for LocalDateTime
     */
    static class TimestampAdapter implements JsonSerializer<Timestamp> {

        public JsonElement serialize(Timestamp timestamp, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(timestamp.toLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }

    /**
     * Inner class for JSON response Error
     */
    static class Error {
        @Expose
        @SerializedName("error")
        public final ErrorContent error;

        public Error(int code, String message) {
            error = new ErrorContent(code, message);
        }
    }

    /**
     * Inner class for JSON response Error
     */
    static class ErrorContent {


        @Expose
        public final int code;
        @Expose
        public final String message;

        public ErrorContent(int code, String message) {
            this.code = code;
            this.message = message;
        }

    }
}
