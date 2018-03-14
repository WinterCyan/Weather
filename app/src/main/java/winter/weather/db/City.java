package winter.weather.db;

/**
 * Created by winter on 3/12/2018.
 */

public class City {
    private int id;
    private String cityName;
    private int cityCode;
    private int porvinceId;

    public void setId(int id) {
        this.id = id;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityCode(int cityCode) {
        this.cityCode = cityCode;
    }

    public void setPorvinceId(int porvinceId) {
        this.porvinceId = porvinceId;
    }

    public int getId() {

        return id;
    }

    public String getCityName() {
        return cityName;
    }

    public int getCityCode() {
        return cityCode;
    }

    public int getPorvinceId() {
        return porvinceId;
    }
}
