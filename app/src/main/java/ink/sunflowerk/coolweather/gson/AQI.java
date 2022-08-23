package ink.sunflowerk.coolweather.gson;

/**
 * @Author zhaokuii11@163.com
 * @Date 2022-08-22 14:03
 * @Description
 */
public class AQI {
    public AQICity city;

    public class AQICity {
        public String api;
        public String pm25;
    }
}
