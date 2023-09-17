package nl.bertriksikken.browanlogger.export;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;

import org.junit.Test;

import nl.bertriksikken.browan.BrowanMessage;

public final class BrowanExporterTest {

    @Test
    public void test() throws IOException {
        BrowanMessage message = new BrowanMessage(Instant.now(), "device-id", 123, "device-name");
        message.setRadio(7, 10.0, -100.0);
        message.parsePayload(103,
                new byte[] { 0x41, 0x0b, 0x36, 0x49, (byte) 0x96, 0x04, 0x02, 0x00, (byte) 0xec, 0x00, 0x35 });

        BrowanCsvExporter exporter = new BrowanCsvExporter(ZoneId.of("Europe/Amsterdam"));
        exporter.write(message);
    }
}
