package winter.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by winter on 3/16/2018.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
