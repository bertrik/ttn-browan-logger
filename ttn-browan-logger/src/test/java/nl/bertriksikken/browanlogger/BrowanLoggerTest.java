package nl.bertriksikken.browanlogger;

import org.junit.Test;

public final class BrowanLoggerTest {

    @Test
    public void testCreateStop() {
        BrowanLoggerConfig config = new BrowanLoggerConfig();
        BrowanLogger logger = new BrowanLogger(config);
        logger.stop();
    }

}
