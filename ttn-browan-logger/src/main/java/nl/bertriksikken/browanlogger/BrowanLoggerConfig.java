package nl.bertriksikken.browanlogger;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonProperty;

import nl.bertriksikken.browanlogger.export.BrowanExportConfig;
import nl.bertriksikken.ttn.TtnConfig;

@JsonAutoDetect(getterVisibility = Visibility.NONE)
public final class BrowanLoggerConfig {
    @JsonProperty("ttn")
    public TtnConfig ttnConfig = new TtnConfig();

    @JsonProperty("export")
    public BrowanExportConfig exportConfig = new BrowanExportConfig();
}
