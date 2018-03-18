package winter.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by winter on 3/16/2018.
 */

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
