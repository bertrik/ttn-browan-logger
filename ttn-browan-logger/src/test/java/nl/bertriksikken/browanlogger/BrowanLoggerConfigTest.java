package nl.bertriksikken.browanlogger;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public final class BrowanLoggerConfigTest {

    @Test
    public void testSerialize() throws JsonProcessingException {
        YAMLMapper mapper = new YAMLMapper();
        BrowanLoggerConfig config = new BrowanLoggerConfig();
        String yaml = mapper.writeValueAsString(config);
        Assert.assertNotNull(yaml);
    }
}
