package winter.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by winter on 3/12/2018.
 */

public class County extends DataSupport {
    private int id;
    private String countyName;
    private int weatherId;
    private int cityId;

    public void setId(int id) {
        this.id = id;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherId(int weatherId) {
        this.weatherId = weatherId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getId() {

        return id;
    }

    public String getCountyName() {
        return countyName;
    }

    public int getWeatherId() {
        return weatherId;
    }

    public int getCityId() {
        return cityId;
    }
}
