package nl.bertriksikken.ttn.enddevice;

import java.io.IOException;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.ttn.enddevice.EndDevice.EndDevices;
import nl.bertriksikken.ttn.enddevice.UpdateEndDeviceRequest.FieldMask;
import okhttp3.OkHttpClient;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Communicates with the TTN v3 device registry API.<br>
 * <br>
 * https://www.thethingsindustries.com/docs/reference/api/end_device/#the-enddeviceregistry-service
 */
public final class EndDeviceClient {

    private static final Logger LOG = LoggerFactory.getLogger(EndDeviceClient.class);

    private final IEndDeviceRegistryApi restApi;
    private final String applicationId;
    private final String authToken;

    EndDeviceClient(IEndDeviceRegistryApi restApi, String applicationId, String apiKey) {
        this.restApi = restApi;
        this.applicationId = applicationId;
        this.authToken = "Bearer " + apiKey;
    }

    public EndDevice buildEndDevice(String deviceId) {
        return new EndDevice(applicationId, deviceId);
    }

    public static EndDeviceClient create(String url, Duration timeout, String applicationId, String key) {
        LOG.info("Creating new REST client for '{}' with timeout {}", url, timeout);
        OkHttpClient client = new OkHttpClient().newBuilder().connectTimeout(timeout).readTimeout(timeout)
                .writeTimeout(timeout).build();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(JacksonConverterFactory.create()).client(client).build();
        IEndDeviceRegistryApi restApi = retrofit.create(IEndDeviceRegistryApi.class);
        return new EndDeviceClient(restApi, applicationId, key);
    }

    public EndDevice getEndDevice(String deviceId, String... fields) throws IOException {
        String fieldMask = String.join(",", fields);
        Response<EndDevice> response = restApi.getEndDevice(authToken, applicationId, deviceId, fieldMask).execute();
        if (!response.isSuccessful()) {
            LOG.warn("getEndDevice failed: {} - {}", response.message(), response.errorBody().string());
        }
        return response.body();
    }

    public EndDevice getNsEndDevice(String deviceId, String... fields) throws IOException {
        String fieldMask = String.join(",", fields);
        Response<EndDevice> response = restApi.getNsEndDevice(authToken, applicationId, deviceId, fieldMask).execute();
        if (!response.isSuccessful()) {
            LOG.warn("getNsEndDevice failed: {} - {}", response.message(), response.errorBody().string());
        }
        return response.body();
    }

    public List<EndDevice> listEndDevices(String... fields) throws IOException {
        String fieldMask = String.join(",", fields);
        Response<EndDevices> response = restApi.listEndDevices(authToken, applicationId, fieldMask).execute();
        if (!response.isSuccessful()) {
            LOG.warn("listEndDevices failed: {} - {}", response.message(), response.errorBody().string());
            return Collections.emptyList();
        }
        EndDevices endDevices = response.body();
        return endDevices.getEndDevices();
    }

    public EndDevice updateEndDevice(EndDevice endDevice, List<String> fields) throws IOException {
        FieldMask fieldMask = new FieldMask(fields);
        UpdateEndDeviceRequest updateEndDeviceRequest = new UpdateEndDeviceRequest(endDevice, fieldMask);
        Response<EndDevice> response = restApi
                .updateEndDevice(authToken, applicationId, endDevice.getIds().getDeviceId(), updateEndDeviceRequest)
                .execute();
        if (!response.isSuccessful()) {
            LOG.warn("updateEndDevice failed: {} - {}", response.message(), response.errorBody().string());
        }
        return response.body();
    }

}
