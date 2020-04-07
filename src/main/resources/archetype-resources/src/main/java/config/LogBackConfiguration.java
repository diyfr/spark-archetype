#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.${artifactId}.config;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ${groupId}.${artifactId}.helper.ResourceFileHelper;     
import org.apache.commons.configuration2.EnvironmentConfiguration;
import org.apache.commons.configuration2.SystemConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.File;

     
     

@Singleton
public class LogBackConfiguration {

    private static final String SPECIFIC_LOCATION = "logback.configurationFile";
    private static final String CONFIG_FILENAME = "logback_{ENV}.xml";



    private static final Logger log = LoggerFactory.getLogger(LogBackConfiguration.class);

    private static final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

    /**
     * Check if file exist in Classpath
     *
     * @param name Name of File
     * @return boolean
     */
    private boolean fileInClassPathExist(String name) {
        boolean result = ResourceFileHelper.fileInClassPathExist(name);
        if (!result) {
            log.info("Invalid URI on logback file {}", name);
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
     * @param ouputLogLocation         Output Directory log
     * @param logbackFileConfiguration Absolute path or file
     * @param profileExtension         Profile extension
     */
    @SuppressWarnings("squid:S3776")
    public void setConfiguration(String ouputLogLocation, String logbackFileConfiguration, String profileExtension) {
        //set envrionment variable
        setOuputFileLog((ouputLogLocation != null) ? ouputLogLocation : "");


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
        log.info("Search Logback config file :{}", filename);

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
                log.error("File {} doesn't exist, Default logback configuration", logbackFileConfiguration);
                return;
            }
        }
        JoranConfigurator configurator = new JoranConfigurator();
        loggerContext.reset();
        configurator.setContext(loggerContext);
        if (localFile.exists()) {
            try {
                configurator.doConfigure(localFile);
                log.info("Loading logback from path {}", localFile.getAbsolutePath());
            } catch (JoranException e) {
                log.error(String.format("Loading logback from path %s error:%s", localFile.getAbsolutePath(), e.getMessage()));
            }
        } else if (fileInClassPathExist(filename)) {
            try {
                configurator.doConfigure(ResourceFileHelper.locateFromClasspath(filename));
                log.info("Loading logback '{}' from classpath ", filename);
            } catch (JoranException e) {
                log.error(String.format("Loading logback '%s' from classpath error:%s", filename, e.getMessage()));
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
            log.info("Set output file log : {}", value);
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
