package sysint;

import com.weather.*;
import org.jmock.Expectations;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.junit.Rule;
import org.junit.Test;

public class CachedForecasterTest {
    @Rule
    public JUnitRuleMockery context = new JUnitRuleMockery();

    private final WeatherService realService = context.mock(WeatherService.class);
    private final CachedForecaster cache = new CachedForecaster(realService, 5);

    @Test
    public void callsRealServiceWhenCacheIsEmpty() {
        context.checking(new Expectations() {{
            oneOf(realService).forecastFor(Region.LONDON, Day.MONDAY);
        }});

        cache.forecastFor(Region.LONDON, Day.MONDAY);
    }
}