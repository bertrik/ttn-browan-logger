package nl.bertriksikken.ttn;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

/**
 * Common class containing only the relevant fields from the TTN upload message.
 */
public final class LoraWanUplink {

    private final Instant time;
    private final String deviceId;
    private final int counter;
    private final int port;
    private final byte[] frmPayload;
    private double rssi = Double.NaN;
    private double snr = Double.NaN;
    private int sf = 0;

    public LoraWanUplink(Instant time, String deviceId, int counter, int port, byte[] frmPayload) {
        this.time = time.truncatedTo(ChronoUnit.MILLIS);
        this.deviceId = deviceId;
        this.counter = counter;
        this.port = port;
        this.frmPayload = frmPayload.clone();
    }

    public void setRadioParams(double rssi, double snr, int sf) {
        this.rssi = rssi;
        this.snr = snr;
        this.sf = sf;
    }

    public Instant getTime() {
        return time;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public int getCounter() {
        return counter;
    }

    public int getPort() {
        return port;
    }

    public byte[] getFrmPayload() {
        return frmPayload.clone();
    }

    public double getRSSI() {
        return rssi;
    }

    public double getSNR() {
        return snr;
    }

    public int getSF() {
        return sf;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "id %s, port %d, SF %d, %d bytes", deviceId, port, sf, frmPayload.length);
    }

}
