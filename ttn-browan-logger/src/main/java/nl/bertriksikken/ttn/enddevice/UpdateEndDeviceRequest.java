package nl.bertriksikken.ttn.enddevice;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * <a href="https://www.thethingsindustries.com/docs/reference/api/end_device/#message:UpdateEndDeviceRequest">UpdateEndDeviceRequest</a>
 */
public record UpdateEndDeviceRequest(@JsonProperty("end_device") EndDevice endDevice,
                                     @JsonProperty("field_mask") FieldMask fieldMask) {

    /**
     * Field mask structure.<br>
     * <br>
     * <a href="https://www.thethingsindustries.com/docs/reference/api/field-mask/">field-mask</a>
     */
    public record FieldMask(@JsonProperty("paths") List<String> paths) {
        public FieldMask {
            paths = List.copyOf(paths);
        }
    }

}
