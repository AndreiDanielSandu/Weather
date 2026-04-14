package sysint;

import com.weather.Day;
import com.weather.Forecast;
import com.weather.Region;

public interface WeatherService {
    Forecast forecastFor(Region region, Day day);
}