package nl.bertriksikken.dragino;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DraginoMessageTest {

    /**
     *
     * Example data: 0E28 00 F602 0327 C504 1900
     * Example data: 0E34 00 FC01 B827 AF01 9000
     * Example data: 0E2E 00 F302 0127 E801 9000
     * Example data: 0E46 00 E801 8A27 DC01 FC00
     *               batt al tvoc eco2 temp humi
     */
    @Test
    public void testDecode() {
        byte[] data = new byte[]{0x0E, 0x28, 0x00, (byte) 0xF6, 0x02, 0x03, 0x27, (byte) 0xC5, 0x04, 0x19, 0x00};
        DraginoMessage message = DraginoMessage.parseAqs01(data);
        assertNotNull(message);
        assertEquals(3.624, message.battery(), 0.001);
        assertEquals(24.6, message.temperature(), 0.1);
        assertEquals(51.5, message.humidity(), 0.1);
        assertEquals(1049, message.co2());
        assertEquals(0, message.alarm());
    }

    @Test
    public void testTooShort() {
        byte[] data = new byte[3];
        DraginoMessage message = DraginoMessage.parseAqs01(data);
        assertNull(message);
    }

}
