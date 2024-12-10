package nl.bertriksikken.browanlogger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public final class BrowanLoggerConfigTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
        YAMLMapper mapper = new YAMLMapper();
        BrowanLoggerConfig config = new BrowanLoggerConfig();
        String yaml = mapper.writeValueAsString(config);
        Assertions.assertNotNull(yaml);
    }
}
