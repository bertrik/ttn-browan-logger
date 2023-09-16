package nl.bertriksikken.browan;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BrowanMessage {

    private static final Logger LOG = LoggerFactory.getLogger(BrowanMessage.class);

    // measurement items from payload
    public enum EBrowanItem {
        STATUS, // generic status field, integer (one byte), no unit
        BATTERY, // floating point, in volts
        PCB_TEMP, // board temperature, floating point, in degrees C
        HUMIDITY, // relative humidity, floating point, in percent
        ECO2, // CO2 equivalent, integer, in ppm
        VOC, // VOC estimate, integer, in ppm
        IAQ, // air quality, integer, 0..500 no unit
        ENV_TEMP, // environment temperature, floating point, in degrees C
        MOVE_TIME, // time since last movement, integer, in minutes
        MOVE_COUNT, // total number of movement, integer, no unit

        RADIO_SF, // spreading factor, integer, 7..12 no unit
        RADIO_SNR, // signal-to-noise ratio, double, in dB
        RADIO_RSSI, // received signal strength indication, double, in dBm
    }

    // mandatory part
    private final Instant time;
    private final int sequenceNumber;
    private final String deviceId;
    private final String deviceName;

    // measurements from payload
    private final Map<EBrowanItem, Number> measurements = new HashMap<>();

    public BrowanMessage(Instant time, int sequenceNumber, String deviceId, String deviceName) {
        this.time = time;
        this.sequenceNumber = sequenceNumber;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
    }

    public Instant getTime() {
        return time;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setRadio(int spreadingFactor, double snr, double rssi) {
        measurements.put(EBrowanItem.RADIO_SF, spreadingFactor);
        measurements.put(EBrowanItem.RADIO_SNR, snr);
        measurements.put(EBrowanItem.RADIO_RSSI, rssi);
    }

    public void parsePayload(int port, byte[] data) {
        switch (port) {
        case 102:
            parseTbms100(data);
            break;
        case 103:
            switch (data.length) {
            case 8:
                parseTbhh100(data);
                break;
            case 11:
                parseTbhv110(data);
                break;
            default:
                LOG.warn("Could not parse payload for port {} and payload length {}", port, data.length);
                break;
            }
            break;
        default:
            LOG.warn("Payload was not parsed, unhandled port {}", port);
            break;
        }
    }

    private void parseTbhh100(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int status = parseStatus(bb.get());
        double battery = parseVoltage(bb.get());
        double temperature = parseTemperature(bb.get());
        double rh = parseHumidity(bb.get());

        measurements.put(EBrowanItem.STATUS, status);
        measurements.put(EBrowanItem.BATTERY, battery);
        measurements.put(EBrowanItem.ENV_TEMP, temperature);
        measurements.put(EBrowanItem.HUMIDITY, rh);
    }

    private void parseTbhv110(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int status = parseStatus(bb.get());
        double battery = parseVoltage(bb.get());
        double pcbTemp = parseTemperature(bb.get());
        double rh = parseHumidity(bb.get());
        int eco2 = bb.getShort() & 0xFFFF;
        int voc = bb.getShort() & 0xFFFF;
        int iaq = bb.getShort() & 0xFFFF;
        double envTemp = parseTemperature(bb.get());

        measurements.put(EBrowanItem.STATUS, status);
        measurements.put(EBrowanItem.BATTERY, battery);
        measurements.put(EBrowanItem.PCB_TEMP, pcbTemp);
        measurements.put(EBrowanItem.HUMIDITY, rh);
        measurements.put(EBrowanItem.ECO2, eco2);
        measurements.put(EBrowanItem.VOC, voc);
        measurements.put(EBrowanItem.IAQ, iaq);
        measurements.put(EBrowanItem.ENV_TEMP, envTemp);
    }

    private void parseTbms100(byte[] data) {
        if (data.length < 8) {
            LOG.warn("Expected at least 8 bytes for TBMS");
            return;
        }
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int status = parseStatus(bb.get());
        double battery = parseVoltage(bb.get());
        double temp = parseTemperature(bb.get());
        int time = bb.getShort() & 0xFFFF;
        int count = bb.getShort() & 0xFFFF;
        count += (bb.get() & 0xFF) << 16;

        measurements.put(EBrowanItem.STATUS, status);
        measurements.put(EBrowanItem.BATTERY, battery);
        measurements.put(EBrowanItem.PCB_TEMP, temp);
        measurements.put(EBrowanItem.MOVE_TIME, time);
        measurements.put(EBrowanItem.MOVE_COUNT, count);
    }

    private int parseStatus(byte b) {
        return b & 0xFF;
    }
    
    private double parseVoltage(byte b) {
        return 2.5 + 0.1 * (b & 0x0F);
    }

    private double parseTemperature(byte b) {
        return (b & 0x7F) - 32.0;
    }

    private double parseHumidity(byte b) {
        return (b & 0x7F);
    }

    public Number getItem(EBrowanItem item) {
        return measurements.get(item);
    }

}
