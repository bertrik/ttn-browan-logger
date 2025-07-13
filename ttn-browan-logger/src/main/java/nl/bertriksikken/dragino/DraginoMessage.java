package nl.bertriksikken.dragino;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public record DraginoMessage(double battery, double temperature, double humidity, double pressure, int co2,
                             byte alarm) {

    public static DraginoMessage parseAqs01(byte[] data) {
        if (data.length != 11) {
            return null; // or throw an exception if preferred
        }
        ByteBuffer bb = ByteBuffer.wrap(data).order(ByteOrder.BIG_ENDIAN);

        double battery = (bb.getShort() & 0xFFFF) / 1000.0; // mV -> V
        double temperature = bb.getShort() / 10.0; // degrees Celcius
        double humidity = (bb.getShort() & 0xFFFF) / 10.0; // percent
        double pressure = (bb.getShort() & 0xFFFF) * 100.0; // Pa
        int co2 = bb.getShort() & 0xFFFF;  // ppm
        byte alarm = bb.get();

        return new DraginoMessage(battery, temperature, humidity, pressure, co2, alarm);
    }

}
