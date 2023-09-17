package nl.bertriksikken.ttn;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

@JsonIgnoreProperties(ignoreUnknown = true)
public final class TtnUplinkMessage {

    @JsonProperty("received_at")
    private String receivedAt = "";

    @JsonProperty("end_device_ids")
    private JsonNode endDeviceIds;

    @JsonProperty("uplink_message")
    private UplinkMessage uplink;

    public LoraWanUplink toLoraWanUplink() {
        Instant time = Instant.parse(receivedAt);
        String deviceId = endDeviceIds.at("/device_id").asText("");
        LoraWanUplink message = new LoraWanUplink(time, deviceId, uplink.fcnt, uplink.fport, uplink.payload);
        int sf = uplink.settings.at("/data_rate/lora/spreading_factor").asInt();

        // find highest RSSI and SNR over all receiving gateways
        double rssi = uplink.rxMetadata.stream().mapToDouble(m -> m.at("/rssi").asDouble()).max().orElse(Double.NaN);
        double snr = uplink.rxMetadata.stream().mapToDouble(m -> m.at("/snr").asDouble()).max().orElse(Double.NaN);
        message.setRadioParams(rssi, snr, sf);
        return message;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static final class UplinkMessage {
        @JsonProperty("f_port")
        private int fport;

        @JsonProperty("f_cnt")
        private int fcnt;

        @JsonProperty("frm_payload")
        private byte[] payload = new byte[0];

        @JsonProperty("rx_metadata")
        private List<JsonNode> rxMetadata = new ArrayList<>();

        @JsonProperty("settings")
        private JsonNode settings;
    }

}
