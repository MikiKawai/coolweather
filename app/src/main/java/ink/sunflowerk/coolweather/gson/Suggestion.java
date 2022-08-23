package ink.sunflowerk.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Author zhaokuii11@163.com
 * @Date 2022-08-22 14:06
 * @Description
 */
public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;
    @SerializedName("cw")
    public CarWash carWash;
    public Sport sport;

    public class Comfort {
        @SerializedName("txt")
        public String info;
    }

    public class CarWash {
        @SerializedName("txt")
        public String info;
    }

    public class Sport {
        @SerializedName("txt")
        public String info;
    }
}
