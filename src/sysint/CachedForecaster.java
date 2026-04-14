package sysint;

import com.weather.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CachedForecaster implements WeatherService {
    private final WeatherService delegate;
    private final Map<CacheKey, CachedValue> cacheMap;
    private final Clock clock;

    public CachedForecaster(WeatherService delegate, Clock clock, final int maxSize) {
        this.delegate = delegate;
        this.clock = clock;
        this.cacheMap = new LinkedHashMap<CacheKey, CachedValue>(maxSize, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<CacheKey, CachedValue> eldest) {
                return size() > maxSize;
            }
        };
    }

    @Override
    public Forecast forecastFor(Region region, Day day) {
        CacheKey key = new CacheKey(region, day);
        CachedValue cached = cacheMap.get(key);

        if (cached != null && (clock.currentTimeMillis() - cached.timestamp) < 3600000) {
            return cached.forecast;
        }

        Forecast freshForecast = delegate.forecastFor(region, day);
        cacheMap.put(key, new CachedValue(freshForecast, clock.currentTimeMillis()));

        return freshForecast;
    }

    private static class CachedValue {
        public final Forecast forecast;
        public final long timestamp;

        public CachedValue(Forecast forecast, long timestamp) {
            this.forecast = forecast;
            this.timestamp = timestamp;
        }
    }

    private static class CacheKey {
        private final Region region;
        private final Day day;

        public CacheKey(Region region, Day day) {
            this.region = region;
            this.day = day;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CacheKey cacheKey = (CacheKey) o;
            return region == cacheKey.region && day == cacheKey.day;
        }

        @Override
        public int hashCode() {
            return Objects.hash(region, day);
        }
    }
}