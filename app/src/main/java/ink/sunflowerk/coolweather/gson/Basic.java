package ink.sunflowerk.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Author zhaokuii11@163.com
 * @Date 2022-08-22 14:00
 * @Description
 */
public class Basic {
    @SerializedName("city")
    public String cityName;
    @SerializedName("id")
    public String weatherId;
    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
