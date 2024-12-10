package nl.bertriksikken.browan;

import nl.bertriksikken.browan.BrowanMessage.EBrowanItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public final class BrowanMessageTest {

    @Test
    public void testGeneric() {
        Instant now = Instant.now();
        BrowanMessage message = new BrowanMessage(now, "id", 123, "name");
        message.setRadio(7, 10.0, -100.0);

        Assertions.assertEquals(now, message.getTime());
        Assertions.assertEquals(123, message.getSequenceNumber());
        Assertions.assertEquals("id", message.getDeviceId());
        Assertions.assertEquals("name", message.getDeviceName());

        Assertions.assertEquals(7, message.getItem(EBrowanItem.RADIO_SF));
        Assertions.assertEquals(10.0, message.getItem(EBrowanItem.RADIO_SNR).doubleValue(), 0.1);
        Assertions.assertEquals(-100.0, message.getItem(EBrowanItem.RADIO_RSSI).doubleValue(), 0.1);
    }

    @Test
    public void testMissingPayload() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        Assertions.assertFalse(message.parsePayload(102, new byte[0]));

        Assertions.assertNull(message.getItem(EBrowanItem.STATUS));
    }

    @Test
    public void testWrongPort() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x00, 0x0B, 0x35, (byte) 0xEA, 0x0C, 0x4D, 0x03, 0x00 };
        Assertions.assertFalse(message.parsePayload(0, data));

        Assertions.assertNull(message.getItem(EBrowanItem.STATUS));
    }

    @Test
    public void testTbms() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x00, 0x0B, 0x35, (byte) 0xEA, 0x0C, 0x4D, 0x03, 0x00 };
        Assertions.assertTrue(message.parsePayload(102, data));

        Assertions.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assertions.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assertions.assertEquals(21.0, message.getItem(EBrowanItem.PCB_TEMP).doubleValue(), 0.01);
        Assertions.assertEquals(3306, message.getItem(EBrowanItem.MOVE_TIME));
        Assertions.assertEquals(845, message.getItem(EBrowanItem.MOVE_COUNT));
    }

    @Test
    public void testTbhv110() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x41, 0x0b, 0x36, 0x49, (byte) 0x96, 0x04, 0x02, 0x00, (byte) 0xec, 0x00, 0x35 };
        message.setRadio(7, 10.0, -100.0);
        Assertions.assertTrue(message.parsePayload(103, data));

        Assertions.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assertions.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assertions.assertEquals(22.0, message.getItem(EBrowanItem.PCB_TEMP).doubleValue(), 0.01);
        Assertions.assertEquals(73.0, message.getItem(EBrowanItem.HUMIDITY).doubleValue(), 0.1);
        Assertions.assertEquals(1174, message.getItem(EBrowanItem.ECO2));
        Assertions.assertEquals(2, message.getItem(EBrowanItem.VOC));
        Assertions.assertEquals(236, message.getItem(EBrowanItem.IAQ));
        Assertions.assertEquals(21.0, message.getItem(EBrowanItem.TEMPERATURE).doubleValue(), 0.01);
    }

    @Test
    public void testTbhh100() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x08, 0x0b, 0x3a, 0x38, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        Assertions.assertTrue(message.parsePayload(103, data));

        Assertions.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assertions.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assertions.assertEquals(26.0, message.getItem(EBrowanItem.TEMPERATURE).doubleValue(), 0.01);
        Assertions.assertEquals(56, message.getItem(EBrowanItem.HUMIDITY).doubleValue(), 0.1);
    }

    @Test
    public void testTbsl() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x00, 0x0B, 0x35, 0x50 };
        Assertions.assertTrue(message.parsePayload(105, data));

        Assertions.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assertions.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assertions.assertEquals(21.0, message.getItem(EBrowanItem.PCB_TEMP).doubleValue(), 0.1);
        Assertions.assertEquals(80, message.getItem(EBrowanItem.SOUND_LEVEL).doubleValue(), 0.1);
    }

    @Test
    public void testExtremeValues() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x41, 0x0B, 0x35, 0x4B, 0x14, 0x30, (byte) 0xE8, 0x03, (byte) 0xF4, 0x01, 0x34 };
        Assertions.assertTrue(message.parsePayload(103, data));

        Assertions.assertEquals(65, message.getItem(EBrowanItem.STATUS));
        Assertions.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assertions.assertEquals(21.0, message.getItem(EBrowanItem.PCB_TEMP).doubleValue(), 0.01);
        Assertions.assertEquals(75, message.getItem(EBrowanItem.HUMIDITY).doubleValue(), 0.1);
        Assertions.assertEquals(12308, message.getItem(EBrowanItem.ECO2).intValue(), 0.1);
        Assertions.assertEquals(1000, message.getItem(EBrowanItem.VOC).intValue());
        Assertions.assertEquals(500, message.getItem(EBrowanItem.IAQ).intValue());
        Assertions.assertEquals(20.0, message.getItem(EBrowanItem.TEMPERATURE).doubleValue(), 0.1);

    }
}
