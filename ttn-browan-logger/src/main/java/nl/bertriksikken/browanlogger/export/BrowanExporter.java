package nl.bertriksikken.browanlogger.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import nl.bertriksikken.browan.BrowanMessage;
import nl.bertriksikken.browan.BrowanMessage.EBrowanItem;

public final class BrowanExporter {

    private static final Logger LOG = LoggerFactory.getLogger(BrowanExporter.class);

    private final DateTimeFormatter dateTimeFormatter;
    private final CsvMapper csvMapper = new CsvMapper();
    private final CsvSchema csvSchema = csvMapper.schemaFor(BrowanExportEvent.class);

    private final BrowanExportConfig config;
    private final String appId;

    public BrowanExporter(BrowanExportConfig config, String appId) {
        this.config = Objects.requireNonNull(config);
        this.appId = Objects.requireNonNull(appId);

        dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(config.getTimeZone());
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

    private File createFile(String baseName) {
        LocalDate now = LocalDate.now(config.getTimeZone());
        int year = now.getYear();
        int week = now.get(WeekFields.of(config.getLocale()).weekOfYear());
        String fileName = String.format(Locale.ROOT, "%04dW%02d_%s.csv", year, week, baseName);
        File folder = config.getExportFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            LOG.warn("Could not create folder: {}", folder.getAbsolutePath());
        }
        File file = new File(config.getExportFolder(), fileName);
        return file;
    }

    public void write(BrowanMessage message) throws IOException {
        BrowanExportEvent event = createEvent(message);
        File file = createFile(appId);
        boolean append = file.exists();
        CsvSchema schema = append ? csvSchema.withoutHeader() : csvSchema.withHeader();
        ObjectWriter writer = csvMapper.writer(schema);
        String value = writer.writeValueAsString(event).trim();
        LOG.info("Writing to {}: {}", file.getAbsolutePath(), value);
        try (FileOutputStream fos = new FileOutputStream(file, append)) {
            writer.writeValue(fos, event);
        }
    }

}
