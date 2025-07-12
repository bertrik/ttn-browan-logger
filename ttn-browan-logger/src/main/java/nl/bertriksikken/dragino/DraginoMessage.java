package nl.bertriksikken.dragino;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record DraginoMessage(double battery, byte alarm, int tvoc, int eco2, double temp, double humidity) {
    public static DraginoMessage parse(byte[] data) {
        if (data.length != 11) {
            return null; // or throw an exception if preferred
        }

        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        double battery = (bb.getShort() & 0xFFFF) / 1000.0; // mV -> V
        byte alarm = bb.get();
        int tvoc = (bb.getShort() & 0xFFFF); // ppb
        int eco2 = bb.getShort() & 0xFFFF;  // ppm
        double temp = bb.getShort() / 10.0; // degrees Celcius
        double humidity = (bb.getShort() & 0xFFFF) / 10.0; // percent

        // guard against nonsense values
        temp = (temp < -50) || (temp > 150) ? Double.NaN : temp;
        humidity = (humidity < 0) || (humidity > 100) ? Double.NaN : humidity;

        return new DraginoMessage(battery, alarm, tvoc, eco2, temp, humidity);
    }
}
