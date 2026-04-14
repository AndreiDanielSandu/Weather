package sysint;

import com.weather.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CachedForecaster implements WeatherService {
    private final WeatherService delegate;
    private final Map<CacheKey, Forecast> cacheMap;

    public CachedForecaster(WeatherService delegate, final int maxSize) {
        this.delegate = delegate;
        // LinkedHashMap with 'removeEldestEntry' handles the size limit for us!
        this.cacheMap = new LinkedHashMap<CacheKey, Forecast>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<CacheKey, Forecast> eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public Forecast forecastFor(Region region, Day day) {
        CacheKey key = new CacheKey(region, day);
        if (cacheMap.containsKey(key)) {
            return cacheMap.get(key);
        }
        Forecast forecast = delegate.forecastFor(region, day);
        cacheMap.put(key, forecast);
        return forecast;
    }

    private static record CacheKey(Region region, Day day) {}
}