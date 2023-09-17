package nl.bertriksikken.browanlogger.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Export event data class.
 */
@JsonPropertyOrder({ "datetime", "deviceid", "devicename", "sequence", "radio_sf", "radio_snr", "radio_rssi", "status",
        "battery", "pcb_temp", "temperature", "humidity", "eco2", "voc", "iaq", "move_time", "move_count" })
public final class BrowanExportEvent {

    // generic properties
    @JsonProperty("datetime")
    String dateTime;

    @JsonProperty("deviceid")
    String deviceId;

    @JsonProperty("devicename")
    String deviceName;

    @JsonProperty("sequence")
    int sequence;

    // radio properties
    @JsonProperty("radio_sf")
    Number radioSf;

    @JsonProperty("radio_snr")
    Number radioSnr;

    @JsonProperty("radio_rssi")
    Number radioRssi;

    // common measurement properties
    @JsonProperty("status")
    Number status;

    @JsonProperty("battery")
    Number battery;

    // specific measurement properties
    @JsonProperty("pcb_temp")
    Number pcbTemp;

    @JsonProperty("temperature")
    Number temperature;

    @JsonProperty("humidity")
    Number humidity;

    @JsonProperty("eco2")
    Number eco2;

    @JsonProperty("voc")
    Number voc;

    @JsonProperty("iaq")
    Number iaq;

    @JsonProperty("move_time")
    Number moveTime;

    @JsonProperty("move_count")
    Number moveCount;
}
