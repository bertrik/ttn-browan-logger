package nl.bertriksikken.ttn.enddevice;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * https://www.thethingsindustries.com/docs/reference/api/end_device/#message:UpdateEndDeviceRequest
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

    /**
     * Field mask structure.<br>
     * <br>
     * https://www.thethingsindustries.com/docs/reference/api/field-mask/
     */
    public static final class FieldMask {
        @JsonProperty("paths")
        List<String> paths = new ArrayList<>();

        FieldMask(List<String> fields) {
            paths.addAll(fields);
        }

    }

}
