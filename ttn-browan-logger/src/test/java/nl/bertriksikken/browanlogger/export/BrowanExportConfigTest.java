package nl.bertriksikken.browanlogger.export;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.temporal.WeekFields;

public final class BrowanExportConfigTest {

    /**
     * Verify that default config has its first weekday on monday.
     */
    @Test
    public void testFirstDayOfWeek() {
        BrowanExportConfig config = new BrowanExportConfig();
        DayOfWeek firstDayOfWeek = WeekFields.of(config.getLocale()).getFirstDayOfWeek();
        Assertions.assertEquals(DayOfWeek.MONDAY, firstDayOfWeek);
    }

}
