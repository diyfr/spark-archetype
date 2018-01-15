#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import com.google.common.io.Resources;
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;
import java.net.URL;

@Singleton
public class LogBackConfiguration {

    private static final String SPECIFIC_LOCATION = "logback.configurationFile";
    private static final String CONFIG_FILENAME = "logback_{ENV}.xml";


    private static final Logger log = LoggerFactory.getLogger(LogBackConfiguration.class);

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
     * Load Logback Configuration and restart this.
     * Specify full location of file or profile extension
     * Log profile extension, PRD, PROD or null -> search logback.xml
     * Search filename with this format logback_${logging.profile}.xml
     * exemple environment = "dev"  search logback_dev.xml
     *
     * @param ouputLogLocation Output Directory log
     * @param logbackFileConfiguration Absolute path or file
     * @param profileExtension         Profile extension
     */
    public void setConfiguration(String ouputLogLocation, String logbackFileConfiguration, String profileExtension) {
        //set envrionment variable
        setOuputFileLog((ouputLogLocation!=null)?ouputLogLocation:"");

        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        File localFile;
        String filename;
        if (profileExtension == null) {
            profileExtension = "PROD";
        }
        if (profileExtension.equals("PROD") || profileExtension.equals("PRD")) {
            filename = CONFIG_FILENAME.replace("_{ENV}", "");
        } else {
            filename = CONFIG_FILENAME.replace("{ENV}", profileExtension);
        }

        if (logbackFileConfiguration == null) {
            // 1 - Check System and Environnement file location
            String confPath = new SystemConfiguration().getString(SPECIFIC_LOCATION);
            if (confPath == null) {
                confPath = new EnvironmentConfiguration().getString(SPECIFIC_LOCATION);
            }

            // Check with confpath
            localFile = getFile(confPath, filename);

            // 2 Environnement or system location file doesn't exist , check in config subdirectory
            if (!localFile.exists()) {
                localFile = getFile("config", filename);
            }
            // 2 Check in current directory
            if (!localFile.exists()) {
                localFile = getFile(null, filename);
            }
        } else {
            localFile = new File(logbackFileConfiguration);
            if (!localFile.exists()) {
                log.error("File " + logbackFileConfiguration + " doesn't exist, Default logback configuration");
                return;
            }
        }
        JoranConfigurator configurator = new JoranConfigurator();
        loggerContext.reset();
        configurator.setContext(loggerContext);
        if (localFile.exists()) {
            try {
                configurator.doConfigure(localFile);
                log.info("Loading logback from path " + localFile.getAbsolutePath());
            } catch (JoranException e) {
                log.error("Loading logback from path " + localFile.getAbsolutePath() + " error:" + e.getMessage());
            }
        } else if (fileInClassPathExist(filename)) {
            try {
                configurator.doConfigure(Resources.getResource(filename));
                log.info("Loading logback from classpath");
            } catch (JoranException e) {
                log.error("Loading logback from classpath error:" + e.getMessage());
            }
        } else {
            log.error("Default logback configuration");
        }
    }

    private void setOuputFileLog(String output) {
        if (output != null) {
            setSystemProperty("LOG_FILE", output);
        }
    }

    /**
     * Set system property without override !
     *
     * @param name  property name
     * @param value property valie
     */
    @SuppressWarnings("SameParameterValue")
    private void setSystemProperty(String name, String value) {
        if (System.getProperty(name) == null && value != null) {
            log.info("Set output file log : '" + value);
            System.setProperty(name, value);
        }
    }

    /**
     * Return XML logback file.
     *
     * @param path String
     * @return File
     */
    private File getFile(String path, String filename) {
        File result;
        String basePath = (path != null) ? path + File.separator + filename : filename;
        result = new File(basePath);
        return result;
    }
}
