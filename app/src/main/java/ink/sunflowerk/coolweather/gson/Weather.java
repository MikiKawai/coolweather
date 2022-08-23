package ink.sunflowerk.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @Author zhaokuii11@163.com
 * @Date 2022-08-22 14:12
 * @Description
 */
public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;
    @SerializedName("daily_forecast")
    public List<ForeCast> foreCastsList;
}
