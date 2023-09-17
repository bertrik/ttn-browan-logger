package nl.bertriksikken.browanlogger.export;

import java.io.IOException;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import nl.bertriksikken.browan.BrowanMessage;
import nl.bertriksikken.browan.BrowanMessage.EBrowanItem;

public final class BrowanCsvExporter {

    private final DateTimeFormatter dateTimeFormatter;
    private final CsvMapper csvMapper = new CsvMapper();
    private final CsvSchema csvSchema = csvMapper.schemaFor(BrowanExportEvent.class);

    public BrowanCsvExporter(ZoneId zone) {
        dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(zone);
    }

    BrowanExportEvent createEvent(BrowanMessage msg) {
        BrowanExportEvent event = new BrowanExportEvent();
        event.dateTime = dateTimeFormatter.format(msg.getTime().truncatedTo(ChronoUnit.SECONDS));
        event.deviceId = msg.getDeviceId();
        event.deviceName = msg.getDeviceName();
        event.sequence = msg.getSequenceNumber();
        event.radioSf = msg.getItem(EBrowanItem.RADIO_SF);
        event.radioSnr = msg.getItem(EBrowanItem.RADIO_SNR);
        event.radioRssi = msg.getItem(EBrowanItem.RADIO_RSSI);
        event.status = msg.getItem(EBrowanItem.STATUS);
        event.battery = msg.getItem(EBrowanItem.BATTERY);
        event.pcbTemp = msg.getItem(EBrowanItem.PCB_TEMP);
        event.temperature = msg.getItem(EBrowanItem.TEMPERATURE);
        event.humidity = msg.getItem(EBrowanItem.HUMIDITY);
        event.eco2 = msg.getItem(EBrowanItem.ECO2);
        event.voc = msg.getItem(EBrowanItem.VOC);
        event.iaq = msg.getItem(EBrowanItem.IAQ);
        event.moveTime = msg.getItem(EBrowanItem.MOVE_TIME);
        event.moveCount = msg.getItem(EBrowanItem.MOVE_COUNT);
        return event;
    }

    public void write(BrowanMessage message) throws IOException {
        BrowanExportEvent event = createEvent(message);

        CsvSchema schema = csvSchema.withHeader();
        ObjectWriter writer = csvMapper.writer(schema);
        String line = writer.writeValueAsString(event);

        System.out.println(line);
    }

}
