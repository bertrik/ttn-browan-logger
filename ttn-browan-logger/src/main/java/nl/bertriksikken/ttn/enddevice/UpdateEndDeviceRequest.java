package nl.bertriksikken.ttn.enddevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <a href="https://www.thethingsindustries.com/docs/reference/api/end_device/#message:UpdateEndDeviceRequest">UpdateEndDeviceRequest</a>
 */
public final class UpdateEndDeviceRequest {

    @JsonProperty("end_device")
    private final EndDevice endDevice;

    @JsonProperty("field_mask")
    private final FieldMask fieldMask;

    UpdateEndDeviceRequest(EndDevice endDevice, FieldMask fieldMask) {
        this.endDevice = endDevice;
        this.fieldMask = fieldMask;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "{device=%s,mask=%s}", endDevice, fieldMask);
    }

    /**
     * Field mask structure.<br>
     * <br>
     * <a href="https://www.thethingsindustries.com/docs/reference/api/field-mask/">field-mask</a>
     */
    public static final class FieldMask {
        @JsonProperty("paths")
        List<String> paths = new ArrayList<>();

        FieldMask(List<String> fields) {
            paths.addAll(fields);
        }

        @Override
        public String toString() {
            return String.format(Locale.ROOT, "{paths=%s}", paths);
        }

    }

}
