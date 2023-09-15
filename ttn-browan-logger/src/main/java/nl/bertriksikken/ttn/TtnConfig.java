package nl.bertriksikken.ttn;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class TtnConfig {

    @JsonProperty("mqtt_url")
    private String mqttUrl = "tcp://eu1.cloud.thethings.network";

    @JsonProperty("identity_server_url")
    private String identityServerUrl = "https://eu1.cloud.thethings.network";

    @JsonProperty("identity_server_timeout")
    private int identityServerTimeout = 20;

    @JsonProperty("name")
    private String name = "hetnatuurhistorischsensors";

    @JsonProperty("key")
    private String key = "NNSXS.xxx";

    public String getMqttUrl() {
        return mqttUrl;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public String getIdentityServerUrl() {
        return identityServerUrl;
    }

    public long getIdentityServerTimeout() {
        return identityServerTimeout;
    }

}