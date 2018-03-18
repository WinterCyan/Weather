package winter.weather.gson;

/**
 * Created by winter on 3/16/2018.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
