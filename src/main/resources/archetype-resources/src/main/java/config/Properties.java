#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.config;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.ClasspathLocationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.net.URL;

/**
 * Properties loader
 * Load yaml or properties configuration
 * Order Format: YAML Format, PROPERTIES Format
 * Order Location : System var 'config.location' ,Environment var 'config.location' ,
 * With default name 'application.yml' or 'application.properties'  in 'config' subdirectory, current directory, classpath
 * Create an empty configuration file if no file is found
 */
@Singleton
public class Properties {
    private static final String SPECIFIC_LOCATION = "config.location";
    private static final String CONFIG_FILENAME = "application";
    private static final String PROPERTIES_EXTENSION = "properties";
    private static final String YAML_EXTENSION = "yml";

    private static final Logger log = LoggerFactory.getLogger(Properties.class);
    private Configuration config;

    public Properties() {
        loadConfiguration();
    }

    /**
     * Return YAML file or PROPERTIES file if doesn't exist.
     *
     * @param path String
     * @return File
     */
    private File getFile(String path) {
        File result;
        String basePath = (path != null) ? path + "\\" + CONFIG_FILENAME : CONFIG_FILENAME;
        String yamlPath = basePath + "." + YAML_EXTENSION;
        String propertiesPath = basePath + "." + PROPERTIES_EXTENSION;
        result = new File(yamlPath);
        if (!result.exists()) {
            result = new File(propertiesPath);
        }
        return result;
    }

    /**
     * Check if file exist in Classpath
     *
     * @param name Name of File
     * @return boolean
     */
    private boolean fileInClassPathExist(String name) {
        boolean result = false;
        try {
            URL url = Resources.getResource(name);
            result = true;
        } catch (java.lang.IllegalArgumentException ignored) {

        }
        return result;
    }

    /**
     * Load configuration file
     */
    private void loadConfiguration() {
        config = null;
        // 1 - Check System and Environnement file location
        String confPath = new SystemConfiguration().getString(SPECIFIC_LOCATION);
        if (confPath == null) {
            confPath = new EnvironmentConfiguration().getString(SPECIFIC_LOCATION);
        }

        File localFile = getFile(confPath);

        // 2 Environnement or system location file doesn't exist , check in config subdirectory
        if (!localFile.exists()) {
            localFile = getFile("config");
        }
        // 2 Check in current directory
        if (!localFile.exists()) {
            localFile = getFile(null);
        }

        Parameters params = new Parameters();
        String locationConfigFile = null;
        FileBasedConfigurationBuilder<FileBasedConfiguration> builder = null;
        if (localFile.exists()) {
            // File exist
            locationConfigFile = localFile.getAbsolutePath();
            if (Files.getFileExtension(localFile.getAbsolutePath()).equals(YAML_EXTENSION)) {
                builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(YAMLConfiguration.class)
                        .configure(params.fileBased()
                                .setFile(localFile));

            } else if (Files.getFileExtension(localFile.getAbsolutePath()).equals(PROPERTIES_EXTENSION)) {
                builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.fileBased()
                                .setFile(localFile));
            } else {
                log.error("Unknow configuration file extension");
                locationConfigFile = null;
            }
        } else {
            // File doesn't exist test in classpath
            if (fileInClassPathExist(CONFIG_FILENAME + "." + YAML_EXTENSION)) {
                builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(YAMLConfiguration.class)
                        .configure(params.fileBased().setLocationStrategy(new ClasspathLocationStrategy())
                                .setFileName(CONFIG_FILENAME + "." + YAML_EXTENSION));
                locationConfigFile = "classpath: " + CONFIG_FILENAME + "." + YAML_EXTENSION;
            } else if (fileInClassPathExist(CONFIG_FILENAME + "." + PROPERTIES_EXTENSION)) {
                builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.fileBased().setLocationStrategy(new ClasspathLocationStrategy())
                                .setFileName(CONFIG_FILENAME + "." + PROPERTIES_EXTENSION));
                locationConfigFile = "classpath: " + CONFIG_FILENAME + "." + PROPERTIES_EXTENSION;
            } else {
                log.error("Configuration : file not found");
            }
        }

        if (builder != null) {
            try {
                config = builder.getConfiguration();
                if (config != null) {
                    log.info("Configuration : Use " + locationConfigFile);
                }
            } catch (ConfigurationException cex) {
                log.error("Configuration : Error on " + locationConfigFile + " loading -> " + cex.getLocalizedMessage());
            }
        }
        if (config == null) {
            // create an empty configuration
            config = new PropertiesConfiguration();
        }
    }

    public String getString(String key) {
        return config.getString(key);
    }

    public String getString(String key, String defaultValue) {
        return config.getString(key, defaultValue);
    }

    public int getInt(String key) {
        return config.getInt(key);
    }

    public int getInt(String key, int defaultValue) {
        return config.getInt(key, defaultValue);
    }

    public boolean getBoolean(String key) {
        return config.getBoolean(key);
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        return config.getBoolean(key, defaultValue);
    }

    public long getLong(String key) {
        return config.getLong(key);
    }

    public long getLong(String key, long defaultValue) {
        return config.getLong(key, defaultValue);
    }

    public double getDouble(String key) {
        return config.getDouble(key);
    }

    public double getDouble(String key, double defaultValue) {
        return config.getDouble(key, defaultValue);
    }

    public float getFloat(String key) {
        return config.getFloat(key);
    }

    public float getFloat(String key, float defaultValue) {
        return config.getFloat(key, defaultValue);
    }


}
