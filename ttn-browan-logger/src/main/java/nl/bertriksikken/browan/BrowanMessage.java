package nl.bertriksikken.browan;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import nl.bertriksikken.dragino.DraginoMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Representation of a Browan message, as sent over LoRaWAN.
 */
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
        TEMPERATURE, // environment temperature, floating point, in degrees C
        MOVE_TIME, // time since last movement, integer, in minutes
        MOVE_COUNT, // total number of movement, integer, no unit
        SOUND_LEVEL, // sound level, floating point, in dB

        RADIO_SF, // spreading factor, integer, 7..12 no unit
        RADIO_SNR, // signal-to-noise ratio, double, in dB
        RADIO_RSSI, // received signal strength indication, double, in dBm
    }

    // mandatory part
    private final Instant time;
    private final String deviceId;
    private final int sequenceNumber;
    private final String deviceName;

    // measurements from payload
    private final Map<EBrowanItem, Number> items = new LinkedHashMap<>();

    public BrowanMessage(Instant time, String deviceId, int sequenceNumber, String deviceName) {
        this.time = time;
        this.deviceId = deviceId;
        this.sequenceNumber = sequenceNumber;
        this.deviceName = deviceName;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{time=%s,id=%s,fcnt=%d,items=%s}", time, deviceId, sequenceNumber, items);
    }

    public Instant getTime() {
        return time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setRadio(int spreadingFactor, double snr, double rssi) {
        items.put(EBrowanItem.RADIO_SF, spreadingFactor);
        items.put(EBrowanItem.RADIO_SNR, snr);
        items.put(EBrowanItem.RADIO_RSSI, rssi);
    }

    public boolean parsePayload(int port, byte[] data) {
        // just drop empty packets
        if (data.length == 0) {
            return false;
        }

        switch (port) {
        case 2:
            return parseDraginoLAQ4(data);
        case 102:
            return parseTbms100(data);
        case 103:
            switch (data.length) {
            case 8:
                return parseTbhh100(data);
            case 11:
                return parseTbhv110(data);
            default:
                LOG.warn("Could not parse payload for port {} and payload length {}", port, data.length);
                break;
            }
            break;
        case 105:
            return parseTbsl100(data);
        default:
            LOG.warn("Payload was not parsed, unhandled port {}", port);
            break;
        }
        return false;
    }

    private boolean parseTbhh100(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int status = parseStatus(bb.get());
        double battery = parseVoltage(bb.get());
        double temperature = parseTemperature(bb.get());
        int humidity = parseHumidity(bb.get());

        items.put(EBrowanItem.STATUS, status);
        items.put(EBrowanItem.BATTERY, battery);
        items.put(EBrowanItem.TEMPERATURE, temperature);
        items.put(EBrowanItem.HUMIDITY, humidity);

        return true;
    }

    private boolean parseTbhv110(byte[] data) {
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int status = parseStatus(bb.get());
        double battery = parseVoltage(bb.get());
        double pcbTemp = parseTemperature(bb.get());
        int humidity = parseHumidity(bb.get());
        int eco2 = bb.getShort() & 0xFFFF;
        int voc = bb.getShort() & 0xFFFF;
        int iaq = bb.getShort() & 0xFFFF;
        double envTemp = parseTemperature(bb.get());

        items.put(EBrowanItem.STATUS, status);
        items.put(EBrowanItem.BATTERY, battery);
        items.put(EBrowanItem.PCB_TEMP, pcbTemp);
        items.put(EBrowanItem.HUMIDITY, humidity);
        items.put(EBrowanItem.ECO2, eco2);
        items.put(EBrowanItem.VOC, voc);
        items.put(EBrowanItem.IAQ, iaq);
        items.put(EBrowanItem.TEMPERATURE, envTemp);

        return true;
    }

    private boolean parseTbms100(byte[] data) {
        if (data.length < 8) {
            LOG.warn("Expected at least 8 bytes for TBMS, got {}", data.length);
            return false;
        }
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int status = parseStatus(bb.get());
        double battery = parseVoltage(bb.get());
        double temp = parseTemperature(bb.get());
        int moveTime = bb.getShort() & 0xFFFF;
        int moveCount = bb.getShort() & 0xFFFF;
        moveCount += (bb.get() & 0xFF) << 16;

        items.put(EBrowanItem.STATUS, status);
        items.put(EBrowanItem.BATTERY, battery);
        items.put(EBrowanItem.PCB_TEMP, temp);
        items.put(EBrowanItem.MOVE_TIME, moveTime);
        items.put(EBrowanItem.MOVE_COUNT, moveCount);
        return true;
    }

    private boolean parseTbsl100(byte[] data) {
        if (data.length < 4) {
            LOG.warn("Expected at least 8 bytes for TBSL, got {}", data.length);
            return false;
        }
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        int status = parseStatus(bb.get());
        double battery = parseVoltage(bb.get());
        double temp = parseTemperature(bb.get());
        double db = bb.get() & 0xFF;
        items.put(EBrowanItem.STATUS, status);
        items.put(EBrowanItem.BATTERY, battery);
        items.put(EBrowanItem.PCB_TEMP, temp);
        if (db != 255) {
            items.put(EBrowanItem.SOUND_LEVEL, db);
        }
        return true;
    }

    private boolean parseDraginoLAQ4(byte[] data) {
        DraginoMessage message = DraginoMessage.parse(data);
        if (message == null) {
            LOG.warn("Could not parse Dragino LAQ4 message");
            return false;
        }
        items.put(EBrowanItem.BATTERY, message.battery());
        items.put(EBrowanItem.STATUS, message.alarm());
        items.put(EBrowanItem.VOC, message.tvoc());
        items.put(EBrowanItem.ECO2, message.eco2());
        if (Double.isFinite(message.temp())) {
            items.put(EBrowanItem.TEMPERATURE, message.temp());
        }
        if (Double.isFinite(message.humidity())) {
            items.put(EBrowanItem.HUMIDITY, message.humidity());
        }
        return true;
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

    private int parseHumidity(byte b) {
        return (b & 0x7F);
    }

    public Number getItem(EBrowanItem item) {
        return items.get(item);
    }

}
