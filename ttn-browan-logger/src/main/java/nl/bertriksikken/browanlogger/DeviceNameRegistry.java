package nl.bertriksikken.browanlogger;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.bertriksikken.ttn.enddevice.EndDevice;
import nl.bertriksikken.ttn.enddevice.EndDeviceClient;
import nl.bertriksikken.ttn.enddevice.IEndDeviceRegistryApi;

/**
 * Fetches device names for a TTN application on a regular interval
 */
final class DeviceNameRegistry {

    private static final Logger LOG = LoggerFactory.getLogger(DeviceNameRegistry.class);

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final Map<String, EndDevice> endDeviceMap = new ConcurrentHashMap<>();
    private final EndDeviceClient client;

    DeviceNameRegistry(String url, Duration timeout, String appName, String appKey) {
        client = EndDeviceClient.create(url, timeout, appName, appKey);
    }

    @SuppressWarnings("FutureReturnValueIgnored")
    public void start() {
        LOG.info("Starting TTN device name registry");
        executor.scheduleAtFixedRate(this::fetchDeviceNames, 0, 60, TimeUnit.MINUTES);
    }

    public void stop() {
        executor.shutdownNow();
        LOG.info("Stopped TTN device name registry");
    }

    public String getDeviceName(String deviceId) {
        EndDevice endDevice = endDeviceMap.getOrDefault(deviceId, null);
        return endDevice != null ? endDevice.getName() : "";
    }

    private void fetchDeviceNames() {
        LOG.info("Fetching end device information");
        try {
            List<EndDevice> endDevices = client.listEndDevices(IEndDeviceRegistryApi.FIELD_NAME);
            endDevices.forEach(device -> endDeviceMap.put(device.getDeviceId(), device));
            LOG.info("Got device information for {} devices", endDevices.size());
        } catch (IOException e) {
            LOG.warn("Failed to list end devices", e);
        }
    }
}
