package nl.bertriksikken.ttn;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
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
    private String key = "secret";

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

    public Duration getIdentityServerTimeout() {
        return Duration.ofSeconds(identityServerTimeout);
    }

}