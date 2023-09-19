package nl.bertriksikken.browanlogger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import nl.bertriksikken.browan.BrowanMessage;
import nl.bertriksikken.browanlogger.export.BrowanExporter;
import nl.bertriksikken.ttn.LoraWanUplink;
import nl.bertriksikken.ttn.MqttListener;

public final class BrowanLogger {

    private static final Logger LOG = LoggerFactory.getLogger(BrowanLogger.class);
    private static final String CONFIG_FILE = "ttn-browan-logger.yaml";

    private final BrowanLoggerConfig config;
    private final MqttListener mqttListener;
    private final BrowanExporter exporter;

    public static void main(String[] args) throws IOException, MqttException {
        PropertyConfigurator.configure("log4j.properties");
        BrowanLoggerConfig config = readConfig(new File(CONFIG_FILE));
        BrowanLogger app = new BrowanLogger(config);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    BrowanLogger(BrowanLoggerConfig config) {
        this.config = Objects.requireNonNull(config);
        this.mqttListener = new MqttListener(config.ttnConfig.getMqttUrl());
        this.exporter = new BrowanExporter(config.exportConfig, config.ttnConfig.getName());
    }

    private void start() throws MqttException {
        mqttListener.subscribe(config.ttnConfig.getName(), config.ttnConfig.getKey(), this::messageReceived);
    }

    private void stop() {
        mqttListener.stop();
        LOG.info("Application stopped");
    }

    // package-private for testing
    void messageReceived(LoraWanUplink uplink) {
        LOG.info("Received uplink: {}", uplink);
        BrowanMessage message = new BrowanMessage(uplink.getTime(), uplink.getDeviceId(), uplink.getCounter(), "");
        message.setRadio(uplink.getSF(), uplink.getSNR(), uplink.getRSSI());
        if (message.parsePayload(uplink.getPort(), uplink.getFrmPayload())) {
            LOG.info("Parsed message: {}", message);
            try {
                exporter.write(message);
            } catch (IOException e) {
                LOG.warn("Could not export message", e);
            }
        }
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
