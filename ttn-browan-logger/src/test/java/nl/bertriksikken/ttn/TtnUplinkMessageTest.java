package nl.bertriksikken.ttn;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class TtnUplinkMessageTest {

    @Test
    public void testDeserialize() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        URL url = getClass().getClassLoader().getResource("uplink.json");
        TtnUplinkMessage message = mapper.readValue(url, TtnUplinkMessage.class);
        LoraWanUplink uplink = message.toLoraWanUplink();
        Assert.assertEquals("eui-e8e1e10001084c94", uplink.getDeviceId());
        Assert.assertEquals(10678, uplink.getCounter());
        Assert.assertEquals(103, uplink.getPort());
        Assert.assertEquals(11, uplink.getFrmPayload().length);
    }

}
