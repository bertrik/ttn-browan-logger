package nl.bertriksikken.ttn;

import java.time.Instant;

import org.junit.Assert;
import org.junit.Test;

public final class LoraWanUplinkTest {

    @Test
    public void testToString() {
        LoraWanUplink uplink = new LoraWanUplink(Instant.now(), "id", 1, 100, new byte[] {1, 2, 3, 4});
        String s = uplink.toString();
        Assert.assertEquals("id id, port 100, SF 0, payload 01020304", s);
    }
    
}
