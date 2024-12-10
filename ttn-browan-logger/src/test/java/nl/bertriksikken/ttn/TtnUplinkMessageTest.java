package nl.bertriksikken.ttn;

import java.io.IOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class TtnUplinkMessageTest {

    @Test
    public void testDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL url = getClass().getClassLoader().getResource("uplink.json");
        TtnUplinkMessage message = mapper.readValue(url, TtnUplinkMessage.class);
        LoraWanUplink uplink = message.toLoraWanUplink();
        Assertions.assertEquals("eui-e8e1e10001084c94", uplink.getDeviceId());
        Assertions.assertEquals(10678, uplink.getCounter());
        Assertions.assertEquals(103, uplink.getPort());
        Assertions.assertEquals(11, uplink.getFrmPayload().length);
    }

}
