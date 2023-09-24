package nl.bertriksikken.browanlogger;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.apache.log4j.PropertyConfigurator;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import nl.bertriksikken.browan.BrowanMessage;
import nl.bertriksikken.browanlogger.export.BrowanExporter;
import nl.bertriksikken.ttn.LoraWanUplink;
import nl.bertriksikken.ttn.MqttListener;

public final class BrowanLogger {

    private static final Logger LOG = LoggerFactory.getLogger(BrowanLogger.class);
    private static final String CONFIG_FILE = "ttn-browan-logger.yaml";
    private static final String LOG4J_FILE = "log4j.properties";

    private final BrowanLoggerConfig config;
    private final MqttListener mqttListener;
    private final BrowanExporter exporter;
    private final DeviceNameRegistry nameRegistry;

    public static void main(String[] args) throws IOException, MqttException {
        PropertyConfigurator.configure(LOG4J_FILE);
        BrowanLoggerConfig config = readConfig(new File(CONFIG_FILE));
        BrowanLogger app = new BrowanLogger(config);
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        app.start();
    }

    BrowanLogger(BrowanLoggerConfig config) {
        this.config = Objects.requireNonNull(config);
        this.mqttListener = new MqttListener(config.ttnConfig.getMqttUrl());
        this.exporter = new BrowanExporter(config.exportConfig, config.ttnConfig.getName());
        this.nameRegistry = new DeviceNameRegistry(config.ttnConfig.getIdentityServerUrl(),
                config.ttnConfig.getIdentityServerTimeout(), config.ttnConfig.getName(), config.ttnConfig.getKey());
    }

    void start() throws MqttException {
        LOG.info("BrowanLogger starting");
        nameRegistry.start();
        mqttListener.subscribe(config.ttnConfig.getName(), config.ttnConfig.getKey(), this::messageReceived);
    }

    void stop() {
        mqttListener.stop();
        nameRegistry.stop();
        LOG.info("BrowanLogger stopped");
    }

    // package-private for testing
    void messageReceived(LoraWanUplink uplink) {
        LOG.info("Received uplink: {}", uplink);
        String name = nameRegistry.getDeviceName(uplink.getDeviceId());
        BrowanMessage message = new BrowanMessage(uplink.getTime(), uplink.getDeviceId(), uplink.getCounter(), name);
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
        YAMLMapper mapper = new YAMLMapper();
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
