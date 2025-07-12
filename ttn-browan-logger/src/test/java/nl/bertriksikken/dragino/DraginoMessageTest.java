package nl.bertriksikken.dragino;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        DraginoMessage message = DraginoMessage.parse(data);
        assertEquals(3.624, message.battery());
        assertEquals(0, message.alarm());
        assertEquals(62978, message.tvoc());
        assertEquals(807, message.eco2());
        assertEquals(Double.NaN, message.temp());
        assertEquals(Double.NaN, message.humidity());
    }

    @Test
    public void testTooShort() {
        byte[] data = new byte[3];
        DraginoMessage message = DraginoMessage.parse(data);
        assertNull(message);
    }

}
