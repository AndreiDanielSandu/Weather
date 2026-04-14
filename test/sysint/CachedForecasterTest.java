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
    private final Clock clock = context.mock(Clock.class);
    private final CachedForecaster cache = new CachedForecaster(realService, clock, 5);

    @Test
    public void callsRealServiceWhenCacheIsEmpty() {
        context.checking(new Expectations() {{
            ignoring(clock);

            oneOf(realService).forecastFor(Region.LONDON, Day.MONDAY);
        }});

        cache.forecastFor(Region.LONDON, Day.MONDAY);
    }

    @Test
    public void doesNotCallRealServiceTwiceForSameRequest() {
        context.checking(new Expectations() {{
            allowing(clock).currentTimeMillis(); will(returnValue(1000L));

            oneOf(realService).forecastFor(Region.LONDON, Day.MONDAY);
        }});

        cache.forecastFor(Region.LONDON, Day.MONDAY);
        cache.forecastFor(Region.LONDON, Day.MONDAY);
    }

    @Test
    public void refreshesCacheAfterOneHour() {
        final org.jmock.States timePhase = context.states("timePhase").startsAs("initial");

        context.checking(new Expectations() {{
            allowing(clock).currentTimeMillis(); will(returnValue(0L)); when(timePhase.is("initial"));
            oneOf(realService).forecastFor(Region.LONDON, Day.MONDAY); when(timePhase.is("initial"));

            allowing(clock).currentTimeMillis(); will(returnValue(4000000L)); when(timePhase.is("expired"));
            oneOf(realService).forecastFor(Region.LONDON, Day.MONDAY); when(timePhase.is("expired"));
        }});

        cache.forecastFor(Region.LONDON, Day.MONDAY);

        timePhase.become("expired");

        cache.forecastFor(Region.LONDON, Day.MONDAY);
    }
}