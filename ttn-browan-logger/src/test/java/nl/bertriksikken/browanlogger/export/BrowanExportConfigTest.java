package nl.bertriksikken.browanlogger.export;

import java.time.DayOfWeek;
import java.time.temporal.WeekFields;

import org.junit.Assert;
import org.junit.Test;

public final class BrowanExportConfigTest {

    /**
     * Verify that default config has its first weekday on monday.
     */
    @Test
    public void testFirstDayOfWeek() {
        BrowanExportConfig config = new BrowanExportConfig();
        DayOfWeek firstDayOfWeek = WeekFields.of(config.getLocale()).getFirstDayOfWeek();
        Assert.assertEquals(DayOfWeek.MONDAY, firstDayOfWeek);
    }

}
