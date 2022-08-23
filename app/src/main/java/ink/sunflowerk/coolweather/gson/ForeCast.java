package ink.sunflowerk.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * @Author zhaokuii11@163.com
 * @Date 2022-08-22 14:09
 * @Description
 */
public class ForeCast {
    public String date;
    @SerializedName("tmp")
    public Temperature temperature;
    @SerializedName("cond")
    public More more;

    public class Temperature {

        public String max;
        public String min;
    }

    public class More {
        @SerializedName("txt_d")
        public String info;
    }
}
