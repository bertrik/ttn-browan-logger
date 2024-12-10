package nl.bertriksikken.browanlogger.export;

import nl.bertriksikken.browan.BrowanMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.Instant;

public final class BrowanExporterTest {

    @Test
    public void testHappyFlow() throws IOException {
        BrowanMessage message = new BrowanMessage(Instant.now(), "device-id", 123, "device-name");
        message.setRadio(7, 10.0, -100.0);
        message.parsePayload(103, new byte[]{0x41, 0x0b, 0x36, 0x49, (byte) 0x96, 0x04, 0x02, 0x00, (byte) 0xec, 0x00, 0x35});

        BrowanExportConfig config = new BrowanExportConfig();
        BrowanExporter exporter = new BrowanExporter(config, "sensors");
        exporter.write(message);

        Assertions.assertTrue(config.getExportFolder().list().length > 0);
    }
}
