package sysint;

import com.weather.*;

public class CachedForecaster implements WeatherService {
    private final WeatherService delegate;
    private final int maxSize;

    public CachedForecaster(WeatherService delegate, int maxSize) {
        this.delegate = delegate;
        this.maxSize = maxSize;
    }

    @Override
    public Forecast forecastFor(Region region, Day day) {
        return delegate.forecastFor(region, day);
    }
}