package nl.bertriksikken.browanlogger;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public final class BrowanLogger {

    private static final Logger LOG = LoggerFactory.getLogger(BrowanLogger.class);
    private static final String CONFIG_FILE = "browanlogger.yaml";

    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("log4j.properties");

        BrowanLoggerConfig config = readConfig(new File(CONFIG_FILE));
        BrowanLogger app = new BrowanLogger(config);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private void start() {
        // TODO
    }

    private void stop() {
        // TODO
    }

    BrowanLogger(BrowanLoggerConfig config) {
        // TODO
    }

    private static BrowanLoggerConfig readConfig(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        BrowanLoggerConfig config = new BrowanLoggerConfig();
        if (file.exists()) {
            try {
                config = mapper.readValue(file, BrowanLoggerConfig.class);
            } catch (IOException e) {
                LOG.warn("Failed to load config {}, using defaults", file.getAbsoluteFile());
            }
        } else {
            LOG.warn("Config file '{}' not found, creating default", file.getAbsoluteFile());
            mapper.writeValue(file, config);
        }
        return config;
    }
}
