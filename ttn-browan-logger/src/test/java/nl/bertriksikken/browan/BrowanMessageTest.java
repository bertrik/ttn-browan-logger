package nl.bertriksikken.browan;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

import nl.bertriksikken.browan.BrowanMessage.EBrowanItem;

public final class BrowanMessageTest {

    @Test
    public void testGeneric() {
        Instant now = Instant.now();
        BrowanMessage message = new BrowanMessage(now, "id", 123, "name");
        message.setRadio(7, 10.0, -100.0);

        Assert.assertEquals(now, message.getTime());
        Assert.assertEquals(123, message.getSequenceNumber());
        Assert.assertEquals("id", message.getDeviceId());
        Assert.assertEquals("name", message.getDeviceName());

        Assert.assertEquals(7, message.getItem(EBrowanItem.RADIO_SF));
        Assert.assertEquals(10.0, message.getItem(EBrowanItem.RADIO_SNR).doubleValue(), 0.1);
        Assert.assertEquals(-100.0, message.getItem(EBrowanItem.RADIO_RSSI).doubleValue(), 0.1);
    }

    @Test
    public void testMissingPayload() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        message.parsePayload(102, new byte[0]);

        Assert.assertNull(message.getItem(EBrowanItem.STATUS));
    }

    @Test
    public void testWrongPort() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x00, 0x0B, 0x35, (byte) 0xEA, 0x0C, 0x4D, 0x03, 0x00 };
        message.parsePayload(0, data);

        Assert.assertNull(message.getItem(EBrowanItem.STATUS));
    }

    @Test
    public void testTbms() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x00, 0x0B, 0x35, (byte) 0xEA, 0x0C, 0x4D, 0x03, 0x00 };
        message.parsePayload(102, data);

        Assert.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assert.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assert.assertEquals(21.0, message.getItem(EBrowanItem.PCB_TEMP).doubleValue(), 0.01);
        Assert.assertEquals(3306, message.getItem(EBrowanItem.MOVE_TIME));
        Assert.assertEquals(845, message.getItem(EBrowanItem.MOVE_COUNT));
    }

    @Test
    public void testTbhv110() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x41, 0x0b, 0x36, 0x49, (byte) 0x96, 0x04, 0x02, 0x00, (byte) 0xec, 0x00, 0x35 };
        message.setRadio(7, 10.0, -100.0);
        message.parsePayload(103, data);

        Assert.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assert.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assert.assertEquals(22.0, message.getItem(EBrowanItem.PCB_TEMP).doubleValue(), 0.01);
        Assert.assertEquals(73.0, message.getItem(EBrowanItem.HUMIDITY).doubleValue(), 0.1);
        Assert.assertEquals(1174, message.getItem(EBrowanItem.ECO2));
        Assert.assertEquals(2, message.getItem(EBrowanItem.VOC));
        Assert.assertEquals(236, message.getItem(EBrowanItem.IAQ));
        Assert.assertEquals(21.0, message.getItem(EBrowanItem.ENV_TEMP).doubleValue(), 0.01);
    }

    @Test
    public void testTbhh100() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x08, 0x0b, 0x3a, 0x38, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        message.parsePayload(103, data);

        Assert.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assert.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assert.assertEquals(26.0, message.getItem(EBrowanItem.ENV_TEMP).doubleValue(), 0.01);
        Assert.assertEquals(56, message.getItem(EBrowanItem.HUMIDITY).doubleValue(), 0.1);
    }

    @Test
    public void test() {
        BrowanMessage message = new BrowanMessage(Instant.now(), "id", 0, "name");
        byte[] data = { 0x08, 0x0b, 0x39, 0x3a, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff };
        message.parsePayload(103, data);

        Assert.assertNotNull(message.getItem(EBrowanItem.STATUS));
        Assert.assertEquals(3.6, message.getItem(EBrowanItem.BATTERY).doubleValue(), 0.01);
        Assert.assertEquals(25.0, message.getItem(EBrowanItem.ENV_TEMP).doubleValue(), 0.01);
        Assert.assertEquals(58, message.getItem(EBrowanItem.HUMIDITY).doubleValue(), 0.1);
    }
}