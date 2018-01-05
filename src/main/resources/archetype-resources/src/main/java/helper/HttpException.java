#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.helper;

import org.eclipse.jetty.http.HttpStatus;

/**
 * custom exception for bugtracker format
 */
public class HttpException extends Exception {

    private int code = HttpStatus.INTERNAL_SERVER_ERROR_500;

    public HttpException(String message) {
        super(message);
    }

    public HttpException(int httpStatus, String message) {
        super(message);
        this.code = httpStatus;
    }

    public int getHttpStatus() {
        return code;
    }


}
