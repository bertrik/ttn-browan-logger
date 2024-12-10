package nl.bertriksikken.ttn;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Instant;

public final class LoraWanUplinkTest {

    @Test
    public void testToString() {
        LoraWanUplink uplink = new LoraWanUplink(Instant.now(), "id", 1, 100, new byte[] {1, 2, 3, 4});
        String s = uplink.toString();
        Assertions.assertEquals("id id, port 100, SF 0, payload 01020304", s);
    }
    
}
