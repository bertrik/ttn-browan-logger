package nl.bertriksikken.browanlogger;

import org.junit.jupiter.api.Test;

public final class BrowanLoggerTest {

    @Test
    public void testCreateStop() {
        BrowanLoggerConfig config = new BrowanLoggerConfig();
        BrowanLogger logger = new BrowanLogger(config);
        logger.stop();
    }

}
