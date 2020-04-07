#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.helper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;

public final class ResourceFileHelper {

    private static final Logger log = LoggerFactory.getLogger(ResourceFileHelper.class);

    private ResourceFileHelper() {
    }


    /**
     * Check if file exist in Classpath
     *
     * @param name Name of File
     * @return boolean
     */
    public static boolean fileInClassPathExist(String name) {
        URL url = locateFromClasspath(name);
        return (url != null);
    }


    public static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }


    /**
     * Get URL from resource file
     *
     * @param resourceName resource name in resources directory
     * @return URL or null if doesn't exist
     */
    public static URL locateFromClasspath(String resourceName) {
        URL url = null;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            if (loader != null) {
                url = loader.getResource(resourceName);
                if (url != null) {
                    log.debug("Loading configuration from the context classpath ({})", resourceName);
                }
            }
        } catch (SecurityException s) {
            log.error("locateFromclasspath current thread cannot get the context ClassLoader ", s);
        }

        if (url == null) {
            url = ClassLoader.getSystemResource(resourceName);
            if (url != null) {
                log.debug("Loading configuration from the system classpath ({})", resourceName);
            }
        }

        return url;
    }

    /**
     * Get file extension
     *
     * @param fullName filename
     * @return extension or empty string
     */
    public static String getFileExtension(String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            String fileName = new File(fullName).getName();
            int dotIndex = fileName.lastIndexOf('.');
            return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
        }
        return "";
    }

}
