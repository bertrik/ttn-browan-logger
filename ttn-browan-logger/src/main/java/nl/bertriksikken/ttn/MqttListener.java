package nl.bertriksikken.ttn;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Listener process for receiving data from MQTT.
 * 
 * Decouples the MQTT callback from listener using a single thread executor.
 */
public final class MqttListener {

    private static final Logger LOG = LoggerFactory.getLogger(MqttListener.class);
    private static final long DISCONNECT_TIMEOUT_MS = 3000;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final String url;
    private final Map<String, MqttClient> subscriptions = new HashMap<>();

    public MqttListener(String url) {
        LOG.info("Creating client for MQTT server '{}'", url);
        this.url = Objects.requireNonNull(url);
    }

    public void subscribe(String appId, String appKey, IMessageReceived callback) throws MqttException {
        MqttClient client = new MqttClient(url, MqttClient.generateClientId(), new MemoryPersistence());
        client.setCallback(new MqttCallbackHandler(client, "v3/+/devices/+/up", callback));

        // connect
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(appId);
        options.setPassword(appKey.toCharArray());
        options.setAutomaticReconnect(true);
        client.connect(options);

        subscriptions.put(appId, client);
    }

    /**
     * Stops this module.
     */
    public void stop() {
        subscriptions.forEach(this::stopSubscription);
    }

    private void stopSubscription(String appId, MqttClient client) {
        LOG.info("Stopping MQTT listener '{}'", appId);
        try {
            client.disconnect(DISCONNECT_TIMEOUT_MS);
        } catch (MqttException e) {
            LOG.warn("Failed to stop connection", e);
        }
    }

    /**
     * MQTT callback handler, (re-)subscribes to the topic and forwards incoming
     * messages.
     */
    private static final class MqttCallbackHandler implements MqttCallbackExtended {

        private final MqttClient client;
        private final String topic;
        private final IMessageReceived listener;

        private MqttCallbackHandler(MqttClient client, String topic, IMessageReceived listener) {
            this.client = client;
            this.topic = topic;
            this.listener = listener;
        }

        @Override
        public void connectionLost(Throwable cause) {
            LOG.warn("Connection lost: {}", cause.getMessage());
        }

        @Override
        public void messageArrived(String topic, MqttMessage mqttMessage) {
            LOG.info("Message arrived on topic '{}'", topic);

            // notify our listener, in an exception safe manner
            try {
                String payload = new String(mqttMessage.getPayload(), StandardCharsets.UTF_8);
                TtnUplinkMessage ttnUplinkMessage = MAPPER.readValue(payload, TtnUplinkMessage.class);
                LoraWanUplink uplink = ttnUplinkMessage.toLoraWanUplink();

                listener.messageReceived(uplink);
            } catch (Exception e) {
                LOG.trace("Caught Exception", e);
                LOG.error("Caught Exception in MQTT listener: {}", e.getMessage());
            }
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
            // nothing to do
        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            LOG.info("Connected to '{}', subscribing to MQTT topic '{}'", serverURI, topic);
            try {
                client.subscribe(topic);
            } catch (MqttException e) {
                LOG.trace("Caught MqttException", e);
                LOG.error("Caught MqttException while subscribing: {}", e.getMessage());
            }
        }
    }

    /**
     * Interface of the callback from the MQTT listener.
     */
    public interface IMessageReceived {

        /**
         * Indicates that a message was received.
         * 
         * @param uplink the message
         */
        void messageReceived(LoraWanUplink uplink);

    }
}
