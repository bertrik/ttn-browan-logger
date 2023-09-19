package nl.bertriksikken.browanlogger.export;

import java.io.File;
import java.time.ZoneId;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class BrowanExportConfig {

    @JsonProperty("timezone")
    private String timeZone = "Europe/Amsterdam";

    @JsonProperty("locale")
    private String locale = "nl";

    @JsonProperty("folder")
    private String exportFolder = "data";

    public ZoneId getTimeZone() {
        return ZoneId.of(timeZone);
    }

    public Locale getLocale() {
        return new Locale(locale);
    }

    public File getExportFolder() {
        return new File(exportFolder);
    }

}
