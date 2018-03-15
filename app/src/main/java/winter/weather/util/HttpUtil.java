package winter.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by winter on 3/14/2018.
 */

public class HttpUtil {
    public static void sendHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
}
